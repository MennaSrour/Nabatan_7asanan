package com.menna.nabatan_7asena.presentation.screens.sebha

data class ZikrItem(
    val id: Int,
    val text: String,
    val target: Int = 33,
    val audioResId: Int = 0,
    val color: Long = 0xFF42A5F5,
    val isCustom: Boolean = false
)

data class SebhaUiState(
    val isSelectionMode: Boolean = true,
    val azkarList: List<ZikrItem> = emptyList(),
    val currentZikr: ZikrItem? = null,
    val currentCount: Int = 0,
    val totalCycles: Int = 0,
    val isPlayingAudio: Boolean = false,
    val showCelebration: Boolean = false
)
