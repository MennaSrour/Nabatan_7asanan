package com.menna.nabatan_7asena.data.local.room

import androidx.room.Entity
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Entity(tableName = "daily_rewards", primaryKeys = ["profileId", "day", "kind"])
data class DailyRewardEntity(
    val profileId: String,
    val day: String,
    val kind: String,
    val prize: String,
    val stars: Int,
    val synced: Boolean,
    val earnedHere: Boolean = true
)

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(CREATE_DAILY_REWARDS)
    }
}

internal const val CREATE_DAILY_REWARDS = """CREATE TABLE IF NOT EXISTS daily_rewards (
    profileId TEXT NOT NULL, day TEXT NOT NULL, kind TEXT NOT NULL,
    prize TEXT NOT NULL, stars INTEGER NOT NULL, synced INTEGER NOT NULL, earnedHere INTEGER NOT NULL,
    PRIMARY KEY(profileId, day, kind))"""
