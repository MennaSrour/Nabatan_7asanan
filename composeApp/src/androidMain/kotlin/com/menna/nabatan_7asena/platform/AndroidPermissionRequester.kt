package com.menna.nabatan_7asena.platform

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

actual fun appPermissions(): PermissionPlatform = AndroidPermissions()

@Composable
actual fun rememberPermissionRequester(onResult: (AppPermission) -> Unit): (AppPermission) -> Unit {
    val context = LocalContext.current
    val callback by rememberUpdatedState(onResult)
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    val finished: () -> Unit = {
        pending?.let { name -> pending = null; callback(AppPermission.valueOf(name)) }
    }
    val runtime = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { finished() }
    val settings = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { finished() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { finished() }
    return { permission ->
        if (pending == null) {
            pending = permission.name
            val prefs = PlatformPreferences("permission_requests")
            val names = when (permission) {
                AppPermission.LOCATION -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                AppPermission.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else emptyArray()
                else -> emptyArray()
            }
            val activity = context.findActivity()
            val deniedPermanently = names.isNotEmpty() && prefs.getBoolean(permission.name) &&
                activity != null && names.none { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
            try {
                if (names.isNotEmpty() && !deniedPermanently && !appPermissions().status().granted(permission)) {
                    prefs.putBoolean(permission.name, true)
                    if (permission == AppPermission.NOTIFICATIONS) BroadcastNotificationManager.recordPermissionRequest()
                    runtime.launch(names)
                } else {
                    val uri = Uri.parse("package:${context.packageName}")
                    val intent = when {
                        deniedPermanently -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                        permission == AppPermission.EXACT_ALARMS && Build.VERSION.SDK_INT >= 31 -> Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, uri)
                        permission == AppPermission.BATTERY -> Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, uri)
                        permission == AppPermission.NOTIFICATIONS -> Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        else -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                    }
                    settings.launch(intent)
                }
            } catch (_: android.content.ActivityNotFoundException) {
                try { settings.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))) }
                catch (_: android.content.ActivityNotFoundException) { finished() }
            } catch (_: SecurityException) { finished() }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
