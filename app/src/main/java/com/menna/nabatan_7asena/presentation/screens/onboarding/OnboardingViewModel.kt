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
            title = "أهلاً بيك",
            description = "أنا تيمور، رفيقك في رحلة الصلاة والأذكار. هنمشي سوا خطوة بخطوة.",
            imageRes = R.drawable.onboarding_home
        ),
        OnboardingPage(
            title = "الصلاة عماد الدين",
            description = "هفكّرك بكل صلاة في ميعادها المظبوط، علشان ميفوتكش أجرها.",
            imageRes = R.drawable.onboarding_leaderboard
        ),
        OnboardingPage(
            title = "أذكار يومية",
            description = "تسبيح وذكر بعد كل صلاة، مع أذكار الصباح والمساء في مكان واحد.",
            imageRes = R.drawable.onboarding_sebha
        ),
        OnboardingPage(
            title = "محتاجين نعرف مكانك",
            description = "علشان نحسب مواقيت الصلاة واتجاه القبلة بدقة حسب مدينتك.",
            imageRes = R.drawable.onboarding_badges
        ),
        OnboardingPage(
            title = "الإشعارات",
            description = "هنبعتلك تنبيه بالأذان ووقت الصلاة والأذكار في وقتها بالظبط.",
            imageRes = R.drawable.onboarding_badges
        ),
        OnboardingPage(
            title = "تحسين استهلاك البطارية",
            description = "محتاجين نشتغل في الخلفية علشان التنبيهات توصلك من غير تأخير.",
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