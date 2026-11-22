package com.menna.nabatan_7asena.domain.entity

import kotlinx.coroutines.flow.Flow

data class AchievementStats(val fajrDays: Int, val azkarDays: Int, val finishedFriday: Boolean)

fun interface AchievementProgress {
    fun observe(userId: String): Flow<AchievementStats>
}
