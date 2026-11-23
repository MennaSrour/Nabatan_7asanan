package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import com.menna.nabatan_7asena.domain.entity.GardenDecoration
import com.menna.nabatan_7asena.domain.entity.GardenPlacement
import com.menna.nabatan_7asena.domain.entity.GardenTree
import kotlin.math.PI
import kotlin.math.cos

import kotlin.math.sin

@Composable
internal fun rememberGardenWateringProgress(profileId: String, sequence: Long): Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
    val water = remember(profileId) { Animatable(1f) }
    var previous by remember(profileId) { mutableLongStateOf(sequence) }
    LaunchedEffect(profileId, sequence) {
        if (sequence > previous) {
            previous = sequence
            water.snapTo(0f)
            water.animateTo(1f, tween(1500))
        }
    }
    return water
}

@Composable
internal fun GardenPlotArtwork(
    tree: GardenTree?,
    placements: List<GardenPlacement>,
    selected: Boolean,
    night: Boolean,
    phase: State<Float>,
    water: () -> Float,
    modifier: Modifier
) {
    val growth by animateFloatAsState(tree?.stage?.toFloat() ?: 0f, tween(1500), label = "treeGrowth")
    Canvas(modifier) {
        drawGardenLandscape(phase.value, night)
        val scale = minOf(size.width / 525f, size.height / 570f)
        clipRect {
            withTransform({
                translate(size.width / 2 - 500f * scale, size.height - 630f * scale)
                scale(scale, scale, Offset.Zero)
            }) {
                drawWorldPlot(selected, phase.value)
                if (tree?.visible == true) {
                    val frame = GardenFrame(growth, phase.value, water(), night, species = tree.species)
                    drawGardenTree(frame)
                    drawGardenEffects(frame)
                }
                placements.forEach { placed ->
                    GardenDecoration.entries.firstOrNull { it.id == placed.itemId }?.let {
                        drawPlacedWorldItem(it, placed.slot, phase.value, night)
                    }
                }
            }
        }
    }
}

internal fun DrawScope.drawGardenLandscape(phase: Float, night: Boolean) {
    val w = size.width
    val h = size.height
    val unit = size.minDimension
    if (unit <= 0f) return
    val sky = if (night) listOf(Color(0xFF1E3156), Color(0xFF63638D))
        else listOf(Color(0xFFACE7FC), Color(0xFFFFF5D3))
    drawRect(Brush.verticalGradient(sky))
    val light = Offset(w * 0.82f, h * 0.24f)
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE6A2).copy(alpha = 0.60f), Color.Transparent), light, unit * 0.28f),
        unit * 0.28f, light)
    drawCircle(Color(0xFFFFD776), unit * 0.09f, light)
    if (night) {
        drawCircle(sky.first(), unit * 0.075f, light + Offset(unit * 0.035f, -unit * 0.035f))
        repeat(12) { index ->
            val alpha = 0.35f + 0.25f * sin(phase * 2f * PI.toFloat() + index)
            drawCircle(Color(0xFFFFF3CF).copy(alpha = alpha), unit * 0.009f,
                Offset(w * ((index * 17 % 89) + 4) / 100f, h * ((index * 11 % 45) + 3) / 100f))
        }
    }
    for (index in 0..2) {
        val x = ((index * 0.45f + phase * 0.18f) % 1.15f - 0.14f) * w
        val y = h * (0.20f + index % 2 * 0.15f)
        val cloud = Color.White.copy(alpha = if (night) 0.15f else 0.75f)
        drawOval(cloud, Offset(x, y), Size(unit * 0.35f, unit * 0.075f))
        drawCircle(cloud, unit * 0.08f, Offset(x + unit * 0.15f, y + unit * 0.015f))
    }
    val hills = Path().apply {
        moveTo(0f, h * 0.74f)
        cubicTo(w * 0.20f, h * 0.55f, w * 0.38f, h * 0.58f, w * 0.55f, h * 0.76f)
        cubicTo(w * 0.70f, h * 0.64f, w * 0.89f, h * 0.57f, w, h * 0.72f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(hills, Brush.verticalGradient(
        if (night) listOf(Color(0xFF668B81), Color(0xFF354E55)) else listOf(Color(0xFFB9DE80), Color(0xFF75B96E)),
        startY = h * 0.55f, endY = h
    ))
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
