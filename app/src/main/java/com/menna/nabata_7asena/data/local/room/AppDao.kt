package com.menna.nabata_7asena.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

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