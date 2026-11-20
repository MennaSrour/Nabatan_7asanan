package com.menna.nabatan_7asena.data.local.room

import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor
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
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
