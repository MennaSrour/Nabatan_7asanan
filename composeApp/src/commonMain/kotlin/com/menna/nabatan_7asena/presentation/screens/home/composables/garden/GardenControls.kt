package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    Button(onClick = onWater, enabled = tree.visible && (state.garden?.waterDrops ?: 0) > 0 && !state.isWateringGarden,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SummerTheme.colors.goldWarm, contentColor = SummerTheme.colors.textDarkBrown)) {
        Icon(Icons.Rounded.WaterDrop, null); Spacer(Modifier.width(8.dp))
        Text(genderedStringResource(GenderedStrings.gardenWater))
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
