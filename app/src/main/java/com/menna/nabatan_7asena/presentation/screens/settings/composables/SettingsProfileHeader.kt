package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.components.avatar.AvatarPreview
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun SettingsProfileHeader(state: SettingsUiState, actions: SettingsActions) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AvatarPreview(state.avatarResId, actions::onEditAvatarClicked,
            enabled = state.userId != null && !state.isSavingAvatar)
        Spacer(Modifier.height(8.dp))
        Text(state.name, color = SummerTheme.colors.textPrimary)
        if (state.isSavingAvatar) CircularProgressIndicator(color = SummerTheme.colors.goldWarm)
        Spacer(Modifier.height(16.dp))
    }
}
