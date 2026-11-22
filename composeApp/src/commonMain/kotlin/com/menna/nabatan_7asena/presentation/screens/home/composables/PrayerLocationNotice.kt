package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource

@Composable
internal fun PrayerLocationNotice(state: HomeUiState, onSettings: () -> Unit) {
    TextButton(onClick = onSettings, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(if (state.needsPrayerLocation) stringResource(R.string.prayer_choose_location)
            else stringResource(R.string.prayer_location_used, state.prayerLocationLabel))
    }
}
