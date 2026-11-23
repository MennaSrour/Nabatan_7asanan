package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.menna.nabatan_7asena.data.local.prayerCityChoices
import com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsActions
import com.menna.nabatan_7asena.presentation.screens.settings.SettingsUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun PrayerSettingsSection(state: SettingsUiState, actions: SettingsActions) {
    SummerSectionTitle("مواقيت الصلاة والتاريخ الهجري")
    val settings = state.prayerSettings
    val useGps = stringResource(R.string.prayer_use_gps)
    val savedLocation = stringResource(
        R.string.prayer_saved_location,
        settings.place?.city ?: stringResource(R.string.prayer_no_location)
    )
    SettingsChoice(
        icon = Icons.Rounded.LocationOn,
        title = stringResource(R.string.prayer_location_settings),
        subtitle = "${settings.selectedCity?.city ?: useGps}\n$savedLocation",
        options = listOf(useGps) + prayerCityChoices.map { it.city },
        onSelect = { actions.selectPrayerCity(prayerCityChoices.getOrNull(it - 1)) }
    )
    val methods = PrayerCalculationMethod.entries
    val methodOptions = listOf(stringResource(R.string.prayer_method_auto)) + methods.map { methodName(it) }
    SettingsChoice(
        icon = Icons.Rounded.Schedule,
        title = stringResource(R.string.prayer_method_title),
        subtitle = settings.method?.let { methodName(it) } ?: methodOptions.first(),
        options = methodOptions,
        onSelect = { actions.selectPrayerMethod(methods.getOrNull(it - 1)) }
    )
    val offsets = listOf("تأخير يومين", "تأخير يوم", "بدون تعديل", "تقديم يوم", "تقديم يومين")
    SettingsChoice(
        icon = Icons.Rounded.CalendarMonth,
        title = stringResource(R.string.hijri_offset_title),
        subtitle = "${offsets[(settings.hijriDateOffset + 2).coerceIn(offsets.indices)]}\n${stringResource(R.string.hijri_offset_hint)}",
        options = offsets,
        onSelect = { actions.setHijriDateOffset(it - 2) }
    )
}

@Composable
private fun SettingsChoice(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        SummerSettingsItem(icon, title, subtitle, iconColor = SummerTheme.colors.wisdomAccent,
            onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    expanded = false
                    onSelect(index)
                })
            }
        }
    }
}

@Composable
private fun methodName(method: PrayerCalculationMethod): String = stringResource(when (method) {
    PrayerCalculationMethod.EGYPTIAN -> R.string.prayer_method_egypt
    PrayerCalculationMethod.UMM_AL_QURA -> R.string.prayer_method_saudi
    PrayerCalculationMethod.MWL -> R.string.prayer_method_mwl
    PrayerCalculationMethod.ISNA -> R.string.prayer_method_isna
})
