package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun gardenStageName(stage: Int): String = stringResource(listOf(R.string.garden_seed, R.string.garden_sprout,
    R.string.garden_sapling, R.string.garden_young, R.string.garden_blooming, R.string.garden_fruiting)[stage.coerceIn(0, 5)])

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GardenPlotSheet(
    state: HomeUiState, actions: HomeActions, plot: Int, onInventory: () -> Unit, onDismiss: () -> Unit
) {
    val garden = state.garden ?: return
    val tree = garden.trees.firstOrNull { it.plotIndex == plot }
    val decorations = remember(garden.placements, plot) { garden.placements.filter { it.visible && it.plotIndex == plot } }
    val phase = rememberGardenPhase()
    val water = rememberGardenWateringProgress(state.gardenProfileId, state.gardenWateringSequence)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SummerTheme.colors.dialogBackground
    ) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("مساحة ${arabicIndic(plot + 1)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            GardenPlotArtwork(
                tree, decorations, selected = true, night = state.isNightTheme, phase = phase,
                water = { if (water.isRunning && tree?.id == state.gardenWateredTreeId) water.value else 0f },
                modifier = Modifier.fillMaxWidth().height(128.dp).clip(RoundedCornerShape(18.dp))
            )
            SelectedTreeControls(state, tree) { actions.waterGarden(tree?.id) }
            if (tree != null) {
                TextButton(enabled = !state.isGardenShopBusy, onClick = { actions.showGardenTree(tree.id, !tree.visible) }) {
                    Text(stringResource(if (tree.visible) R.string.world_hide else R.string.world_show))
                }
            }
            state.gardenShopError?.let { Text(genderedStringResource(it), color = SummerTheme.colors.errorRed) }
            TextButton(onClick = onInventory) { Text(stringResource(R.string.world_inventory)) }
        }
    }
}

@Composable
internal fun SelectedTreeControls(state: HomeUiState, tree: GardenTree?, onWater: () -> Unit) {
    if (tree == null) {
        Text(stringResource(R.string.world_empty_plot), color = SummerTheme.colors.textBrown)
        return
    }
    Text(gardenSpeciesName(tree.species) + " · " + gardenStageName(tree.stage), fontWeight = FontWeight.ExtraBold,
        color = SummerTheme.colors.textDarkBrown)
    key(tree.id) { TreeGrowthMeter(tree) }
    val today = state.prayerDate?.toString() ?: com.menna.nabatan_7asena.platform.AppClock.today().toString()
    if (tree.lastWateredDate == null || tree.lastWateredDate < today) Text(stringResource(R.string.garden_needs_water),
        style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.highlightAccent)
    ChunkyButton(
        onClick = onWater,
        enabled = tree.visible && (state.garden?.waterDrops ?: 0) > 0 && !state.isWateringGarden,
        color = GardenGold,
        edgeColor = GardenGoldEdge
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.WaterDrop, null, tint = GardenInk)
            Text(genderedStringResource(GenderedStrings.gardenWater), color = GardenInk, fontWeight = FontWeight.Bold)
        }
    }
    Text(stringResource(R.string.garden_earn_water), style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.textBrown)
    if (state.gardenSaveFailed) Text(stringResource(R.string.garden_save_failed), color = SummerTheme.colors.errorRed)
}

@Composable
private fun TreeGrowthMeter(tree: GardenTree) {
    val xp by animateFloatAsState(tree.xp.toFloat(), tween(1500), label = "selectedTreeXp")
    val stage = GardenStageThresholds.indexOfLast { xp >= it }.coerceAtLeast(0)
    val fraction = if (stage == 5) 1f else ((xp - GardenStageThresholds[stage]) /
        (GardenStageThresholds[stage + 1] - GardenStageThresholds[stage])).coerceIn(0f, 1f)
    LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth().height(12.dp))
    Text(if (tree.stage == 5) stringResource(R.string.garden_fully_grown)
        else stringResource(R.string.garden_next_stage, GardenStageThresholds[tree.stage + 1] - tree.xp),
        style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.textBrown)
}
