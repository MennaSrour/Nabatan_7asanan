package com.menna.nabatan_7asena.presentation.screens.leaderboard

import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry

data class LeaderboardUiState(
    val list: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String = "",
    val isLoading: Boolean = false
)
