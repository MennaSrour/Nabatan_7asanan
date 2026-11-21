package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun GardenCard(state: HomeUiState, onWater: () -> Unit) {
    var expanded by rememberSaveable(state.gardenProfileId) { mutableStateOf(false) }
    if (state.garden == null && !state.isGardenUnavailable) return
    Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(28.dp),
        color = SummerTheme.colors.dialogBackground, border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.6f)), shadowElevation = 4.dp) {
        GardenContent(state, onWater, expanded = false, onToggleSize = { expanded = true })
    }
    if (expanded) Dialog(onDismissRequest = { expanded = false }) {
        Surface(shape = RoundedCornerShape(28.dp), color = SummerTheme.colors.dialogBackground) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                GardenContent(state, onWater, expanded = true, onToggleSize = { expanded = false })
            }
        }
    }
}

@Composable
private fun GardenContent(state: HomeUiState, onWater: () -> Unit, expanded: Boolean, onToggleSize: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.garden_title), modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold, color = SummerTheme.colors.textDarkBrown)
            IconButton(onClick = onToggleSize) {
                Icon(if (expanded) Icons.Rounded.Close else Icons.Rounded.OpenInFull,
                    stringResource(if (expanded) R.string.garden_close else R.string.garden_expand), tint = SummerTheme.colors.highlightAccent)
            }
        }
        val garden = state.garden
        if (garden == null) Text(stringResource(R.string.garden_unavailable), color = SummerTheme.colors.textBrown)
        else {
            GardenScene(garden.stage, state.gardenProfileId, state.gardenWateringSequence,
                stringResource(R.string.garden_scene_description, gardenStageName(garden.stage)),
                Modifier.fillMaxWidth().height(if (expanded) 360.dp else 250.dp).clip(RoundedCornerShape(22.dp)))
            GardenControls(state, garden, onWater)
        }
    }
}
