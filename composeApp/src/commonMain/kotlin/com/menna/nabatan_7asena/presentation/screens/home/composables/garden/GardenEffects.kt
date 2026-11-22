package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*

internal fun DrawScope.drawGardenEffects(frame: GardenFrame) {
    if (frame.stage > 3.1f) drawGardenSparkles(frame)
    if (frame.stage > 4.1f) drawGardenMeadow(frame)
    if (frame.water > 0f) drawWatering(frame.water)
}

private fun DrawScope.drawGardenSparkles(frame: GardenFrame) {
    val alpha = ((frame.stage - 3.1f) / 0.9f).coerceIn(0f, 1f)
    repeat(14) { index ->
        val t = (frame.phase * 3f + index / 14f) % 1f
        val center = Offset(270f + (index * 83 % 460), 470f - t * 310f)
        drawSparkle(center, 4f + index % 3 * 2f, Color(0xFFFFEAA9).copy(alpha * sin(t * PI.toFloat()).coerceAtLeast(0f)))
    }
}

private fun DrawScope.drawGardenMeadow(frame: GardenFrame) {
    val alpha = ((frame.stage - 4.1f) / 0.9f).coerceIn(0f, 1f)
    repeat(18) { index ->
        val x = 100f + index * 46f
        val y = 594f + sin(index * 3f) * 14f
        drawGardenLeaf(Offset(x, y), Offset(x - 9f, y - 22f), 5f, Color(0xFFB0DC88).copy(alpha))
        drawGardenLeaf(Offset(x, y), Offset(x + 13f, y - 18f), 5f, Color(0xFF83C475).copy(alpha))
        if (index % 2 == 0) drawGardenFlower(Offset(x, y - 24f), 7f, Color(0xFFEFC0D4).copy(alpha))
    }

}

private fun DrawScope.drawWatering(progress: Float) {
    repeat(17) { index ->
        val t = ((progress * 2.5f - index * 0.035f)).coerceIn(0f, 1f)
        if (t > 0f && t < 1f) {
            val x = 480f + (index % 5 - 2) * 24f + sin(index.toFloat()) * 18f
            val y = 190f + t * 365f
            val drop = Path().apply { moveTo(x, y - 16f); cubicTo(x - 15f, y, x - 13f, y + 11f, x, y + 12f); cubicTo(x + 13f, y + 11f, x + 15f, y, x, y - 16f); close() }
            drawPath(drop, Color(0xFF75CDE8).copy(0.85f))
            drawCircle(Color.White.copy(0.6f), 3f, Offset(x - 3f, y + 3f))
        }
    }
    val ripple = ((progress - 0.35f) / 0.65f).coerceIn(0f, 1f)
    drawOval(Color(0xFFB2E5EE).copy((1f - ripple) * 0.8f), Offset(500f - ripple * 135f, 560f - ripple * 22f), Size(ripple * 270f, ripple * 44f), style = Stroke(4f))
    if (progress > 0.5f) repeat(10) { index ->
        val t = (progress - 0.5f) * 2f
        val angle = index * 2f * PI.toFloat() / 10f
        drawSparkle(Offset(500f + cos(angle) * t * 170f, 520f + sin(angle) * t * 70f - t * 120f), 8f, Color(0xFFFFDF85).copy(1f - t))
    }
}

private fun DrawScope.drawSparkle(center: Offset, radius: Float, color: Color) {
    val path = Path().apply { moveTo(center.x, center.y - radius); lineTo(center.x + radius * 0.3f, center.y - radius * 0.3f); lineTo(center.x + radius, center.y); lineTo(center.x + radius * 0.3f, center.y + radius * 0.3f); lineTo(center.x, center.y + radius); lineTo(center.x - radius * 0.3f, center.y + radius * 0.3f); lineTo(center.x - radius, center.y); lineTo(center.x - radius * 0.3f, center.y - radius * 0.3f); close() }
    drawPath(path, color)
}
