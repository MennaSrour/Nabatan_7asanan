package com.menna.nabata_7asena.presentation.screens.start

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.data.local.room.AppDao
import com.menna.nabata_7asena.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val dao: AppDao
) : ViewModel() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _uiEvent = MutableSharedFlow<SplashEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            delay(2000)

            val isOnboardingCompleted = isOnboardingCompleted()

            if (!isOnboardingCompleted) {
                _uiEvent.emit(SplashEvent.NavigateToOnboarding)
                return@launch
            }
            val hasPermissions = areAllPermissionsGranted()

            if (!hasPermissions) {
                _uiEvent.emit(SplashEvent.NavigateToOnboarding)
                return@launch
            }

            val isLoggedIn = isUserLoggedIn()

            if (isLoggedIn) {
                _uiEvent.emit(SplashEvent.NavigateToHome)
            } else {
                _uiEvent.emit(SplashEvent.NavigateToLogin)
            }
        }
    }

    private fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("is_onboarding_completed", false)
    }

    private suspend fun isUserLoggedIn(): Boolean {
        val userStats = dao.getUserStats().firstOrNull()

        return userStats != null && userStats.userId.isNotBlank()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isBatteryOptimizationDisabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        val hasEssentialPermissions = isLocationPermissionGranted() &&
                isNotificationPermissionGranted()

        return hasEssentialPermissions
    }
}

sealed class SplashEvent {
    object NavigateToOnboarding : SplashEvent()
    object NavigateToLogin : SplashEvent()
    object NavigateToHome : SplashEvent()
}