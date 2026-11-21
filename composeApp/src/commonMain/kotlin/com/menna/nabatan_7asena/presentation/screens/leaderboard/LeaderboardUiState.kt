package com.menna.nabatan_7asena.presentation.screens.leaderboard

import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User

data class LeaderboardUiState(
    val list: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String = "",
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val currentUserRank: Long? = null,
    val isRankLoading: Boolean = false,
    val isRankUnavailable: Boolean = false
)
