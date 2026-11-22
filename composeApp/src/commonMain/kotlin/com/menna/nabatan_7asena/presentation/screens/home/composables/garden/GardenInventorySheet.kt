package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.screens.home.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GardenInventorySheet(state: HomeUiState, actions: HomeActions, selected: Int, onPlot: (Int) -> Unit, initialTab: Int = 0, onDismiss: () -> Unit) {
    val garden = state.garden ?: return
    val phase = rememberGardenPhase()
    var tab by rememberSaveable(state.gardenProfileId, initialTab) { mutableIntStateOf(initialTab) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SummerTheme.colors.dialogBackground) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.world_inventory), style = MaterialTheme.typography.titleLarge)
            GardenPlotChoices(garden, selected, onPlot)
            Text(stringResource(R.string.world_plot_capacity), style = MaterialTheme.typography.bodySmall)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(R.string.world_tree_inventory, R.string.world_decoration_inventory, R.string.world_placed_inventory).forEachIndexed { index, title ->
                    FilterChip(selected = tab == index, onClick = { tab = index }, label = { Text(stringResource(title)) })
                }
            }
            state.gardenShopError?.let { Text(com.menna.nabatan_7asena.presentation.common.genderedStringResource(it), color = SummerTheme.colors.errorRed) }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (tab == 0) items(garden.trees.sortedBy { it.plotIndex != null }, key = { "tree-${it.id}" }) { tree ->
                    Surface(shape = MaterialTheme.shapes.large, color = SummerTheme.colors.cardBackground) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(gardenSpeciesName(tree.species) + " · " + gardenStageName(tree.stage), fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.world_tree_xp, tree.xp), style = MaterialTheme.typography.bodySmall)
                            Text(tree.plotIndex?.let { stringResource(R.string.world_selected_plot, it + 1) } ?: stringResource(R.string.world_in_inventory))
                            Button(onClick = { actions.plantGardenTree(tree.id, selected) },
                                enabled = !state.isGardenShopBusy && tree.plotIndex != selected && garden.trees.none { it.plotIndex == selected }) {
                                Text(stringResource(if (tree.plotIndex == null) R.string.world_plant_here else R.string.world_move_here))
                            }
                            if (tree.plotIndex != null) {
                                InventoryVisibility(tree.visible, state.isGardenShopBusy) { actions.showGardenTree(tree.id, it) }
                                TextButton(onClick = { actions.plantGardenTree(tree.id, null) }, enabled = !state.isGardenShopBusy) { Text(stringResource(R.string.world_return_inventory)) }
                            }
                        }
                    }
                }
                if (tab == 1 && garden.quantities.isEmpty()) item { Text(stringResource(R.string.world_inventory_empty)) }
                if (tab == 1) items(GardenDecoration.entries.filter { garden.quantities.getOrElse(it.id) { 0 } > 0 }, key = { "stock-${it.id}" }) { item ->
                    val product = GardenProduct.entries.first { it.id == item.id }
                    Surface(shape = MaterialTheme.shapes.large, color = SummerTheme.colors.cardBackground) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            GardenProductPreview(product, phase, state.isNightTheme, Modifier.size(52.dp))
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(gardenProductName(product), fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.world_available_quantity, garden.availableDecoration(item.id)))
                                Button(onClick = { actions.placeGardenDecoration(item, selected) }, enabled = !state.isGardenShopBusy &&
                                    garden.availableDecoration(item.id) > 0 && garden.placements.count { it.plotIndex == selected } < GARDEN_DECORATION_SLOTS) {
                                    Text(stringResource(R.string.world_place_here))
                                }
                            }
                        }
                    }
                }
                if (tab == 2 && garden.placements.isEmpty()) item { Text(stringResource(R.string.world_no_placed)) }
                if (tab == 2) items(garden.placements, key = { "placed-${it.id}" }) { placed ->
                    Surface(shape = MaterialTheme.shapes.large, color = SummerTheme.colors.cardBackground) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            val product = GardenProduct.entries.firstOrNull { it.id == placed.itemId }
                            Text((product?.let { gardenProductName(it) } ?: stringResource(R.string.world_decoration)) + " · " + stringResource(R.string.world_selected_plot, placed.plotIndex + 1))
                            InventoryVisibility(placed.visible, state.isGardenShopBusy) { actions.manageGardenPlacement(placed.id, visible = it) }
                            Row {
                                TextButton(onClick = { actions.manageGardenPlacement(placed.id, plot = selected) }, enabled = !state.isGardenShopBusy &&
                                    placed.plotIndex != selected && garden.placements.count { it.plotIndex == selected } < GARDEN_DECORATION_SLOTS) { Text(stringResource(R.string.world_move_here)) }
                                TextButton(onClick = { actions.manageGardenPlacement(placed.id, remove = true) }, enabled = !state.isGardenShopBusy) { Text(stringResource(R.string.world_return_inventory)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryVisibility(visible: Boolean, busy: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(if (visible) R.string.world_visible else R.string.world_hidden), modifier = Modifier.weight(1f))
        Switch(visible, onCheckedChange = onChange, enabled = !busy)
    }
}
