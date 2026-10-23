package com.menna.nabatan_7asena.presentation.screens.onboarding

sealed interface OnboardingEffect {
    data object RequestLocationPermission : OnboardingEffect
    data object RequestNotificationPermission : OnboardingEffect
    data object RequestBatteryExemption : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
}
