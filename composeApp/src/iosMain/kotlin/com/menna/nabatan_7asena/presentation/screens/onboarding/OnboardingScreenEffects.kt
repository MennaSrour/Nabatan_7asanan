package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects

@Composable
actual fun OnboardingScreenEffects(viewModel: OnboardingViewModel, onNavigateToLogin: () -> Unit) {
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
            else -> viewModel.onPermissionResult()
        }
    }
}
