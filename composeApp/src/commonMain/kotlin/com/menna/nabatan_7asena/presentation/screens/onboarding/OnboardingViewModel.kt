package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class OnboardingViewModel constructor(
    private val permissions: com.menna.nabatan_7asena.platform.PermissionPlatform
) : ViewModel(), OnboardingActions {
    private val preferences = com.menna.nabatan_7asena.platform.PlatformPreferences("app_prefs")
    private val _uiState = MutableStateFlow(
        OnboardingUiState(pages = if (permissions.supportsAndroidPermissions) onboardingPages else onboardingPages.take(3))
    )
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    override fun updatePageIndex(index: Int) {
        _uiState.update { it.copy(currentPage = index.coerceIn(it.pages.indices)) }
    }

    override fun onNextClicked() {
        if (!permissions.supportsAndroidPermissions) {
            if (_uiState.value.currentPage == _uiState.value.pages.lastIndex) complete()
            else updatePageIndex(_uiState.value.currentPage + 1)
            return
        }
        when (_uiState.value.currentPage) {
            3 -> if (!permissions.supportsAndroidPermissions || locationGranted()) updatePageIndex(4) else emit(OnboardingEffect.RequestLocationPermission)
            4 -> if (!permissions.supportsAndroidPermissions || notificationsGranted()) updatePageIndex(5) else emit(OnboardingEffect.RequestNotificationPermission)
            5 -> if (!permissions.supportsAndroidPermissions || batteryExempt()) complete() else _uiState.update { it.copy(showBatteryExplanation = true) }
            else -> updatePageIndex(_uiState.value.currentPage + 1)
        }
    }

    override fun onSkipClicked() {
        if (!permissions.supportsAndroidPermissions) complete()
        else if (_uiState.value.currentPage < 3) updatePageIndex(3) else complete()
    }

    fun onPermissionResult() {
        when (_uiState.value.currentPage) {
            3 -> if (!permissions.supportsAndroidPermissions || locationGranted()) updatePageIndex(4)
            4 -> if (!permissions.supportsAndroidPermissions || notificationsGranted()) updatePageIndex(5)
        }
    }

    override fun dismissBatteryExplanation() { _uiState.update { it.copy(showBatteryExplanation = false) } }
    override fun confirmBatteryExplanation() { dismissBatteryExplanation(); emit(OnboardingEffect.RequestBatteryExemption) }

    private fun complete() {
        preferences.putBoolean("is_onboarding_completed", true)
        emit(OnboardingEffect.NavigateToLogin)
    }

    private fun emit(effect: OnboardingEffect) { viewModelScope.launch { _effects.send(effect) } }
    private fun locationGranted() = permissions.locationGranted()
    private fun notificationsGranted() = permissions.notificationsGranted()
    private fun batteryExempt() = permissions.batteryExempt()
}
