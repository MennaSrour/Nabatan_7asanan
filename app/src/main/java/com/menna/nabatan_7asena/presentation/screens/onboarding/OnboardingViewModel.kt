package com.menna.nabatan_7asena.presentation.screens.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currentPageIndex = mutableIntStateOf(0)
    val currentPageIndex: State<Int> = _currentPageIndex

    val pages = listOf(
        OnboardingPage(
            title = GenderedStrings.onboardingWelcomeTitle,
            description = GenderedStrings.onboardingWelcomeDescription,
            imageRes = R.drawable.onboarding_home
        ),
        OnboardingPage(
            title = GenderedStrings.onboardingPrayerTitle,
            description = GenderedStrings.onboardingPrayerDescription,
            imageRes = R.drawable.onboarding_leaderboard
        ),
        OnboardingPage(
            title = GenderedStrings.onboardingAzkarTitle,
            description = GenderedStrings.onboardingAzkarDescription,
            imageRes = R.drawable.onboarding_sebha
        ),
        OnboardingPage(
            title = GenderedStrings.onboardingLocationTitle,
            description = GenderedStrings.onboardingLocationDescription,
            imageRes = R.drawable.onboarding_badges
        ),
        OnboardingPage(
            title = GenderedStrings.onboardingNotificationsTitle,
            description = GenderedStrings.onboardingNotificationsDescription,
            imageRes = R.drawable.onboarding_badges
        ),
        OnboardingPage(
            title = GenderedStrings.onboardingBackgroundTitle,
            description = GenderedStrings.onboardingBackgroundDescription,
            imageRes = R.drawable.onboarding_badges
        )
    )

    fun updatePageIndex(index: Int) {
        _currentPageIndex.intValue = index
    }

    fun saveOnboardingCompleted() {
        prefs.edit().putBoolean("is_onboarding_completed", true).apply()
    }

    fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean("is_onboarding_completed", false)

    fun isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun isNotificationPermissionGranted(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    fun isBatteryOptimizationDisabled(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }

    fun areAllPermissionsGranted(context: Context): Boolean =
        isLocationPermissionGranted() &&
                isNotificationPermissionGranted() &&
                isBatteryOptimizationDisabled(context)
}