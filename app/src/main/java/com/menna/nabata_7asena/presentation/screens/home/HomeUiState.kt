package com.menna.nabata_7asena.presentation.screens.home


import androidx.compose.ui.graphics.Color
import com.menna.nabata_7asena.domain.entity.TaskCategory

data class HomeUiState(
    val isLoading: Boolean = true,
    val user: UiUser? = null,
    val dailyWisdom: String = "جاري تحميل نور اليوم...",
    val hijriDate: String = "",
    val tasks: List<UiTaskItem> = emptyList(),
    val showCelebration: Boolean = false
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
        val backgroundColor: Color,
        val contentColor: Color,
        val emoji: String,
        val category: TaskCategory,
        val isPlaying: Boolean = false
    )
}