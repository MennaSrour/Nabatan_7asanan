package com.menna.nabatan_7asena.presentation.screens.onboarding

interface OnboardingActions {
    fun updatePageIndex(index: Int)
    fun onNextClicked()
    fun onSkipClicked()
    fun dismissBatteryExplanation()
    fun confirmBatteryExplanation()
}
