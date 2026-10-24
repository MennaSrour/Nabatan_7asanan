package com.menna.nabatan_7asena.presentation.screens.settings

interface SettingsActions {
    fun onEditAvatarClicked()
    fun onImagePickerDismissed()
    fun onAvatarSelected(imageIdentifier: Int)
    fun toggleNotification(category: String, isEnabled: Boolean)
    fun updateUserName(newName: String)
    fun resetProgress()
}
