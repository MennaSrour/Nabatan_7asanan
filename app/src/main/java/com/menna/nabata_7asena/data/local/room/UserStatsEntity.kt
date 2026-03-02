package com.menna.nabata_7asena.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val gender: String = "BOY",
    val userId: String = "",

    val totalStars: Int = 0,
    val treeStage: Int = 1,
    val currentStreak: Int = 0,
    val lastActiveDate: String = "",
    val quranPartsFinished: Int = 0,
    val tasksCompletedTotal: Int = 0,
    val lastQuranPage: Int = 1,
    val prayerNotifications: Boolean = true,
    val azkarNotifications: Boolean = true,
    val quranNotifications: Boolean = true,
    )