package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.screens.home.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun GardenWorldContent(state: HomeUiState, actions: HomeActions, selectedPlot: Int, onPlot: (Int) -> Unit,
    onBack: () -> Unit, onShop: () -> Unit, onInventory: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.garden_close)) }
            Text(stringResource(R.string.garden_title), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onShop, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.shop_title)) }
            FilledTonalButton(onClick = onInventory, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.world_inventory)) }
        }
        val garden = state.garden
        if (garden == null) Text(stringResource(R.string.garden_unavailable))
        else {
            Text(stringResource(R.string.world_entry_stats, garden.trees.count { it.plotIndex != null }, garden.waterDrops))
            key(state.gardenProfileId) { GardenWorldCanvas(state, garden, selectedPlot, onPlot) }
            GardenPlotChoices(garden, selectedPlot, onPlot)
            val selectedTree = garden.trees.firstOrNull { it.plotIndex == selectedPlot }
            Text(stringResource(R.string.world_selected_plot, selectedPlot + 1), color = SummerTheme.colors.textBrown)
            SelectedTreeControls(state, selectedTree) { actions.waterGarden(selectedTree?.id) }
            if (selectedTree != null) TextButton(onClick = { actions.showGardenTree(selectedTree.id, !selectedTree.visible) }) {
                Text(stringResource(if (selectedTree.visible) R.string.world_hide else R.string.world_show))
            }
            state.gardenShopError?.let { Text(com.menna.nabatan_7asena.presentation.common.genderedStringResource(it), color = SummerTheme.colors.errorRed) }
        }
    }
}

@Composable
internal fun GardenPlotChoices(garden: GardenProgress, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(garden.plotCount) { plot ->
            FilterChip(selected = selected == plot, onClick = { onSelect(plot) }, label = {
                Text(stringResource(R.string.world_plot_chip, plot + 1) + if (garden.trees.any { it.plotIndex == plot }) " •" else " +")
            })
        }
    }
}
