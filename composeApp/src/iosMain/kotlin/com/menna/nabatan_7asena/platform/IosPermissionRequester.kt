@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.platform

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.*
import platform.UIKit.*
import platform.UserNotifications.*
import kotlin.coroutines.resume

internal object IosPermissions : PermissionPlatform {
    override val supportsAndroidPermissions = false
    private var notificationAccess = false
    override fun locationGranted() = IosLocationAccess.granted()
    override fun notificationsGranted() = notificationAccess
    override fun exactAlarmsGranted() = false
    override fun batteryExempt() = false
    override suspend fun refresh() {
        notificationAccess = suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
                if (continuation.isActive) continuation.resume(settings?.authorizationStatus in
                    listOf(UNAuthorizationStatusAuthorized, UNAuthorizationStatusProvisional, UNAuthorizationStatusEphemeral))
            }
        }
    }
}
actual fun appPermissions(): PermissionPlatform = IosPermissions

@Composable
actual fun rememberPermissionRequester(onResult: (AppPermission) -> Unit): (AppPermission) -> Unit {
    val scope = rememberCoroutineScope()
    val callback by rememberUpdatedState(onResult)
    var requesting by remember { mutableStateOf(false) }
    return { permission ->
        if (!requesting) {
            requesting = true
            scope.launch {
                try {
                    when (permission) {
                        AppPermission.LOCATION -> {
                            if (CLLocationManager.authorizationStatus() == kCLAuthorizationStatusNotDetermined)
                                IosLocationAccess.requestAuthorization()
                            else openIosAppSettings()
                        }
                        AppPermission.NOTIFICATIONS -> {
                            val status = suspendCancellableCoroutine<UNAuthorizationStatus> { continuation ->
                                UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler {
                                    if (continuation.isActive) continuation.resume(it?.authorizationStatus ?: UNAuthorizationStatusNotDetermined)
                                }
                            }
                            if (status == UNAuthorizationStatusNotDetermined) suspendCancellableCoroutine<Unit> { continuation ->
                                UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge) { _, _ ->
                                    if (continuation.isActive) continuation.resume(Unit)
                                }
                            } else openIosAppSettings()
                        }
                        else -> Unit
                    }
                    IosPermissions.refresh()
                    callback(permission)
                } finally { requesting = false }
            }
        }
    }
}
private fun openIosAppSettings() {
    NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
        UIApplication.sharedApplication.openURL(it, emptyMap<Any?, Any>(), null)
    }
}
