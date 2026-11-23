package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.platform.AppPermission
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun PermissionsSettingsSection(state: SettingsUiState, actions: SettingsActions) {
    SummerSectionTitle(stringResource(R.string.permissions_title))
    val permissions = if (state.supportsAndroidPermissions) AppPermission.entries
        else listOf(AppPermission.LOCATION, AppPermission.NOTIFICATIONS)
    permissions.forEach { permission ->
        val (icon, title, hint) = when (permission) {
            AppPermission.LOCATION -> Triple(Icons.Rounded.LocationOn, R.string.permission_location, R.string.permission_location_hint)
            AppPermission.NOTIFICATIONS -> Triple(Icons.Rounded.Notifications, R.string.permission_notifications, R.string.permission_notifications_hint)
            AppPermission.EXACT_ALARMS -> Triple(Icons.Rounded.Schedule, R.string.permission_exact, R.string.permission_exact_hint)
            AppPermission.BATTERY -> Triple(Icons.Rounded.BatteryChargingFull, R.string.permission_battery, R.string.permission_battery_hint)
        }
        val granted = state.permissions.granted(permission)
        val status = stringResource(if (granted) R.string.permission_enabled else R.string.permission_disabled)
        SummerSettingsItem(
            icon = icon,
            title = stringResource(title),
            subtitle = "$status · ${stringResource(hint)}",
            iconColor = if (granted) SummerTheme.Colors.TaskCompletedGreen else SummerTheme.colors.wisdomAccent,
            onClick = { actions.onPermissionClicked(permission) }
        )
    }
}
