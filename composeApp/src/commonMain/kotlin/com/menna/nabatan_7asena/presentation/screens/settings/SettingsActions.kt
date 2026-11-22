package com.menna.nabatan_7asena.presentation.screens.settings

interface SettingsActions {
    fun onPermissionClicked(permission: com.menna.nabatan_7asena.platform.AppPermission) = Unit
    fun selectPrayerCity(city: com.menna.nabatan_7asena.domain.entity.PrayerPlace?) = Unit
    fun selectPrayerMethod(method: com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod?) = Unit
    fun setHijriDateOffset(offset: Int) = Unit
    fun onEditAvatarClicked()
    fun onImagePickerDismissed()
    fun onAvatarSelected(imageIdentifier: Int)
    fun toggleNotification(category: String, isEnabled: Boolean)
    fun updateUserName(newName: String)
    fun resetProgress()
}
