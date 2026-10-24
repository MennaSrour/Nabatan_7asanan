package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.annotation.DrawableRes
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog

data class SettingsUiState(
    val name: String = "",
    val prayerNotifications: Boolean = true,
    val azkarNotifications: Boolean = true,
    val quranNotifications: Boolean = true,
    val userId: String? = null,
    val gender: User.Gender = User.Gender.BOY,
    val avatarIndex: Int = 0,
    val showImagePickerDialog: Boolean = false,
    val isSavingAvatar: Boolean = false
) {
    @get:DrawableRes
    val avatarResId: Int get() = AvatarCatalog.resFor(gender, avatarIndex)
}
