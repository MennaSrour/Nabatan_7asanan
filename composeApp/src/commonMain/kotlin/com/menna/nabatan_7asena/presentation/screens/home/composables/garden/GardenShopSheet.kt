package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenShopSheet(state: HomeUiState, onBuy: (GardenDecoration) -> Unit,
    onEquip: (GardenDecoration, Boolean) -> Unit, onDismiss: () -> Unit) {
    val garden = state.garden ?: return
    val balance = gardenShopBalance(state.gardenEarnedStars, garden.spentStars)
    val phase = rememberShopPhase()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SummerTheme.colors.dialogBackground) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.shop_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, null, tint = SummerTheme.colors.goldWarm)
                Text(stringResource(R.string.shop_balance, balance), fontWeight = FontWeight.Bold)
            }
            Text(stringResource(R.string.shop_balance_hint), style = MaterialTheme.typography.bodySmall)
            ShopCelebration(state.gardenProfileId, state.gardenPurchaseSequence)
            state.gardenShopError?.let { Text(genderedStringResource(it), color = SummerTheme.colors.errorRed) }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(GardenDecoration.entries, key = { it.id }) { item ->
                    ShopItemRow(item, garden, balance, state.isGardenShopBusy || state.isGardenUnavailable,
                        phase, state.isNightTheme, onBuy, onEquip)
                }
            }
        }
    }
}

@Composable
private fun rememberShopPhase(): State<Float> {
    val active = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value.isAtLeast(Lifecycle.State.STARTED)
    return if (active) rememberInfiniteTransition(label = "shopIdle").animateFloat(0f, 1f,
        infiniteRepeatable(tween(12000, easing = LinearEasing)), label = "shopDecorations") else remember { mutableStateOf(0f) }
}

@Composable
private fun ShopItemRow(item: GardenDecoration, garden: GardenProgress, balance: Int, busy: Boolean,
    phase: State<Float>, night: Boolean, onBuy: (GardenDecoration) -> Unit, onEquip: (GardenDecoration, Boolean) -> Unit) {
    val owned = item.id in garden.ownedItems
    Surface(shape = RoundedCornerShape(20.dp), color = SummerTheme.colors.cardBackground,
        border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.4f))) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Canvas(Modifier.size(76.dp).background(SummerTheme.colors.highlightAccent.copy(0.08f), RoundedCornerShape(16.dp))) {
                drawShopDecoration(item, center, size.width / 110f, phase.value, night)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(shopItemName(item), fontWeight = FontWeight.Bold, color = SummerTheme.colors.textDarkBrown)
                if (owned) ShopOwnedToggle(item, item.id in garden.equippedItems, busy, onEquip)
                else {
                    Text(stringResource(R.string.shop_price, item.price), color = SummerTheme.colors.textBrown)
                    Button(onClick = { onBuy(item) }, enabled = !busy && balance >= item.price) {
                        Text(genderedStringResource(GenderedStrings.shopBuy))
                    }
                    if (balance < item.price) Text(stringResource(R.string.shop_more_stars, item.price - balance), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ShopOwnedToggle(item: GardenDecoration, equipped: Boolean, busy: Boolean, onEquip: (GardenDecoration, Boolean) -> Unit) {
    val title = shopItemName(item)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(if (equipped) R.string.shop_equipped else R.string.shop_unequipped),
            style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Switch(equipped, onCheckedChange = { onEquip(item, it) }, enabled = !busy,
            modifier = Modifier.semantics { contentDescription = title })
    }
}

@Composable
private fun shopItemName(item: GardenDecoration): String = stringResource(when (item) {
    GardenDecoration.BUTTERFLIES -> R.string.shop_butterflies
    GardenDecoration.BIRDS -> R.string.shop_birds
    GardenDecoration.FLOWERS -> R.string.shop_flowers
    GardenDecoration.LANTERN -> R.string.shop_lantern
    GardenDecoration.RAINBOW -> R.string.shop_rainbow
    GardenDecoration.SPARKLES -> R.string.shop_sparkles
})

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
