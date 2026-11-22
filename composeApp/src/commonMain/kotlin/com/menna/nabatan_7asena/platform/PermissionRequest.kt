package com.menna.nabatan_7asena.platform

import androidx.compose.runtime.Composable

enum class AppPermission { LOCATION, NOTIFICATIONS, EXACT_ALARMS, BATTERY }
data class PermissionStatus(
    val location: Boolean = false, val notifications: Boolean = false,
    val exactAlarms: Boolean = false, val battery: Boolean = false
) {
    fun granted(permission: AppPermission) = when (permission) {
        AppPermission.LOCATION -> location
        AppPermission.NOTIFICATIONS -> notifications
        AppPermission.EXACT_ALARMS -> exactAlarms
        AppPermission.BATTERY -> battery
    }
}
fun PermissionPlatform.status() = PermissionStatus(locationGranted(), notificationsGranted(), exactAlarmsGranted(), batteryExempt())

@Composable
expect fun rememberPermissionRequester(onResult: (AppPermission) -> Unit): (AppPermission) -> Unit
expect fun appPermissions(): PermissionPlatform
