package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*
import com.menna.nabatan_7asena.domain.entity.GardenSpecies

internal fun DrawScope.drawGardenTree(frame: GardenFrame) {
    val stage = frame.stage
    val seedAlpha = (1f - stage * 1.7f).coerceIn(0f, 1f)
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFDF87).copy(seedAlpha * 0.7f), Color.Transparent), Offset(500f, 547f), 55f), 55f, Offset(500f, 547f))
    drawOval(Color(0xFFFFD775).copy(seedAlpha), Offset(486f, 527f), Size(28f, 38f))
    if (stage <= 0.02f) return
    rotate(sin(frame.phase * PI.toFloat() * 6) * (1.4f + stage * 0.2f), Offset(500f, 558f)) {
        val height = (100f + (stage - 1f).coerceAtLeast(0f) * 68f) * stage.coerceAtMost(1f)
        val top = 558f - height
        drawTrunk(stage, top)
        if (stage < 3f) drawGrowingLeaves(stage, top, frame.phase)
        if (stage > 1.2f && frame.species != GardenSpecies.PALM) drawTreeBranches(stage, top)
        if (stage > 2.1f) {
            if (frame.species == GardenSpecies.PALM) drawPalm(stage, top, frame.phase)
            else drawCanopy(stage, top, frame.phase, frame.species)
        }
        if (stage > 3.1f && frame.species != GardenSpecies.PALM && frame.species != GardenSpecies.OLIVE) drawTreeBlossoms(stage, top)
        if (stage > 4.1f && frame.species == GardenSpecies.BLOSSOM) drawTreeBlossoms(stage, top - 20f)
        else if (stage > 4.1f && frame.species != GardenSpecies.PALM) drawTreeFruit(stage, top, frame.phase, frame.species)
    }
}

private fun DrawScope.drawTrunk(stage: Float, top: Float) {
    val trunk = Path().apply { moveTo(500f, 566f); cubicTo(475f, 490f, 525f, top + 85f, 500f, top) }
    val width = 7f + (stage - 1f).coerceAtLeast(0f) * 7f
    val color = if (stage < 1.6f) Color(0xFF38855C) else Color(0xFF79533B)
    drawPath(trunk, color, style = Stroke(width, cap = StrokeCap.Round))
    drawPath(trunk, Color(0xFFDBB481).copy(0.65f), style = Stroke(width * 0.3f, cap = StrokeCap.Round))
    if (stage > 2f) {
        drawLine(Color(0xFF79533B), Offset(500f, 551f), Offset(472f, 566f), width / 2, StrokeCap.Round)
        drawLine(Color(0xFF79533B), Offset(500f, 551f), Offset(530f, 567f), width / 2, StrokeCap.Round)
    }
}

