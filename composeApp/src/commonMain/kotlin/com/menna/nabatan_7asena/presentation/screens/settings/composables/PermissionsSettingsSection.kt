package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.platform.AppPermission
import com.menna.nabatan_7asena.presentation.screens.settings.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource

@Composable
internal fun PermissionsSettingsSection(state: SettingsUiState, actions: SettingsActions) {
    SummerSectionTitle(stringResource(R.string.permissions_title))
    val items = if (state.supportsAndroidPermissions) AppPermission.entries else AppPermission.entries.take(2)
    items.forEach { permission ->
        val (title, hint) = when (permission) {
            AppPermission.LOCATION -> R.string.permission_location to R.string.permission_location_hint
            AppPermission.NOTIFICATIONS -> R.string.permission_notifications to R.string.permission_notifications_hint
            AppPermission.EXACT_ALARMS -> R.string.permission_exact to R.string.permission_exact_hint
            AppPermission.BATTERY -> R.string.permission_battery to R.string.permission_battery_hint
        }
        OutlinedCard(onClick = { actions.onPermissionClicked(permission) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(title), style = MaterialTheme.typography.titleSmall)
                    Text(stringResource(hint), style = MaterialTheme.typography.bodySmall)
                }
                SuggestionChip(onClick = { actions.onPermissionClicked(permission) }, label = {
                    Text(stringResource(if (state.permissions.granted(permission)) R.string.permission_enabled else R.string.permission_disabled))
                })
            }
        }
    }
}
