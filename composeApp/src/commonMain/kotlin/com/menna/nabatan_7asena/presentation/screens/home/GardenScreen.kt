package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.GardenProduct
import com.menna.nabatan_7asena.domain.entity.plotCount
import com.menna.nabatan_7asena.presentation.screens.home.composables.garden.*
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme

@Composable
fun GardenScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var panel by rememberSaveable(state.gardenProfileId) { mutableStateOf("") }
    var inventoryTab by rememberSaveable(state.gardenProfileId) { mutableIntStateOf(0) }
    var selected by rememberSaveable(state.gardenProfileId) { mutableIntStateOf(0) }
    val garden = state.garden
    val selectedPlot = selected.coerceIn(0, ((garden?.plotCount ?: 1) - 1).coerceAtLeast(0))
    val closePanel = { panel = "" }
    val openInventory = { inventoryTab = 0; panel = "inventory" }

    NabataAppTheme(state.isNightTheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            GardenWorldContent(
                state, selectedPlot,
                onPlot = { plot ->
                    selected = plot
                    if (garden?.trees?.any { it.plotIndex == plot } == true) panel = "plot"
                    else openInventory()
                },
                onBack = onBack,
                onShop = { panel = "shop" },
                onInventory = openInventory,
                onBuyPlot = { viewModel.buyGardenProduct(GardenProduct.LAND) }
            )
            when (panel) {
                "shop" -> GardenShopSheet(state, viewModel::buyGardenProduct, onInventory = { product ->
                    inventoryTab = if (product.species != null) 0 else 1
                    panel = "inventory"
                }, onDismiss = closePanel)
                "inventory" -> GardenInventorySheet(state, viewModel, selectedPlot, { selected = it },
                    initialTab = inventoryTab, onDismiss = closePanel)
                "plot" -> GardenPlotSheet(state, viewModel, selectedPlot, openInventory, closePanel)
            }
        }
    }
}
