package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.menna.nabatan_7asena.presentation.common.LocalUserGender
import androidx.compose.ui.unit.LayoutDirection
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    onBack: () -> Unit,
    state: SettingsUiState,
    actions: SettingsActions,
    snackbarHostState: SnackbarHostState
) {

    var showResetDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalUserGender provides state.gender) {
        Scaffold(
            containerColor = SummerTheme.colors.transparent,
            snackbarHost = {
                SettingsSnackbarHost(snackbarHostState)
            },
            topBar = {
                SettingsTopBar(onBack)
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                SummerSettingsBackground()

                SettingsSections(state, actions, padding, { showEditNameDialog = true }, { showResetDialog = true })
            }
        }

        SettingsImagePickerDialog(state, actions)

        if (showEditNameDialog) EditNameDialog(actions) { showEditNameDialog = false }

        if (showResetDialog) ResetProgressDialog(actions) { showResetDialog = false }

    }
}
