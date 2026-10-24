package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.settings.composables.SettingsContent

@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val avatarSaveError = genderedStringResource(GenderedStrings.avatarSaveError, gender = state.gender)
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.ShowError -> snackbar.showSnackbar(effect.message)
            is SettingsEffect.ShowInfo -> snackbar.showSnackbar(effect.message)
            SettingsEffect.AvatarSaveFailed -> snackbar.showSnackbar(avatarSaveError)
            SettingsEffect.NavigateToLogin -> onLogout()
        }
    }
    SettingsContent(onBack, state, viewModel, snackbar)
}
