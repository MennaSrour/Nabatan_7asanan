package com.menna.nabatan_7asena.presentation.screens.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.start.composables.StartContent

@Composable
fun StartScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: StartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            StartEffect.NavigateToHome -> onNavigateToHome()
            StartEffect.NavigateToLogin -> onNavigateToLogin()
            StartEffect.NavigateToOnboarding -> onNavigateToOnboarding()
        }
    }
    DisposableEffect(viewModel) {
        viewModel.onScreenEntered()
        onDispose { viewModel.onScreenLeft() }
    }
    StartContent(state)
}
