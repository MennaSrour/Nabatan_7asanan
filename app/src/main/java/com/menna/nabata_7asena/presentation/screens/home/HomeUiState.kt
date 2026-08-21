package com.menna.nabata_7asena.presentation.screens.home

import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.TaskCategory

data class HomeUiState(
    val isLoading: Boolean = true,
    val user: UiUser? = null,
    val dailyWisdom: String = "جاري تحميل نور اليوم...",
    val hijriDate: String = "",
    val tasks: List<UiTaskItem> = emptyList(),
    val showCelebration: Boolean = false,
    val isNightTheme: Boolean = false,
    val hasBookmark: Boolean = false,
    val showChallengeDialog: Boolean = false,
    val currentRiddle: DailyContent.Riddle? = null
) {
    data class UiUser(
        val name: String,
        val avatarEmoji: String,
        val starsText: String,
        val streakText: String
    )

    data class UiTaskItem(
        val id: Int,
        val title: String,
        val subtitle: String? = null,
        val isCompleted: Boolean,
        val emoji: String,
        val category: TaskCategory,
        val isPlaying: Boolean = false
    )
}