private fun DrawScope.drawTreeBranches(stage: Float, top: Float) {
    val amount = ((stage - 1.2f) / 0.8f).coerceIn(0f, 1f)
    repeat(6) { index ->
        val side = if (index % 2 == 0) -1 else 1
        val y = top + 60f + index / 2 * 37f
        val reach = (65f + stage * 8f) * amount
        val branch = Path().apply { moveTo(501f, y + 30f); quadraticTo(500f + side * reach * 0.5f, y + 18f, 500f + side * reach, y - 12f) }
        drawPath(branch, Color(0xFF79533B), style = Stroke(4f + stage, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawGrowingLeaves(stage: Float, top: Float, phase: Float) {
    val count = if (stage < 1.5f) 2 else 8
    repeat(count) { index ->
        val side = if (index % 2 == 0) -1f else 1f
        val y = top + 20f + index / 2 * 38f
        val reach = if (index < 2) 48f else 60f
        drawGardenLeaf(Offset(500f, y + 16f), Offset(500f + side * reach, y - 14f + sin(phase * 20f + index) * 4f), 18f,
            (if (index % 3 == 0) Color(0xFF75BC72) else Color(0xFF3E9866)).copy(alpha = (3f - stage).coerceIn(0f, 1f)))
    }
}

internal fun DrawScope.drawGardenLeaf(start: Offset, end: Offset, width: Float, color: Color) {
    val middle = (start + end) / 2f
    val leaf = Path().apply { moveTo(start.x, start.y); quadraticTo(middle.x, middle.y - width, end.x, end.y); quadraticTo(middle.x, middle.y + width, start.x, start.y); close() }
    drawPath(leaf, color)
    drawLine(Color.White.copy(0.25f), start, end, 1.5f)
}

private fun DrawScope.drawCanopy(stage: Float, top: Float, phase: Float, species: GardenSpecies) {
    val alpha = ((stage - 2.1f) / 0.9f).coerceIn(0f, 1f)
    val radius = 74f + stage * 7f
    val dark = when (species) { GardenSpecies.OLIVE -> Color(0xFF547868); GardenSpecies.BLOSSOM -> Color(0xFFAB719E); else -> Color(0xFF2F7252) }
    val light = when (species) { GardenSpecies.OLIVE -> Color(0xFFA5BA9B); GardenSpecies.BLOSSOM -> Color(0xFFE8B1C9); else -> Color(0xFF82BE73) }
    repeat(11) { index ->
        val angle = index * 2.4f
        val x = 500f + cos(angle) * (if (index < 7) 100f else 55f)
        val y = top + 35f + sin(angle) * 58f
        val shift = sin(phase * 18f + index) * 3f
        drawCircle(dark.copy(alpha), radius, Offset(x, y))
        drawCircle(light.copy(alpha), radius * 0.72f, Offset(x - 17f + shift, y - 23f))
    }
}

private fun DrawScope.drawTreeBlossoms(stage: Float, top: Float) {
    val alpha = ((stage - 3.1f) / 0.9f).coerceIn(0f, 1f)
    repeat(12) { index ->
        val center = Offset(500f + cos(index * 2.4f) * (65f + index % 3 * 28f), top + 30f + sin(index * 2.4f) * 76f)
        drawGardenFlower(center, 10f, (if (index % 2 == 0) Color(0xFFF3B1CC) else Color(0xFFFFEAB6)).copy(alpha))
    }
}

internal fun DrawScope.drawGardenFlower(center: Offset, radius: Float, color: Color) {
    repeat(5) { petal ->
        val angle = petal * PI.toFloat() * 2f / 5f
        drawCircle(color, radius * 0.6f, center + Offset(cos(angle), sin(angle)) * radius * 0.7f)
    }
    drawCircle(Color(0xFFFFCE63).copy(color.alpha), radius * 0.38f, center)
}

private fun DrawScope.drawTreeFruit(stage: Float, top: Float, phase: Float, species: GardenSpecies) {
    val alpha = ((stage - 4.1f) / 0.9f).coerceIn(0f, 1f)
    repeat(8) { index ->
        val center = Offset(500f + cos(index * 2.7f) * 135f, top + 55f + sin(index * 2.7f) * 63f)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFDE79).copy(alpha * (0.25f + 0.15f * sin(phase * 18f + index))), Color.Transparent), center, 30f), 30f, center)
        if (species == GardenSpecies.OLIVE) drawOval(Color(0xFF45484F).copy(alpha), center - Offset(6f, 10f), Size(12f, 20f))
        else drawCircle(Color(0xFFE98E79).copy(alpha), 14f, center)
        drawCircle(Color.White.copy(alpha * 0.6f), 4f, center + Offset(-4f, -4f))
        drawGardenLeaf(center + Offset(0f, -14f), center + Offset(16f, -22f), 5f, Color(0xFF326F4E).copy(alpha))
    }
}

private fun DrawScope.drawPalm(stage: Float, top: Float, phase: Float) {
    val alpha = ((stage - 2.1f) / 0.9f).coerceIn(0f, 1f)
    repeat(8) { index ->
        val side = if (index % 2 == 0) -1 else 1
        val reach = 115f + index / 2 * 13f
        val end = Offset(500f + side * reach, top + 20f + index / 2 * 27f + sin(phase * 18 + index) * 4)
        val branch = Path().apply { moveTo(500f, top + 25f); quadraticTo(500f + side * reach * 0.6f, top - 80f + index * 3f, end.x, end.y) }
        drawPath(branch, Color(0xFF4E9363).copy(alpha), style = Stroke(12f, cap = StrokeCap.Round))
        repeat(6) { leaf ->
            val t = (leaf + 1) / 7f
            val x = 500f + (end.x - 500f) * t
            val y = (1 - t) * (1 - t) * (top + 25) + 2 * (1 - t) * t * (top - 80f + index * 3) + t * t * end.y
            drawLine(Color(0xFF77AF73).copy(alpha), Offset(x, y), Offset(x + side * 18f, y + 25f), 4f, StrokeCap.Round)
        }
    }
    if (stage > 4f) repeat(10) { index ->
        drawOval(Color(0xFFE3A55F).copy((stage - 4).coerceIn(0f, 1f)), Offset(476f + index % 5 * 10f, top + 47f + index / 5 * 13f), Size(8f, 13f))
    }
}
