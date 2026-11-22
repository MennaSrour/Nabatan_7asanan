package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.data.local.prayerCityChoices
import com.menna.nabatan_7asena.domain.entity.PrayerCalculationMethod
import com.menna.nabatan_7asena.presentation.screens.settings.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource

@Composable
internal fun PrayerSettingsSection(state: SettingsUiState, actions: SettingsActions) {
    SummerSectionTitle(stringResource(R.string.prayer_location_settings))
    SettingsChoice(state.prayerSettings.selectedCity?.city ?: stringResource(R.string.prayer_use_gps),
        listOf(stringResource(R.string.prayer_use_gps)) + prayerCityChoices.map { it.city }) {
        actions.selectPrayerCity(prayerCityChoices.getOrNull(it - 1))
    }
    Text(stringResource(R.string.prayer_saved_location, state.prayerSettings.place?.city ?: stringResource(R.string.prayer_no_location)),
        style = MaterialTheme.typography.bodySmall)
    SummerSectionTitle(stringResource(R.string.prayer_method_title))
    val choices = listOf(stringResource(R.string.prayer_method_auto)) + PrayerCalculationMethod.entries.map { methodName(it) }
    SettingsChoice(state.prayerSettings.method?.let { methodName(it) } ?: choices.first(), choices) {
        actions.selectPrayerMethod(PrayerCalculationMethod.entries.getOrNull(it - 1))
    }
    SummerSectionTitle(stringResource(R.string.hijri_offset_title))
    Text(stringResource(R.string.hijri_offset_hint), style = MaterialTheme.typography.bodySmall)
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (-2..2).forEach { offset ->
            FilterChip(selected = state.prayerSettings.hijriDateOffset == offset, onClick = { actions.setHijriDateOffset(offset) },
                label = { Text(if (offset > 0) "+$offset" else "$offset", style = MaterialTheme.typography.labelLarge.copy(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr)) })
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun SettingsChoice(selected: String, options: List<String>, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(selected) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { expanded = false; onSelect(index) })
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
