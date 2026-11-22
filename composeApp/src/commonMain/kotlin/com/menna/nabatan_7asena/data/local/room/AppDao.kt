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
    @Query("SELECT * FROM daily_rewards WHERE profileId = :profileId AND day >= :day ORDER BY day DESC")
    fun observeRewards(profileId: String, day: String): Flow<List<DailyRewardEntity>>

    @Query("SELECT * FROM daily_rewards WHERE profileId = :profileId AND synced = 0 ORDER BY day")
    suspend fun pendingRewards(profileId: String): List<DailyRewardEntity>

    @Query("SELECT * FROM daily_rewards WHERE profileId = :profileId AND day = :day AND kind = 'wheel'")
    suspend fun wheelReward(profileId: String, day: String): DailyRewardEntity?

    @Query("SELECT itemId FROM daily_records WHERE date = :date AND isCompleted = 1")
    suspend fun completedGiftTaskIds(date: String): List<Int>

    @Query("SELECT id FROM extra_tasks WHERE date = :date")
    suspend fun extraGiftTaskIds(date: String): List<Int>

    @Query("SELECT userId FROM user_stats WHERE id = 1")
    suspend fun activeRewardProfileId(): String?

    private suspend fun giftTasksCompleted(profileId: String, date: String): Boolean {
        if (activeRewardProfileId().orEmpty().ifBlank { "local" } != profileId) return false
        return missingDailyGiftTasks(extraGiftTaskIds(date), completedGiftTaskIds(date).toSet()) == 0
    }

    @Transaction
    suspend fun reserveDailyGift(reward: DailyRewardEntity, taskDate: String): Boolean =
        giftTasksCompleted(reward.profileId, taskDate) && claimReward(reward)

    @Transaction
    suspend fun claimDailyGiftGame(reward: DailyRewardEntity, taskDate: String): Boolean {
        if (!giftTasksCompleted(reward.profileId, taskDate)) return false
        val wheel = wheelReward(reward.profileId, reward.day)?.takeIf { it.settled } ?: return false
        if (WheelPrize.fromStoredCode(wheel.prize)?.game?.name != reward.kind) return false
        return claimReward(reward)
    }

    @Query("SELECT MAX(day) FROM daily_rewards WHERE profileId = :profileId AND kind = :kind")
    suspend fun latestRewardDate(profileId: String, kind: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReward(reward: DailyRewardEntity): Long

    @Query("""
        UPDATE daily_rewards SET prize = :prize, stars = :stars, settled = 1, earnedHere = :earnedHere, synced = :synced
        WHERE profileId = :profileId AND day = :day AND kind = :kind AND claimId = :claimId AND settled = 0
    """)
    suspend fun setRewardSettlement(
        profileId: String, day: String, kind: String, claimId: String,
        prize: String, stars: Int, earnedHere: Boolean, synced: Boolean
    ): Int

    @Query("UPDATE daily_rewards SET synced = 1 WHERE profileId = :profileId AND day = :day AND kind = :kind")
    suspend fun markRewardSynced(profileId: String, day: String, kind: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun ensureStats(stats: UserStatsEntity): Long

    @Query("""
        UPDATE user_stats SET totalStars = MIN(2147483647, totalStars + :stars),
        treeStage = CASE WHEN :stars = 0 THEN treeStage
            WHEN totalStars + :stars < 50 THEN 1 WHEN totalStars + :stars < 200 THEN 2 ELSE 3 END
        WHERE id = 1 AND userId = :profileId
    """)
    suspend fun creditRewardStars(profileId: String, stars: Int): Int

    @Transaction
    suspend fun settleReward(reward: DailyRewardEntity): Boolean {
        val updated = setRewardSettlement(
            reward.profileId, reward.day, reward.kind, reward.claimId,
            reward.prize, reward.stars, reward.earnedHere, reward.synced
        )
        if (updated == 0) return false
        creditRewardBalance(reward)
        return true
    }

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

    @Transaction
    suspend fun claimReward(reward: DailyRewardEntity): Boolean {
        val latest = latestRewardDate(reward.profileId, reward.kind)
        if (latest != null && latest >= reward.day) return false
        if (insertReward(reward) == -1L) return false
        if (reward.settled) creditRewardBalance(reward)
        return true
    }

    private suspend fun creditRewardBalance(reward: DailyRewardEntity) {
        val storedId = if (reward.profileId == "local") "" else reward.profileId
        if (storedId.isEmpty()) ensureStats(UserStatsEntity(name = "بطل"))
        check(creditRewardStars(storedId, reward.stars) == 1) { "Profile changed" }
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
