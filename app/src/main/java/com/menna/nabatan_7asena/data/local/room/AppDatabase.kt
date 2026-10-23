package com.menna.nabatan_7asena.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        DailyRecordEntity::class,
        UserStatsEntity::class,
        DailyPlanEntity::class,
        ExtraTaskEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}