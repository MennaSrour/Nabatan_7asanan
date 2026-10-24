package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.common.components.avatar.AvatarPickerSheet
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState

@Composable
internal fun SettingsImagePickerDialog(state: SettingsUiState, actions: SettingsActions) {
    if (state.showImagePickerDialog) {
        AvatarPickerSheet(
            gender = state.gender,
            selectedIndex = state.avatarIndex,
            onAvatarPicked = actions::onAvatarSelected,
            onDismiss = actions::onImagePickerDismissed,
            enabled = !state.isSavingAvatar
        )
    }
}
