package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.menna.nabatan_7asena.platform.rememberPermissionRequester
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects

@Composable
fun OnboardingScreenEffects(viewModel: OnboardingViewModel, onNavigateToLogin: () -> Unit) {
    val request = rememberPermissionRequester(viewModel::onPermissionResult)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshPermissions() }
    CollectEffects(viewModel.effects) { effect -> when (effect) {
        OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
        is OnboardingEffect.RequestPermission -> request(effect.permission)
    } }
}
