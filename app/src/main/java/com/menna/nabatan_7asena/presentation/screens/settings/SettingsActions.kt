package com.menna.nabatan_7asena.presentation.screens.settings

interface SettingsActions {
    fun toggleNotification(category: String, isEnabled: Boolean)
    fun updateUserName(newName: String)
    fun resetProgress()
}
