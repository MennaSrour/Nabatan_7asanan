package com.menna.nabatan_7asena.presentation.screens.leaderboard

sealed interface LeaderboardEffect {
    data class ShowMessage(val message: String) : LeaderboardEffect
}
