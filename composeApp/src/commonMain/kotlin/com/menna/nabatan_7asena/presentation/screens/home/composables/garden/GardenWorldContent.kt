package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.domain.entity.GardenProduct
import com.menna.nabatan_7asena.domain.entity.GardenProgress
import com.menna.nabatan_7asena.domain.entity.gardenShopBalance
import com.menna.nabatan_7asena.domain.entity.plotCount
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

internal val GardenCream = Color(0xFFFFF8E6)
internal val GardenInk = Color(0xFF33354C)
internal val GardenGold = Color(0xFFFFDEA0)
internal val GardenGoldEdge = Color(0xFFD29142)
internal val GardenLavender = Color(0xFFE9DDF9)
internal val GardenLavenderEdge = Color(0xFFAA8BCB)

@Composable
internal fun GardenWorldContent(
    state: HomeUiState,
    selectedPlot: Int,
    onPlot: (Int) -> Unit,
    onBack: () -> Unit,
    onShop: () -> Unit,
    onInventory: () -> Unit,
    onBuyPlot: () -> Unit
) {
    val night = state.isNightTheme
    val background = if (night) listOf(Color(0xFF1D2946), Color(0xFF30494D))
        else listOf(Color(0xFFF0FAFF), Color(0xFFE3F0CB))
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(background)).safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        BoxWithConstraints(Modifier.widthIn(max = 1120.dp).fillMaxSize()) {
            val spacing = if (maxWidth < 400.dp) 8.dp else 14.dp
            val columns = gardenGridColumns(maxWidth.value, spacing.value)
            val garden = state.garden
            val balance = gardenShopBalance(state.gardenEarnedStars, garden?.spentStars ?: 0)
            val phase = rememberGardenPhase()
            Column(Modifier.fillMaxSize()) {
                key(state.gardenProfileId) {
                    val grid = rememberLazyGridState()
                    val plotCount = garden?.plotCount ?: 0
                    var previousPlots by remember { mutableIntStateOf(plotCount) }
                    LaunchedEffect(plotCount) {
                        val grew = previousPlots > 0 && plotCount > previousPlots
                        previousPlots = plotCount
                        if (grew) grid.animateScrollToItem(plotCount)
                    }
                    val trees = remember(garden?.trees) { garden?.trees.orEmpty().associateBy { it.plotIndex } }
                    val decorations = remember(garden?.placements) {
                        garden?.placements.orEmpty().filter { it.visible }.groupBy { it.plotIndex }
                    }
                    val water = rememberGardenWateringProgress(state.gardenProfileId, state.gardenWateringSequence)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        state = grid,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(spacing),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        item(key = "sky", span = { GridItemSpan(maxLineSpan) }) {
                            GardenHero(night, phase, balance, garden?.waterDrops ?: 0, onBack)
                        }
                        if (garden == null) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(stringResource(R.string.garden_unavailable), color = SummerTheme.colors.textPrimary)
                            }
                        } else {
                            items(plotCount, key = { it }) { plot ->
                                val tree = trees[plot]
                                val selected = selectedPlot == plot
                                val label = "مساحة ${arabicIndic(plot + 1)}"
                                ChunkyButton(
                                    onClick = { onPlot(plot) },
                                    color = GardenCream,
                                    edgeColor = if (selected) GardenGoldEdge else Color(0xFF79A366),
                                    faceBorder = if (selected) GardenGoldEdge else Color.White,
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(19.dp))) {
                                        GardenPlotArtwork(
                                            tree, decorations[plot].orEmpty(), selected, night, phase,
                                            water = { if (water.isRunning && tree?.id == state.gardenWateredTreeId) water.value else 0f },
                                            modifier = Modifier.matchParentSize()
                                        )
                                        Surface(
                                            modifier = Modifier.align(Alignment.TopStart).padding(5.dp),
                                            color = GardenCream.copy(alpha = 0.96f),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Text(label, Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                color = GardenInk, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        }
                                        if (tree == null) Icon(Icons.Rounded.Add, null, Modifier.align(Alignment.Center), tint = GardenInk)
                                    }
                                }
                            }
                            item(key = "new-plot") {
                                BuyPlotTile(balance, !state.isGardenShopBusy && !state.isGardenUnavailable, onBuyPlot)
                            }
                        }
                    }
                }
                state.gardenShopError?.let {
                    Text(genderedStringResource(it), Modifier.padding(horizontal = spacing, vertical = 4.dp), color = SummerTheme.colors.errorRed)
                }
                Surface(
                    color = if (night) Color(0xFF2B3850) else GardenCream,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    shadowElevation = 5.dp
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = spacing, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(spacing)) {
                        GardenTab(stringResource(R.string.shop_title), Icons.Rounded.Storefront, GardenGold, GardenGoldEdge, onShop, Modifier.weight(1f))
                        GardenTab(stringResource(R.string.world_inventory), Icons.Rounded.Inventory2, GardenLavender, GardenLavenderEdge, onInventory, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun GardenHero(night: Boolean, phase: State<Float>, stars: Int, water: Int, onBack: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))) {
        val minHeight = (maxWidth * 0.37f).coerceIn(112.dp, 168.dp)
        Box(Modifier.fillMaxWidth().heightIn(min = minHeight)) {
            Canvas(Modifier.matchParentSize()) { drawGardenLandscape(phase.value, night) }
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledIconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = GardenCream, contentColor = GardenInk)
                    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.garden_close)) }
                    Text(stringResource(R.string.garden_title), color = if (night) GardenCream else GardenInk,
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GardenCounter(Icons.Rounded.Star, "${arabicIndic(stars)} نجمة", Color(0xFF986217), Modifier.weight(1f, fill = false))
                    GardenCounter(Icons.Rounded.WaterDrop, "${arabicIndic(water)} قطرة", Color(0xFF287692), Modifier.weight(1f, fill = false))
                }
            }
        }
    }
}

