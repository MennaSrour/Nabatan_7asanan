package com.menna.nabatan_7asena.presentation.screens.onboarding

import com.menna.nabatan_7asena.platform.AppPermission

sealed interface OnboardingEffect {
    data class RequestPermission(val permission: AppPermission) : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
}
