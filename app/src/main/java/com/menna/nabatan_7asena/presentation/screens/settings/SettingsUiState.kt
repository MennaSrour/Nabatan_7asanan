package com.menna.nabatan_7asena.presentation.screens.settings

data class SettingsUiState(
    val name: String = "",
    val prayerNotifications: Boolean = true,
    val azkarNotifications: Boolean = true,
    val quranNotifications: Boolean = true
)
