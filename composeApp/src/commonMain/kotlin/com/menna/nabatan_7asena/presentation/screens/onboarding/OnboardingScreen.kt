package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.onboarding.composables.OnboardingContent

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit, viewModel: OnboardingViewModel = platformOnboardingViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OnboardingScreenEffects(viewModel, onNavigateToLogin)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OnboardingContent(state, viewModel)
    }
}
