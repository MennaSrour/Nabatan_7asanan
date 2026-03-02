package com.menna.nabata_7asena.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.menna.nabata_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabata_7asena.core.worker.DailyManagerWorker
import com.menna.nabata_7asena.data.local.room.AppDao
import com.menna.nabata_7asena.data.local.room.UserStatsEntity
import com.menna.nabata_7asena.data.mappers.toDomain
import com.menna.nabata_7asena.data.remote.FirebaseDataSource
import com.menna.nabata_7asena.data.remote.dto.UserRankDto
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.LeaderboardEntry
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: AppDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val globalAlarmManager: GlobalAlarmManager,
    @param:ApplicationContext private val context: Context
) : UserRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun getCurrentUser(): Flow<User> {
        return dao.getUserStats().map { entity ->

            entity?.toDomain() ?: User(
                id = "local",
                name = "بطل",
                gender = User.Gender.BOY,
                totalStars = 0,
                currentStreak = 0,
                treeStage = 1
            )
        }
    }


    override suspend fun registerUser(user: User) {

        val entity = UserStatsEntity(
            id = 1,
            userId = user.id,
            name = user.name,
            gender = user.gender.name,
            totalStars = user.totalStars,
            treeStage = user.treeStage,
            currentStreak = user.currentStreak,
            quranPartsFinished = user.quranPartsFinished
        )
        dao.clearUserStats()
        dao.clearAllRecords()
        dao.insertUserStats(entity)


        try {
            val dto = UserRankDto(
                userId = user.id,
                name = user.name,
                gender = user.gender.name.lowercase(),
                totalStars = user.totalStars
            )
            firebaseDataSource.uploadUserScore(dto)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "failed to upload user score on register", e)
        }
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
                        quranPartsFinished = dto.quranPartsFinished
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

    override suspend fun syncUserScore(user: User) {
        try {
            val dto = UserRankDto(
                userId = user.id,
                name = user.name,
                gender = user.gender.name.lowercase(),
                totalStars = user.totalStars,
                quranPartsFinished = user.quranPartsFinished
            )
            firebaseDataSource.uploadUserScore(dto)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "failed to sync user score", e)
        }
    }

    override suspend fun updateUserName(newName: String) {
        val user = dao.getUserStats().firstOrNull()
        if (user != null) {
            val updatedUser = user.copy(name = newName)
            dao.updateUserStats(updatedUser)

            try {
                val dto = UserRankDto(
                    userId = user.userId,
                    name = newName,
                    gender = user.gender.lowercase(),
                    totalStars = user.totalStars
                )
                firebaseDataSource.uploadUserScore(dto)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("UserRepositoryImpl", "failed to upload updated user name", e)
            }
        }
    }

    override suspend fun resetAllProgress() {
        try {
            val user = dao.getUserStats().firstOrNull()


            if (user != null && user.userId.isNotEmpty()) {
                try {
                    firebaseDataSource.deleteUserScore(user.userId)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", "failed to delete user score from firebase", e)
                }
            }


            dao.clearAllRecords()
            dao.clearUserStats()


            workManager.cancelUniqueWork("daily_manager_work")


            globalAlarmManager.cancelAllAlarms()

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "failed to reset all progress", e)
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        val stats = dao.getUserStats().firstOrNull()
        stats?.let {
            val updatedStats = it.copy(
                prayerNotifications = enabled,
                azkarNotifications = enabled,
                quranNotifications = enabled
            )
            dao.updateUserStats(updatedStats)
        }

        if (enabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<DailyManagerWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                "daily_manager_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            workManager.cancelUniqueWork("daily_manager_work")
            globalAlarmManager.cancelAllAlarms()
        }
    }

    override suspend fun incrementQuranPart(): Resource<Unit> {
        return try {

            val userEntity = dao.getUserStats().firstOrNull()

            if (userEntity != null) {

                val updatedCount = userEntity.quranPartsFinished + 1
                val updatedEntity = userEntity.copy(quranPartsFinished = updatedCount)


                dao.updateUserStats(updatedEntity)


                try {

                    val dto = UserRankDto(
                        userId = userEntity.userId,
                        name = userEntity.name,
                        gender = userEntity.gender.lowercase(),
                        totalStars = userEntity.totalStars,
                        quranPartsFinished = updatedCount
                    )
                    firebaseDataSource.uploadUserScore(dto)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", "failed to upload user score after increment", e)
                }

                Resource.Success(Unit)
            } else {
                Resource.Error(Exception("User not found"), "User not found")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "error incrementing quran part", e)
            Resource.Error(e, e.message ?: "Error incrementing quran part")
        }
    }

    override suspend fun decrementQuranPart(): Resource<Unit> {
        return try {
            val userEntity = dao.getUserStats().firstOrNull()
            if (userEntity != null && userEntity.quranPartsFinished > 0) {
                val updatedCount = userEntity.quranPartsFinished - 1
                val updatedEntity = userEntity.copy(quranPartsFinished = updatedCount)

                dao.updateUserStats(updatedEntity)

                try {
                    val dto = UserRankDto(
                        userId = userEntity.userId,
                        name = userEntity.name,
                        gender = userEntity.gender.lowercase(),
                        totalStars = userEntity.totalStars,
                        quranPartsFinished = updatedCount
                    )
                    firebaseDataSource.uploadUserScore(dto)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", "failed to upload user score after decrement", e)
                }

                Resource.Success(Unit)
            } else {
                Resource.Error(
                    Exception("User not found or part already zero"),
                    "User not found or part already zero"
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "error decrementing quran part", e)
            Resource.Error(e, e.message ?: "Error decrementing quran part")
        }
    }
}