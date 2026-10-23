package com.menna.nabatan_7asena.presentation.screens.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.login.composables.LoginContent

@Composable
fun LoginScreen(onNavigateToHome: () -> Unit, viewModel: LoginViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val resources = LocalResources.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            LoginEffect.NavigateToHome -> onNavigateToHome()
            is LoginEffect.ShowError -> snackbar.showSnackbar(resources.getString(effect.message.resourceId(state.selectedGender)))
        }
    }
    DisposableEffect(viewModel) {
        viewModel.onScreenEntered()
        onDispose { viewModel.onScreenLeft() }
    }
    LoginContent(state, viewModel, snackbar)
}
