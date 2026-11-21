package com.menna.nabatan_7asena.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM daily_rewards WHERE profileId = :profileId AND day >= :day ORDER BY day DESC")
    fun observeRewards(profileId: String, day: String): Flow<List<DailyRewardEntity>>

    @Query("SELECT * FROM daily_rewards WHERE profileId = :profileId AND synced = 0 ORDER BY day")
    suspend fun pendingRewards(profileId: String): List<DailyRewardEntity>

    @Query("SELECT MAX(day) FROM daily_rewards WHERE profileId = :profileId AND kind = :kind")
    suspend fun latestRewardDate(profileId: String, kind: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReward(reward: DailyRewardEntity): Long

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
    suspend fun claimReward(reward: DailyRewardEntity): Boolean {
        val latest = latestRewardDate(reward.profileId, reward.kind)
        if (latest != null && latest >= reward.day) return false
        val storedId = if (reward.profileId == "local") "" else reward.profileId
        if (storedId.isEmpty()) ensureStats(UserStatsEntity(name = "بطل"))
        if (insertReward(reward) == -1L) return false
        check(creditRewardStars(storedId, reward.stars) == 1) { "Profile changed" }
        return true
    }

    @Query("""
        UPDATE user_stats SET totalStars = MIN(2147483647, MAX(0, totalStars + :stars)),
        treeStage = CASE WHEN totalStars + :stars < 50 THEN 1 WHEN totalStars + :stars < 200 THEN 2 ELSE 3 END,
        tasksCompletedTotal = MAX(0, tasksCompletedTotal + :tasks),
        currentStreak = :streak, lastActiveDate = :date
        WHERE id = 1 AND userId = :userId
    """)
    suspend fun applyTaskProgress(userId: String, stars: Int, tasks: Int, streak: Int, date: String): Int

    @Query("UPDATE user_stats SET name = :name WHERE id = 1 AND userId = :userId")
    suspend fun updateProfileName(userId: String, name: String)

    @Query("UPDATE user_stats SET prayerNotifications = :enabled, azkarNotifications = :enabled, quranNotifications = :enabled WHERE id = 1")
    suspend fun updateAllNotifications(enabled: Boolean)

    @Query("UPDATE user_stats SET quranPartsFinished = MAX(0, quranPartsFinished + :change) WHERE id = 1 AND userId = :userId")
    suspend fun adjustQuranParts(userId: String, change: Int)


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
}