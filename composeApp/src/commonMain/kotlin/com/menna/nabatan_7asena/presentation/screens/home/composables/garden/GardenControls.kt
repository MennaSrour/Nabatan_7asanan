package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.GardenProgress
import com.menna.nabatan_7asena.domain.entity.GardenStageThresholds
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
internal fun GardenControls(state: HomeUiState, garden: GardenProgress, onWater: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(gardenStageName(garden.stage), fontWeight = FontWeight.ExtraBold, color = SummerTheme.colors.textDarkBrown)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.WaterDrop, null, tint = SummerTheme.colors.highlightAccent, modifier = Modifier.size(20.dp))
            Text(stringResource(R.string.garden_drops, garden.waterDrops), color = SummerTheme.colors.textBrown)
        }
    }
    GardenGrowthMeter(garden)
    if (state.gardenNeedsWater) Text(stringResource(R.string.garden_needs_water),
        style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.highlightAccent)
    GardenWaterButton(garden.waterDrops > 0 && !state.isWateringGarden && !state.isGardenUnavailable, onWater)
    Text(stringResource(if (garden.waterDrops == 0) R.string.garden_earn_water else R.string.garden_water_hint),
        style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.textBrown)
    if (state.gardenSaveFailed) Text(stringResource(R.string.garden_save_failed), color = SummerTheme.colors.errorRed)
    if (garden.syncPending && state.gardenProfileId != "local") Text(stringResource(R.string.garden_saved_locally),
        style = MaterialTheme.typography.labelSmall, color = SummerTheme.colors.textBrown)
}

@Composable
private fun GardenGrowthMeter(garden: GardenProgress) {
    val animatedXp by animateFloatAsState(garden.xp.toFloat(), tween(1500), label = "gardenXp")
    val stage = GardenStageThresholds.indexOfLast { animatedXp >= it }.coerceAtLeast(0)
    val fraction = if (stage == 5) 1f else ((animatedXp - GardenStageThresholds[stage]) /
        (GardenStageThresholds[stage + 1] - GardenStageThresholds[stage])).coerceIn(0f, 1f)
    LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(50)),
        color = Color(0xFF438F60), trackColor = SummerTheme.colors.goldWarm.copy(alpha = 0.2f))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(6) { index -> Box(Modifier.size(if (index == garden.stage) 10.dp else 7.dp)
            .background(if (index <= garden.stage) Color(0xFF438F60) else SummerTheme.colors.textBrown.copy(0.2f), CircleShape)) }
    }
    Text(if (garden.stage == 5) stringResource(R.string.garden_fully_grown)
        else stringResource(R.string.garden_next_stage, GardenStageThresholds[garden.stage + 1] - garden.xp),
        style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.textBrown)
}

@Composable
private fun GardenWaterButton(enabled: Boolean, onWater: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Box(Modifier.matchParentSize().offset(y = 4.dp).clip(RoundedCornerShape(18.dp)).background(SummerTheme.colors.goldDeep))
        Button(onClick = onWater, enabled = enabled, shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SummerTheme.colors.goldWarm, contentColor = SummerTheme.colors.textDarkBrown),
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
            Icon(Icons.Rounded.WaterDrop, null)
            Spacer(Modifier.width(8.dp))
            Text(genderedStringResource(GenderedStrings.gardenWater), fontWeight = FontWeight.ExtraBold)
        }
    }
}
