package com.menna.nabatan_7asena.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.first
import com.menna.nabatan_7asena.domain.entity.PRAYER_GUIDE_BONUS_STARS
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT COUNT(*) FROM daily_records WHERE itemId = :itemId AND isCompleted = 1")
    fun observeCompletedDays(itemId: Int): Flow<Int>

    @Query("SELECT isCompleted FROM daily_records WHERE date = :date AND itemId = :itemId")
    suspend fun getRecordStatus(date: String, itemId: Int): Boolean?

    @Transaction
    suspend fun awardPrayerGuideBonus(date: String, userId: String): Boolean {
        val stats = getUserStats().first() ?: error("A saved profile is required")
        check(stats.userId == userId)
        if (getRecordStatus(date, PRAYER_GUIDE_REWARD_ID) == true) return false
        val stars = stats.totalStars + PRAYER_GUIDE_BONUS_STARS
        updateUserStats(stats.copy(totalStars = stars, treeStage = treeStageFor(stars)))
        updateOrInsertRecord(DailyRecordEntity(date, PRAYER_GUIDE_REWARD_ID, true))
        return true
    }

    @Transaction
    suspend fun saveActivity(date: String, itemId: Int, completed: Boolean, today: String, yesterday: String) {
        if ((getRecordStatus(date, itemId) ?: false) == completed) return
        updateOrInsertRecord(DailyRecordEntity(date, itemId, completed))
        val stats = getUserStats().first() ?: UserStatsEntity()
        val stars = (stats.totalStars + if (completed) 10 else -10).coerceAtLeast(0)
        val newActiveDay = completed && stats.lastActiveDate != today
        val streak = when {
            !newActiveDay -> stats.currentStreak
            stats.lastActiveDate == yesterday -> stats.currentStreak + 1
            else -> 1
        }
        insertUserStats(stats.copy(
            totalStars = stars,
            tasksCompletedTotal = stats.tasksCompletedTotal + if (completed) 1 else 0,
            treeStage = treeStageFor(stars),
            currentStreak = streak,
            lastActiveDate = if (newActiveDay) today else stats.lastActiveDate
        ))
    }


    @Transaction
    suspend fun replaceProfile(stats: UserStatsEntity) {
        clearAllRecords()
        insertUserStats(stats)
    }

    @Transaction
    suspend fun clearProfile() {
        clearAllRecords()
        clearUserStats()
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
}

// Daily activity IDs are positive. This record also follows the existing profile reset lifecycle.
private const val PRAYER_GUIDE_REWARD_ID = -1

private fun treeStageFor(stars: Int) = when {
    stars < 50 -> 1
    stars < 200 -> 2
    else -> 3
}
