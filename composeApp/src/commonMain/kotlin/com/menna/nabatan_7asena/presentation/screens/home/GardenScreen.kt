package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.home.composables.garden.*
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun GardenScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var panel by rememberSaveable(state.gardenProfileId) { mutableStateOf("") }
    var inventoryTab by rememberSaveable(state.gardenProfileId) { mutableIntStateOf(0) }
    var selected by rememberSaveable(state.gardenProfileId) { mutableIntStateOf(0) }
    val selectPlot: (Int) -> Unit = { selected = it }
    NabataAppTheme(state.isNightTheme) {
        Surface(Modifier.fillMaxSize(), color = SummerTheme.colors.backgroundLight) {
            Column(Modifier.safeDrawingPadding().verticalScroll(rememberScrollState())) {
                GardenWorldContent(state, viewModel, selected, onPlot = { plot ->
                    selected = plot
                    if (state.garden?.trees?.none { it.plotIndex == plot } == true) { inventoryTab = 0; panel = "inventory" }
                }, onBack = onBack, onShop = { panel = "shop" }, onInventory = { inventoryTab = 0; panel = "inventory" })
            }
        }
        if (panel == "shop") GardenShopSheet(state, viewModel::buyGardenProduct, onInventory = { product -> inventoryTab = if (product.species != null) 0 else 1; panel = "inventory" }, onDismiss = { panel = "" })
        if (panel == "inventory") GardenInventorySheet(state, viewModel, selected, selectPlot, initialTab = inventoryTab, onDismiss = { panel = "" })
    }
}
