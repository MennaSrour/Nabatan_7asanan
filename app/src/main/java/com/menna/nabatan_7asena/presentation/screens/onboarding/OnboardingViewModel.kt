package com.menna.nabatan_7asena.presentation.screens.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), OnboardingActions {
    private val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    override fun updatePageIndex(index: Int) {
        _uiState.update { it.copy(currentPage = index.coerceIn(it.pages.indices)) }
    }

    override fun onNextClicked() {
        when (_uiState.value.currentPage) {
            3 -> if (locationGranted()) updatePageIndex(4) else emit(OnboardingEffect.RequestLocationPermission)
            4 -> if (notificationsGranted()) updatePageIndex(5) else emit(OnboardingEffect.RequestNotificationPermission)
            5 -> if (batteryExempt()) complete() else _uiState.update { it.copy(showBatteryExplanation = true) }
            else -> updatePageIndex(_uiState.value.currentPage + 1)
        }
    }

    override fun onSkipClicked() {
        if (_uiState.value.currentPage < 3) updatePageIndex(3) else complete()
    }

    fun onPermissionResult() {
        when (_uiState.value.currentPage) {
            3 -> if (locationGranted()) updatePageIndex(4)
            4 -> if (notificationsGranted()) updatePageIndex(5)
        }
    }

    override fun dismissBatteryExplanation() { _uiState.update { it.copy(showBatteryExplanation = false) } }
    override fun confirmBatteryExplanation() { dismissBatteryExplanation(); emit(OnboardingEffect.RequestBatteryExemption) }

    private fun complete() {
        preferences.edit().putBoolean("is_onboarding_completed", true).apply()
        emit(OnboardingEffect.NavigateToLogin)
    }

    private fun emit(effect: OnboardingEffect) { viewModelScope.launch { _effects.send(effect) } }
    private fun locationGranted() = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private fun notificationsGranted() = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    private fun batteryExempt() = context.getSystemService(PowerManager::class.java)?.isIgnoringBatteryOptimizations(context.packageName) == true
}
