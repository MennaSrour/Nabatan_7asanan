package com.menna.nabatan_7asena.presentation.screens.badges

data class BadgesUiState(
    val name: String = "",
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val treeStage: Int = 1,
    val quranPartsFinished: Int = 0
)
