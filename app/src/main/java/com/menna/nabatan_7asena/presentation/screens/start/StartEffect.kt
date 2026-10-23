package com.menna.nabatan_7asena.presentation.screens.start

sealed interface StartEffect {
    data object NavigateToOnboarding : StartEffect
    data object NavigateToLogin : StartEffect
    data object NavigateToHome : StartEffect
}
