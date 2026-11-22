package com.menna.nabatan_7asena.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.platform.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class OnboardingViewModel(
    private val permissions: PermissionPlatform,
    private val persistCompletion: () -> Unit = { PlatformPreferences("app_prefs").putBoolean("is_onboarding_completed", true) }
) : ViewModel(), OnboardingActions {
    private val _uiState = MutableStateFlow(OnboardingUiState(pages =
        if (permissions.supportsAndroidPermissions) onboardingPages else onboardingPages.take(5)))
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var completed = false

    override fun updatePageIndex(index: Int) { _uiState.update { it.copy(currentPage = index.coerceIn(it.pages.indices)) } }
    private fun advance() {
        if (_uiState.value.currentPage == _uiState.value.pages.lastIndex) complete()
        else updatePageIndex(_uiState.value.currentPage + 1)
    }
    override fun onNextClicked() {
        val state = _uiState.value
        if (state.currentPage < 3) { advance(); return }
        val permission = permissionForPage(state.currentPage) ?: return
        if (permissions.status().granted(permission) || permission in state.returnedPrompts) { advance(); return }
        if (permission == AppPermission.BATTERY) _uiState.update { it.copy(showBatteryExplanation = true) }
        else request(permission)
    }
    override fun onSkipClicked() {
        if (_uiState.value.currentPage < 3) updatePageIndex(3) else advance()
    }
    fun refreshPermissions() { viewModelScope.launch {
        permissions.refresh()
        _uiState.update { it.copy(permissions = permissions.status()) }
    } }
    fun onPermissionResult(permission: AppPermission) {
        _uiState.update { it.copy(returnedPrompts = it.returnedPrompts + permission, permissions = permissions.status()) }
        refreshPermissions()
        if (permissionForPage(_uiState.value.currentPage) == permission && permissions.status().granted(permission)) advance()
    }
    override fun dismissBatteryExplanation() { _uiState.update { it.copy(showBatteryExplanation = false) } }
    override fun confirmBatteryExplanation() { dismissBatteryExplanation(); request(AppPermission.BATTERY) }
    private fun request(permission: AppPermission) {
        emit(OnboardingEffect.RequestPermission(permission))
    }
    private fun complete() {
        if (completed) return
        completed = true
        persistCompletion()
        emit(OnboardingEffect.NavigateToLogin)
    }
    private fun emit(effect: OnboardingEffect) { viewModelScope.launch { _effects.send(effect) } }
}

internal fun permissionForPage(page: Int): AppPermission? = when (page) {
    3 -> AppPermission.LOCATION
    4 -> AppPermission.NOTIFICATIONS
    5 -> AppPermission.EXACT_ALARMS
    6 -> AppPermission.BATTERY
    else -> null
}
