package com.menna.nabatan_7asena.presentation.screens.settings

sealed class SettingsEffect {
    data object AvatarSaveFailed : SettingsEffect()
    data object NavigateToLogin : SettingsEffect()
    data class ShowError(val message: String) : SettingsEffect()
    data class ShowInfo(val message: String) : SettingsEffect()
}