@Composable
private fun GardenCounter(icon: ImageVector, label: String, tint: Color, modifier: Modifier) {
    Surface(modifier, color = GardenCream, shape = RoundedCornerShape(50), shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color.White)) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(icon, null, Modifier.size(18.dp), tint = tint)
            Text(label, color = GardenInk, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BuyPlotTile(balance: Int, available: Boolean, onBuy: () -> Unit) {
    val price = GardenProduct.LAND.price
    ChunkyButton(
        onClick = onBuy,
        enabled = available && balance >= price,
        color = GardenLavender,
        edgeColor = GardenLavenderEdge,
        faceBrush = Brush.verticalGradient(listOf(GardenCream, GardenLavender)),
        faceBorder = Color.White,
        contentPadding = PaddingValues(2.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().aspectRatio(1f).padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.Add, null, Modifier.size(26.dp), tint = GardenInk)
            Text("+ مساحة جديدة", textAlign = TextAlign.Center, color = GardenInk,
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
            Text("${arabicIndic(price)} نجمة", color = GardenInk, style = MaterialTheme.typography.labelMedium)
            if (balance < price) Text("تحتاج إلى ${arabicIndic(price - balance)} نجمة",
                color = GardenInk, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun GardenTab(label: String, icon: ImageVector, face: Color, edge: Color, onClick: () -> Unit, modifier: Modifier) {
    ChunkyButton(onClick, face, edge, modifier, depth = 4.dp, contentPadding = PaddingValues(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = GardenInk)
            Text(label, color = GardenInk, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun GardenPlotChoices(garden: GardenProgress, selected: Int, onSelect: (Int) -> Unit) {
    val list = rememberLazyListState(initialFirstVisibleItemIndex = selected)
    LaunchedEffect(selected) {
        if (list.layoutInfo.visibleItemsInfo.none { it.index == selected }) list.scrollToItem(selected)
    }
    LazyRow(Modifier.fillMaxWidth(), state = list, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(garden.plotCount, key = { it }) { plot ->
            FilterChip(selected = selected == plot, onClick = { onSelect(plot) },
                label = { Text("مساحة ${arabicIndic(plot + 1)}") })
        }
    }
}
