package com.menna.nabatan_7asena.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.menna.nabatan_7asena.domain.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.serialization.encodeToString

@Dao
interface AppDao {
    @Query("SELECT COUNT(*) FROM daily_records WHERE itemId = :itemId AND isCompleted = 1")
    fun observeCompletedDays(itemId: Int): Flow<Int>

    @Query("SELECT isCompleted FROM daily_records WHERE date = :date AND itemId = :itemId")
    suspend fun getRecordStatus(date: String, itemId: Int): Boolean?

    @Transaction
    suspend fun awardPrayerGuideBonus(date: String, userId: String): Boolean {
        val stats = checkNotNull(readUserStats()) { "A saved profile is required" }
        check(stats.userId == userId)
        if (getRecordStatus(date, PRAYER_GUIDE_REWARD_ID) == true) return false
        updateUserStats(stats.withBonus(PRAYER_GUIDE_BONUS_STARS))
        updateOrInsertRecord(DailyRecordEntity(date, PRAYER_GUIDE_REWARD_ID, true))
        return true
    }

    @Query("SELECT -100 - itemId FROM daily_records WHERE date = :seasonKey AND itemId BETWEEN -130 AND -101 AND isCompleted = 1 AND EXISTS (SELECT 1 FROM user_stats WHERE userId = :profileId)")
    fun observeRamadanDays(profileId: String, seasonKey: String): Flow<List<Int>>

    @Query("SELECT * FROM daily_records WHERE date GLOB 'ramadan-[0-9]*' AND itemId BETWEEN -130 AND -101 AND isCompleted = 1 AND EXISTS (SELECT 1 FROM user_stats WHERE userId = :profileId)")
    fun observeRamadanRecords(profileId: String): Flow<List<DailyRecordEntity>>

    @Transaction
    suspend fun completeRamadanDay(seasonKey: String, userId: String, day: Int, awardStars: Boolean) {
        val stats = readUserStats() ?: error("A saved profile is required")
        check(stats.userId == userId)
        val rewardId = -100 - day
        if (getRecordStatus(seasonKey, rewardId) == true) return
        if (awardStars) {
            val stars = stats.totalStars + RAMADAN_CHALLENGE_STARS
            updateUserStats(stats.copy(totalStars = stars, treeStage = treeStageFor(stars)))
        }
        updateOrInsertRecord(DailyRecordEntity(seasonKey, rewardId, true))
    }

    @Transaction
    suspend fun saveActivity(date: String, itemId: Int, completed: Boolean, today: LocalDate, yesterday: LocalDate) {
        if ((getRecordStatus(date, itemId) ?: false) == completed) return
        updateOrInsertRecord(DailyRecordEntity(date, itemId, completed))
        val stats = readUserStats() ?: UserStatsEntity()
        insertUserStats(stats.withDailyActivity(completed, today, yesterday))
    }


    @Transaction
    suspend fun replaceProfile(stats: UserStatsEntity) {
        clearAllRecords()
        insertUserStats(stats)
    }

    @Query("UPDATE user_stats SET name = :name WHERE id = 1")
    suspend fun updateName(name: String)

    @Query("UPDATE user_stats SET prayerNotifications = :enabled, azkarNotifications = :enabled, quranNotifications = :enabled WHERE id = 1")
    suspend fun updateAllNotifications(enabled: Boolean)

    @Query("UPDATE user_stats SET quranPartsFinished = quranPartsFinished + :change WHERE id = 1 AND quranPartsFinished + :change >= 0")
    suspend fun changeQuranParts(change: Int): Int

    @Query("SELECT * FROM daily_records WHERE date = :date")
    fun getRecordsForDate(date: String): Flow<List<DailyRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertRecord(record: DailyRecordEntity)

    @Query("DELETE FROM daily_records")
    suspend fun clearAllRecords()


    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    @androidx.room.Update
    suspend fun updateUserStats(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET avatarIndex = :avatarIndex WHERE id = 1 AND userId = :userId AND gender = :gender")
    suspend fun updateProfileImage(userId: String, gender: String, avatarIndex: Int): Int

    @Query("DELETE FROM user_stats")
    suspend fun clearUserStats()

    @Query("SELECT lastQuranPage FROM user_stats WHERE id = 1")
    fun getLastQuranPage(): Flow<Int>

    @Query("UPDATE user_stats SET lastQuranPage = :page WHERE id = 1")
    suspend fun updateQuranPage(page: Int)


    @Query("SELECT * FROM daily_plans WHERE dayId = :id")
    suspend fun getPlanForDay(id: Int): DailyPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DailyPlanEntity)

    @Insert
    suspend fun insertExtraTask(task: ExtraTaskEntity)

    @Query("SELECT * FROM extra_tasks WHERE date = :date")
    fun getExtraTasksForDate(date: String): Flow<List<ExtraTaskEntity>>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStatsEntity?>

