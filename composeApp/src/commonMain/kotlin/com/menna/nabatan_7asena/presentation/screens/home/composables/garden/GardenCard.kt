package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun GardenCard(state: HomeUiState, onEnter: () -> Unit) {
    val garden = state.garden
    if (garden == null && !state.isGardenUnavailable) return
    Surface(onClick = onEnter, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp), color = SummerTheme.colors.dialogBackground,
        border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.6f)), shadowElevation = 3.dp) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(androidx.compose.material.icons.Icons.Rounded.Park, null,
                tint = androidx.compose.ui.graphics.Color(0xFF438F60), modifier = Modifier.size(42.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.garden_title), fontWeight = FontWeight.ExtraBold, color = SummerTheme.colors.textDarkBrown)
                Text(if (garden == null) stringResource(R.string.garden_unavailable) else gardenStageName(garden.stage) + " · " + stringResource(R.string.garden_drops, garden.waterDrops), color = SummerTheme.colors.textBrown)
                Text(com.menna.nabatan_7asena.presentation.common.genderedStringResource(com.menna.nabatan_7asena.presentation.common.GenderedStrings.gardenEnter), color = SummerTheme.colors.highlightAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun GardenContent(state: HomeUiState, onWater: () -> Unit, expanded: Boolean, onToggleSize: () -> Unit) {
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
                Modifier.fillMaxWidth().height(380.dp).clip(RoundedCornerShape(22.dp)), night = state.isNightTheme)
            GardenControls(state, garden, onWater)
        }
    }
}
