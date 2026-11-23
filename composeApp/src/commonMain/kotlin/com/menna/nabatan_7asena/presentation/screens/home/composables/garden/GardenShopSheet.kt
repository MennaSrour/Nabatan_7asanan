package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.core.util.arabicIndic
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenShopSheet(state: HomeUiState, onBuy: (GardenProduct) -> Unit, onInventory: (GardenProduct) -> Unit, onDismiss: () -> Unit) {
    val garden = state.garden ?: return
    val balance = gardenShopBalance(state.gardenEarnedStars, garden.spentStars)
    val phase = rememberGardenPhase()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SummerTheme.colors.dialogBackground) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.shop_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text("رصيد المتجر: ${arabicIndic(balance)} نجمة", fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.shop_balance_hint), style = MaterialTheme.typography.labelSmall)
            ShopCelebration(state.gardenProfileId, state.gardenPurchaseSequence)
            state.gardenShopError?.let { Text(genderedStringResource(it), color = SummerTheme.colors.errorRed) }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(listOf(GardenProduct.LAND) + GardenProduct.entries.filterNot { it == GardenProduct.LAND }, key = { it.id }) { product ->
                    ShopProductRow(product, garden, balance, state.isGardenShopBusy || state.isGardenUnavailable,
                        phase, state.isNightTheme, onBuy, onInventory)
                }
            }
        }
    }
}

@Composable
private fun ShopProductRow(product: GardenProduct, garden: GardenProgress, balance: Int, busy: Boolean,
    phase: State<Float>, night: Boolean, onBuy: (GardenProduct) -> Unit, onInventory: (GardenProduct) -> Unit) {
    val owned = garden.ownedQuantity(product)
    Surface(shape = RoundedCornerShape(20.dp), color = SummerTheme.colors.cardBackground,
        border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.4f))) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GardenProductPreview(product, phase, night, Modifier.size(76.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(gardenProductName(product), fontWeight = FontWeight.Bold, color = SummerTheme.colors.textDarkBrown)
                Text(if (product == GardenProduct.LAND) stringResource(R.string.world_expansion_hint, garden.plotCount)
                    else stringResource(R.string.world_owned_quantity, owned), style = MaterialTheme.typography.bodySmall)
                Text("${arabicIndic(product.price)} نجمة")
                ChunkyButton(onClick = { onBuy(product) }, enabled = !busy && balance >= product.price,
                    color = GardenGold, edgeColor = GardenGoldEdge, depth = 4.dp, contentPadding = PaddingValues(8.dp)) {
                    Text(if (product == GardenProduct.LAND) "+ مساحة جديدة" else genderedStringResource(GenderedStrings.shopBuy),
                        color = GardenInk, fontWeight = FontWeight.Bold)
                }
                if (balance < product.price) Text("تحتاج إلى ${arabicIndic(product.price - balance)} نجمة", style = MaterialTheme.typography.bodySmall)
                if (owned > 0 && product != GardenProduct.LAND) TextButton(onClick = { onInventory(product) }) { Text(stringResource(R.string.world_place_manage)) }
            }
        }
    }
}

@Composable
internal fun GardenProductPreview(product: GardenProduct, phase: State<Float>, night: Boolean, modifier: Modifier) {
    Canvas(modifier.background(SummerTheme.colors.highlightAccent.copy(0.08f), RoundedCornerShape(16.dp))) {
        val species = product.species
        if (species != null) {
            val scale = size.minDimension / 500f
            withTransform({ translate(size.width / 2 - 500f * scale, size.height - 565f * scale); scale(scale, scale, Offset.Zero) }) {
                drawGardenTree(GardenFrame(5f, phase.value, 0f, night, species = species))
            }
        } else if (product.decoration != null) drawShopDecoration(requireNotNull(product.decoration), center, size.width / 110f, phase.value, night)
        else {
            drawRoundRect(Color(0xFF9FCDA2), Offset(4f, size.height * 0.4f), Size(size.width - 8f, size.height * 0.5f), CornerRadius(10f))
            drawOval(Color(0xFFB48D67), Offset(size.width * 0.22f, size.height * 0.57f), Size(size.width * 0.56f, size.height * 0.23f))
        }
    }
}

@Composable
internal fun gardenProductName(product: GardenProduct): String = stringResource(when (product) {
    GardenProduct.PALM -> R.string.world_palm
    GardenProduct.APPLE -> R.string.world_apple
    GardenProduct.OLIVE -> R.string.world_olive
    GardenProduct.BLOSSOM -> R.string.world_blossom
    GardenProduct.BUTTERFLIES -> R.string.shop_butterflies
    GardenProduct.BIRDS -> R.string.shop_birds
    GardenProduct.FLOWERS -> R.string.shop_flowers
    GardenProduct.LANTERN -> R.string.shop_lantern
    GardenProduct.RAINBOW -> R.string.shop_rainbow
    GardenProduct.SPARKLES -> R.string.shop_sparkles
    GardenProduct.BENCH -> R.string.world_bench
    GardenProduct.FOUNTAIN -> R.string.world_fountain
    GardenProduct.LAND -> R.string.world_expand
})

@Composable
internal fun gardenSpeciesName(species: GardenSpecies) = gardenProductName(GardenProduct.entries.first { it.id == species.id })

@Composable
private fun ShopCelebration(profileId: String, sequence: Long) {
    val animation = remember(profileId) { Animatable(1f) }
    var previous by remember(profileId) { mutableLongStateOf(sequence) }
    LaunchedEffect(profileId, sequence) {
        if (sequence <= previous) return@LaunchedEffect
        previous = sequence
        animation.snapTo(0f)
        animation.animateTo(1f, tween(1600))
    }
    if (animation.isRunning) Box(Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            repeat(18) { index ->
                val p = animation.value
                drawShopStar(Offset(size.width / 2 + (index - 9) * 16f * p, size.height / 2 - p * 50f + (index % 3) * 13f),
                    3f + index % 3 * 2, listOf(Color(0xFFFFCB64), Color(0xFFD5B2EC), Color(0xFF9BCEBF))[index % 3].copy(1f - p))
            }
        }
        Text(stringResource(R.string.shop_celebrate), color = SummerTheme.colors.highlightAccent,
            fontWeight = FontWeight.ExtraBold, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
    }
}