    @Query("UPDATE user_stats SET prayerNotifications = :enabled WHERE id = 1")
    suspend fun updatePrayerNotif(enabled: Boolean)

    @Query("UPDATE user_stats SET azkarNotifications = :enabled WHERE id = 1")
    suspend fun updateAzkarNotif(enabled: Boolean)

    @Query("UPDATE user_stats SET quranNotifications = :enabled WHERE id = 1")
    suspend fun updateQuranNotif(enabled: Boolean)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun readUserStats(): UserStatsEntity?

    @Query("SELECT * FROM quran_journey WHERE profileId = :profileId")
    fun observeQuranJourney(profileId: String): Flow<QuranJourneyEntity?>

    @Query("SELECT * FROM quran_journey WHERE profileId = :profileId")
    suspend fun readQuranJourney(profileId: String): QuranJourneyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeQuranJourney(entity: QuranJourneyEntity)

    @Transaction
    suspend fun readQuranProfile(profileId: String): Pair<UserStatsEntity?, QuranJourneyEntity?> =
        readUserStats()?.takeIf { it.userId == profileId } to readQuranJourney(profileId)

    @Query("UPDATE quran_journey SET syncedVersion = :version WHERE profileId = :profileId AND syncVersion = :version")
    suspend fun markQuranJourneySynced(profileId: String, version: Int)

    @Query("DELETE FROM quran_journey WHERE profileId = :profileId")
    suspend fun clearQuranJourney(profileId: String)

    @Transaction
    suspend fun clearProfileProgress(): String? {
        val profileId = readUserStats()?.userId
        val syncedProfileId = profileId?.takeIf { readQuranJourney(it)?.syncVersion?.let { version -> version > 0 } == true }
        if (profileId != null) clearQuranJourney(profileId)
        clearAllRecords()
        clearUserStats()
        return syncedProfileId
    }

    @Transaction
    suspend fun changeQuranJourney(profileId: String, change: (QuranJourney) -> QuranJourney) {
        requireActiveProfile(profileId)
        val stored = readQuranJourney(profileId)
        val previous = stored?.toJourney() ?: QuranJourney()
        val next = change(previous)
        if (previous == next) return
        persistJourney(profileId, stored, previous, next)
    }

    @Transaction
    suspend fun completeQuranWird(profileId: String, date: LocalDate, expected: Wird,
                                  taskDate: String) {
        val user = requireActiveProfile(profileId)
        val stored = readQuranJourney(profileId) ?: return
        val previous = stored.toJourney()
        val next = previous.completeWird(date, expected)
        if (previous == next) return
        persistJourney(profileId, stored, previous, next)
        var updated = user
        if (getRecordStatus(taskDate, DAILY_QURAN_TASK_ID) != true) {
            updateOrInsertRecord(DailyRecordEntity(taskDate, DAILY_QURAN_TASK_ID, true))
            updated = user.withDailyActivity(true, date, date.minus(1, DateTimeUnit.DAY))
        }
        if (next.completedKhatmahs > previous.completedKhatmahs) {
            updated = updated.withBonus(KHATMAH_BONUS_STARS)
        }
        if (updated != user) updateUserStats(updated)
    }

    @Transaction
    suspend fun recordSurahMemorization(profileId: String, surahId: Int, status: MemorizationStatus,
                                       ayahs: Int, totalAyahs: Int) {
        val user = requireActiveProfile(profileId)
        val stored = readQuranJourney(profileId)
        val previous = stored?.toJourney() ?: QuranJourney()
        val next = previous.updateSurah(surahId, status, ayahs, totalAyahs)
        if (previous == next) return
        persistJourney(profileId, stored, previous, next)
        if (previous.memorization[surahId]?.bonusAwarded != true &&
            next.memorization[surahId]?.bonusAwarded == true
        ) updateUserStats(user.withBonus(MEMORIZATION_BONUS_STARS))
    }

    private suspend fun requireActiveProfile(profileId: String): UserStatsEntity =
        checkNotNull(readUserStats()?.takeIf { it.userId == profileId && profileId.isNotBlank() })

    private suspend fun persistJourney(
        profileId: String, stored: QuranJourneyEntity?, previous: QuranJourney, next: QuranJourney
    ) {
        val syncNeeded = previous.completedKhatmahs != next.completedKhatmahs ||
            previous.memorizedSurahs != next.memorizedSurahs
        writeQuranJourney(QuranJourneyEntity(
            profileId, QuranJourneyEntity.json.encodeToString(next),
            (stored?.syncVersion ?: 0) + if (syncNeeded) 1 else 0,
            stored?.syncedVersion ?: 0
        ))
    }
}

// Daily activity IDs are positive.
private const val PRAYER_GUIDE_REWARD_ID = -1
