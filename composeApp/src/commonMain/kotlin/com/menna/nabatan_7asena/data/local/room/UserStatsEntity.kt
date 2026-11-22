package com.menna.nabatan_7asena.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val gender: String = "BOY",
    val userId: String = "",
    val avatarIndex: Int = 0,

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
internal fun UserStatsEntity.withBonus(stars: Int): UserStatsEntity {
    val newStars = (totalStars + stars).coerceAtLeast(0)
    return copy(totalStars = newStars, treeStage = when {
        newStars < 50 -> 1
        newStars < 200 -> 2
        else -> 3
    })
}

internal fun UserStatsEntity.withDailyActivity(completed: Boolean, date: String, yesterday: String): UserStatsEntity {
    val scored = withBonus(if (completed) 10 else -10)
    if (!completed) return scored
    if (lastActiveDate == date) return scored.copy(tasksCompletedTotal = tasksCompletedTotal + 1)
    return scored.copy(
        tasksCompletedTotal = tasksCompletedTotal + 1,
        currentStreak = if (lastActiveDate == yesterday) currentStreak + 1 else 1,
        lastActiveDate = date
    )
}
