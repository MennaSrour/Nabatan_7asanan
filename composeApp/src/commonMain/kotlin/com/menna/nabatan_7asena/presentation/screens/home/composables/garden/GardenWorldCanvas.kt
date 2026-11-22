package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import kotlin.math.*

@Composable
internal fun GardenWorldCanvas(state: HomeUiState, garden: GardenProgress, selectedPlot: Int, onPlot: (Int) -> Unit) {
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val plotPixels = with(density) { 260.dp.toPx() }
    val phase = rememberGardenPhase()
    val planted = remember(garden.trees) { garden.trees.filter { it.plotIndex != null }.associateBy { it.plotIndex } }
    val growth = planted.values.associate { tree -> tree.id to key(tree.id) { animateFloatAsState(tree.stage.toFloat(), tween(1500), label = "treeGrowth") } }
    val water = remember(state.gardenProfileId) { Animatable(0f) }
    var previous by remember(state.gardenProfileId) { mutableLongStateOf(state.gardenWateringSequence) }
    LaunchedEffect(state.gardenWateringSequence) {
        if (state.gardenWateringSequence <= previous) return@LaunchedEffect
        previous = state.gardenWateringSequence
        water.snapTo(0f); water.animateTo(1f, tween(1500))
    }
    LaunchedEffect(selectedPlot, garden.plotCount) { scroll.animateScrollTo((selectedPlot * plotPixels).toInt().coerceIn(0, scroll.maxValue)) }
    val onTap by rememberUpdatedState(onPlot)
    val description = stringResource(R.string.world_scene_description, garden.plotCount)
    BoxWithConstraints(Modifier.fillMaxWidth().height(360.dp)) {
        val viewportPixels = constraints.maxWidth
        Box(Modifier.fillMaxSize().horizontalScroll(scroll)) {
        // Logical plot zero is on the right, matching horizontalScroll's RTL origin.
        Canvas(Modifier.width(260.dp * garden.plotCount).fillMaxHeight().semantics { contentDescription = description }
            .pointerInput(garden.plotCount) { detectTapGestures { point ->
                onTap(gardenPlotAt(point.x, size.width.toFloat(), garden.plotCount))
            } }) {
            val visible = visibleGardenPlots(scroll.value, viewportPixels, plotPixels, garden.plotCount)
            withTransform({ scale(size.width / (garden.plotCount * 500f), size.height / 640f, Offset.Zero) }) {
                drawWorldLandscape(garden.plotCount, phase.value, state.isNightTheme)
                for (plot in visible) {
                    val origin = (garden.plotCount - 1 - plot) * 500f
                    withTransform({ translate(origin - 250f, 0f) }) {
                        drawWorldPlot(plot == selectedPlot, phase.value)
                        val tree = planted[plot]
                        if (tree?.visible == true) {
                            val frame = GardenFrame(growth.getValue(tree.id).value, phase.value,
                                if (water.isRunning && state.gardenWateredTreeId == tree.id) water.value else 0f, state.isNightTheme, species = tree.species)
                            drawGardenTree(frame)
                            if (state.isNightTheme) drawRect(Color(0xFF132644).copy(0.15f), Offset(250f, 0f), Size(500f, 640f))
                            drawGardenEffects(frame)
                        } else if (tree == null) {
                            drawCircle(Color(0xFFFFF2CD).copy(0.8f), 22f, Offset(500f, 550f))
                            drawLine(Color(0xFF7B6748), Offset(489f, 550f), Offset(511f, 550f), 4f, StrokeCap.Round)
                            drawLine(Color(0xFF7B6748), Offset(500f, 539f), Offset(500f, 561f), 4f, StrokeCap.Round)
                        }
                        garden.placements.filter { it.plotIndex == plot && it.visible }.forEach { placed ->
                            GardenDecoration.entries.firstOrNull { it.id == placed.itemId }?.let { item ->
                                drawPlacedWorldItem(item, placed.slot, phase.value, state.isNightTheme)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

private fun DrawScope.drawWorldPlot(selected: Boolean, phase: Float) {
    if (selected) drawOval(Color(0xFFFFE8A7).copy(0.35f + 0.1f * sin(phase * 12f)), Offset(295f, 526f), Size(410f, 89f))
    drawOval(Color(0xFF87674F), Offset(325f, 540f), Size(350f, 62f))
    drawOval(Color(0xFFB68F65), Offset(340f, 540f), Size(320f, 40f))
    if (selected) drawOval(Color(0xFFFFDF8A), Offset(320f, 535f), Size(360f, 70f), style = Stroke(4f))
}

private fun DrawScope.drawPlacedWorldItem(item: GardenDecoration, slot: Int, phase: Float, night: Boolean) {
    val x = 340f + slot * 155f
    val t = phase * PI.toFloat() * 2
    val center = when (item) {
        GardenDecoration.BUTTERFLIES -> Offset(x + sin(t + slot) * 40f, 350f + cos(t * 2 + slot) * 70f)
        GardenDecoration.BIRDS -> Offset(x + sin(t) * 65f, 240f + cos(t * 2 + slot) * 35f)
        GardenDecoration.RAINBOW -> Offset(x, 210f + slot * 24f)
        GardenDecoration.SPARKLES -> Offset(x, 420f - ((phase * 2 + slot * 0.2f) % 1f) * 220f)
        GardenDecoration.LANTERN -> Offset(x, 450f)
        else -> Offset(x, 560f)
    }
    if (item == GardenDecoration.LANTERN) {
        drawLine(Color(0xFF83664E), Offset(x - 20f, 560f), Offset(x - 20f, 395f), 5f, StrokeCap.Round)
        drawLine(Color(0xFF83664E), Offset(x - 20f, 395f), Offset(x, 395f), 5f, StrokeCap.Round)
        drawLine(Color(0xFF83664E), Offset(x, 395f), Offset(x, 405f), 2f)
    }
    drawShopDecoration(item, center, if (item == GardenDecoration.RAINBOW) 2f else 1.2f, phase + slot * 0.1f, night)
}

private fun DrawScope.drawWorldLandscape(plots: Int, phase: Float, night: Boolean) {
    val width = plots * 500f
    drawRect(Brush.verticalGradient(if (night) listOf(Color(0xFF111A34), Color(0xFF3C3C63)) else listOf(Color(0xFFC6EAF6), Color(0xFFFFF1D2)), endY = 640f), size = Size(width, 640f))
    val light = Offset(width - 130f, 105f)
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE6AB).copy(if (night) 0.22f else 0.5f), Color.Transparent), light, 130f), 130f, light)
    if (night) {
        val moon = Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(light - Offset(43f, 43f), light + Offset(43f, 43f))) },
            Path().apply { addOval(Rect(light + Offset(-12f, -54f), light + Offset(57f, 13f))) })
        drawPath(moon, Color(0xFFFFE9B5))
        repeat(plots * 8) { index -> drawCircle(Color(0xFFFFF2CE).copy(0.4f + 0.5f * ((sin(phase * PI.toFloat() * 4f + index) + 1) / 2)),
            2f + index % 2, Offset(30f + index * 137 % width, 25f + index * 71 % 310)) }
    } else {
        drawCircle(Color(0xFFFFD275), 45f, light)
        repeat(12) { ray ->
            val angle = ray * PI.toFloat() / 6 + phase * PI.toFloat() * 2
            drawLine(Color(0xFFFFDE8B).copy(0.6f), light + Offset(cos(angle), sin(angle)) * 62f,
                light + Offset(cos(angle), sin(angle)) * 82f, 4f, StrokeCap.Round)
        }
    }
    // A repeating two-cloud pattern keeps drift speed constant as the world expands.
    for (cloud in -3..(ceil(width / 430f).toInt() + 2)) {
        val x = cloud * 430f + phase * 860f
        val alternate = ((cloud % 2) + 2) % 2
        drawOval(Color.White.copy(if (night) 0.12f else 0.65f), Offset(x, 145f + alternate * 35), Size(170f, 35f))
        drawCircle(Color.White.copy(if (night) 0.12f else 0.65f), 31f, Offset(x + 70f, 140f + alternate * 35))
    }
    val hills = Path().apply { moveTo(0f, 465f); repeat(plots) { i -> cubicTo(i * 500f + 130f, 350f, i * 500f + 360f, 470f, (i + 1) * 500f, 445f) }; lineTo(width, 640f); lineTo(0f, 640f); close() }
    drawPath(hills, if (night) Color(0xFF3E5C60) else Color(0xFF98C998))
    drawRect(if (night) Color(0xFF2D4B4C) else Color(0xFF6FA56E), Offset(0f, 515f), Size(width, 125f))
    repeat(plots * 12) { index ->
        val x = 20f + index * 41f
        val sway = sin(phase * PI.toFloat() * 6f + index) * 5f
        drawLine((if (night) Color(0xFF6A997B) else Color(0xFFB7D991)).copy(0.8f), Offset(x, 608f), Offset(x + sway, 585f - index % 3 * 5f), 3f, StrokeCap.Round)
        if (night && index % 2 == 0) {
            val t = (phase * 2 + index * 0.07f) % 1f
            drawCircle(Color(0xFFF8EAB5).copy(sin(t * PI.toFloat()).coerceIn(0f, 1f)), 3f, Offset(x + sin(phase * PI.toFloat() * 4f + index) * 14f, 480f - t * 170f))
        }
    }
}
