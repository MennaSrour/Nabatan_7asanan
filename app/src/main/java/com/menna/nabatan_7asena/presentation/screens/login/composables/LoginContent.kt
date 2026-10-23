package com.menna.nabatan_7asena.presentation.screens.login.composables

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
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.LocalUserGender
import com.menna.nabatan_7asena.presentation.screens.login.LoginActions
import com.menna.nabatan_7asena.presentation.screens.login.LoginUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    state: LoginUiState,
    actions: LoginActions,
    snackbarHostState: SnackbarHostState
) {
    CompositionLocalProvider(LocalUserGender provides state.selectedGender) {
        var showAvatarSheet by remember { mutableStateOf(false) }
        val currentAvatarList = AvatarCatalog.listFor(state.selectedGender)
        val currentAvatarIndex = if (state.selectedGender == User.Gender.BOY)
        state.selectedBoyAvatarIndex else state.selectedGirlAvatarIndex
        val currentAvatarRes = currentAvatarList.getOrElse(currentAvatarIndex) { currentAvatarList.first() }

        Scaffold(
            snackbarHost = {
                LoginSnackbarHost(snackbarHostState)
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                NabatanLoginBackground()

                LoginForm(state, actions, currentAvatarRes, { showAvatarSheet = true })
            }
        }

        if (showAvatarSheet) {
            AvatarPickerSheet(
                gender = state.selectedGender,
                avatars = currentAvatarList,
                selectedIndex = currentAvatarIndex,
                onAvatarPicked = { index ->
                    actions.onAvatarSelected(index)
                    showAvatarSheet = false
                },
                onDismiss = { showAvatarSheet = false }
            )
        }
    }
}
