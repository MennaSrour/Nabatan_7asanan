package com.menna.nabata_7asena.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        DailyRecordEntity::class,
        UserStatsEntity::class,
        DailyPlanEntity::class,
        ExtraTaskEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}