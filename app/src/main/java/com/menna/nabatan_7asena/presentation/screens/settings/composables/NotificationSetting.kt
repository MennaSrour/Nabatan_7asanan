package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.common.genderedStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationSetting(
    title: com.menna.nabatan_7asena.presentation.common.GenderedString,
    subtitle: com.menna.nabatan_7asena.presentation.common.GenderedString,
    iconColor: androidx.compose.ui.graphics.Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SummerSettingsItem(
        icon = Icons.Rounded.Notifications,
        title = genderedStringResource(title),
        subtitle = genderedStringResource(subtitle),
        iconColor = iconColor,
        hasSwitch = true,
        isSwitchChecked = checked,
        onSwitchChange = onCheckedChange,
        onClick = { onCheckedChange(!checked) }
    )
}
