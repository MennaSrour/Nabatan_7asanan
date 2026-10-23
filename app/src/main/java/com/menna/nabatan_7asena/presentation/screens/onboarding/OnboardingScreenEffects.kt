package com.menna.nabatan_7asena.presentation.screens.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects

@Composable
internal fun OnboardingScreenEffects(viewModel: OnboardingViewModel, onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val location = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.onPermissionResult() }
    val notifications = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.onPermissionResult() }
    val battery = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { viewModel.onPermissionResult() }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
            OnboardingEffect.RequestLocationPermission -> location.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            OnboardingEffect.RequestNotificationPermission -> if (Build.VERSION.SDK_INT >= 33) notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            OnboardingEffect.RequestBatteryExemption -> battery.launch(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}")))
        }
    }
}
