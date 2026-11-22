package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.platform.Log
import com.menna.nabatan_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.UserStatsEntity
import com.menna.nabatan_7asena.data.mappers.toDomain
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.platform.SchedulingPlatform
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl constructor(
    private val dao: AppDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val globalAlarmManager: GlobalAlarmManager,
    private val scheduling: SchedulingPlatform
) : UserRepository {


    private val scoreSync = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentUser(): Flow<User> = dao.getUserStats().flatMapLatest { entity ->
        val user = entity?.toDomain() ?: User(
            id = "local", name = "بطل", gender = User.Gender.BOY,
            totalStars = 0, currentStreak = 0, treeStage = 1
        )
        if (entity == null || entity.userId.isBlank()) flowOf(user)
        else dao.observeQuranJourney(entity.userId).map {
            user.copy(completedKhatmahs = it?.toJourney()?.completedKhatmahs ?: 0)
        }
    }.distinctUntilChanged()

    override suspend fun registerUser(user: User) {

        val entity = UserStatsEntity(
            id = 1,
            userId = user.id,
            name = user.name,
            gender = user.gender.name,
            totalStars = user.totalStars,
            treeStage = user.treeStage,
            currentStreak = user.currentStreak,
            quranPartsFinished = user.quranPartsFinished,
            avatarIndex = user.avatarIndex
        )
        dao.replaceProfile(entity)

    }

    override fun getLeaderboardResource(): Flow<Resource<List<LeaderboardEntry>>> =
        flow {
            emit(Resource.Loading)
            try {
                val leaderboard = firebaseDataSource.getLeaderboard().mapIndexed { index, dto ->
                    LeaderboardEntry(
                        userId = dto.userId,
                        name = dto.name,
                        gender = if (dto.gender.equals(
                                "boy",
                                true
                            )
                        ) User.Gender.BOY else User.Gender.GIRL,
                        totalStars = dto.totalStars,
                        rank = index + 1,
                        completedKhatmahs = dto.completedKhatmahs,
                        avatarIndex = dto.avatarIndex
                    )
                }
                emit(Resource.Success(leaderboard))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("UserRepositoryImpl", "failed to fetch leaderboard", e)
                emit(Resource.Error(e, e.message ?: "Failed to fetch leaderboard"))
            }
        }

    override suspend fun getUserRank(totalStars: Int): Resource<Long> = try {
        Resource.Success(firebaseDataSource.getUserRank(totalStars))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("UserRepositoryImpl", "Failed to aggregate leaderboard rank", e)
        Resource.Error(e)
    }

    override suspend fun updateNotificationPreference(category: String, enabled: Boolean) {
        val stats = dao.getUserStats().firstOrNull()
        stats?.let {
            when (category) {
                "prayer" -> {
                    dao.updatePrayerNotif(enabled)
                    if (!enabled) globalAlarmManager.cancelPrayerAlarms()
                }

                "azkar" -> {
                    dao.updateAzkarNotif(enabled)
                    if (!enabled) globalAlarmManager.cancelAzkarAlarms()
                }

                "quran" -> {
                    dao.updateQuranNotif(enabled)
                    if (!enabled) globalAlarmManager.cancelQuranAlarms()
                }
            }
        }
    }

    suspend fun syncQuranSummary(profileId: String) = scoreSync.withLock {
        val (profile, stored) = dao.readQuranProfile(profileId)
        if (profile == null || stored == null || stored.syncVersion == stored.syncedVersion) return@withLock
        val journey = stored.toJourney()
        firebaseDataSource.backupQuranSummary(profileId, journey.completedKhatmahs, journey.memorizedSurahs)
        dao.markQuranJourneySynced(profileId, stored.syncVersion)
    }

    override suspend fun updateProfileImage(userId: String, gender: User.Gender, avatarIndex: Int) {
        check(dao.updateProfileImage(userId, gender.name, avatarIndex) == 1) {
            "The saved profile changed or no longer exists"
        }
    }

    override suspend fun updateUserName(newName: String) {
        dao.updateName(newName)
    }

    override suspend fun resetAllProgress() {
        try {
            scoreSync.withLock {
                val profileId = dao.clearProfileProgress()
                if (!profileId.isNullOrBlank()) {
                    try {
                        withTimeoutOrNull(7_000) { firebaseDataSource.deleteQuranSummary(profileId) }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e("UserRepositoryImpl", "failed to delete Quran summary from Firebase", e)
                    }
                }
            }
            scheduling.cancelDailyAutoScheduling()
            globalAlarmManager.cancelAllAlarms()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "failed to reset all progress", e)
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dao.updateAllNotifications(enabled)

        if (enabled) scheduling.enableDailyAutoScheduling(replace = true) else {
            scheduling.cancelDailyAutoScheduling()
            globalAlarmManager.cancelAllAlarms()
        }
    }

    override suspend fun incrementQuranPart(): Resource<Unit> = changeQuranParts(1)

    override suspend fun decrementQuranPart(): Resource<Unit> = changeQuranParts(-1)

    private suspend fun changeQuranParts(change: Int): Resource<Unit> = try {
        if (dao.changeQuranParts(change) == 1) Resource.Success(Unit)
        else Resource.Error(IllegalStateException("No saved Quran progress to update"))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("UserRepositoryImpl", "Failed to update Quran progress", e)
        Resource.Error(e)
    }
}
