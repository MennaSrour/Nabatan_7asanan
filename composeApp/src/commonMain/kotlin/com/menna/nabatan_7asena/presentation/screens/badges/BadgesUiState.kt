package com.menna.nabatan_7asena.presentation.screens.badges

data class BadgesUiState(
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val quranPartsFinished: Int = 0,
    val fajrDays: Int = 0,
    val azkarDays: Int = 0,
    val wateredGarden: Boolean = false,
    val finishedFriday: Boolean = false
)
