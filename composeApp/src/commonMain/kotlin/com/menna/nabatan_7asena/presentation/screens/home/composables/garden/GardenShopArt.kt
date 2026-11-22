package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import com.menna.nabatan_7asena.domain.entity.GardenDecoration
import kotlin.math.*

internal fun DrawScope.drawShopDecoration(item: GardenDecoration, center: Offset, scale: Float, phase: Float, night: Boolean) {
    withTransform({ translate(center.x, center.y); scale(scale, scale, Offset.Zero) }) {
        when (item) {
            GardenDecoration.BUTTERFLIES -> drawShopButterfly(phase)
            GardenDecoration.BIRDS -> drawShopBird(phase)
            GardenDecoration.FLOWERS -> repeat(3) { index ->
                val x = (index - 1) * 20f
                val y = sin(index * 2f) * 10f
                drawGardenLeaf(Offset(x, 28f), Offset(x + 10f, y + 3f), 4f, Color(0xFF4B9867))
                drawLine(Color(0xFF4B9867), Offset(x, 28f), Offset(x, y), 2f)
                drawGardenFlower(Offset(x, y), 9f, listOf(Color(0xFFF6B6D7), Color(0xFFFFD477), Color(0xFFC6B6EE))[index])
            }
            GardenDecoration.LANTERN -> drawShopLantern(night, phase)
            GardenDecoration.RAINBOW -> repeat(6) { index ->
                val inset = index * 7f
                drawArc(listOf(Color(0xFFF0B7CB), Color(0xFFF5CBA3), Color(0xFFF1E3A5), Color(0xFFB9DCC4),
                    Color(0xFFBDD8EE), Color(0xFFD3BDE9))[index].copy(if (night) 0.55f else 0.85f),
                    180f, 180f, false, Offset(-52f + inset, -22f + inset), Size(104f - inset * 2, 92f - inset * 2), style = Stroke(6f))
            }
            GardenDecoration.BENCH -> {
                repeat(3) { row -> drawRoundRect(Color(0xFFB38A60), Offset(-34f, -15f + row * 9f), Size(68f, 6f), CornerRadius(2f)) }
                drawLine(Color(0xFF70563E), Offset(-25f, 6f), Offset(-25f, 28f), 5f, StrokeCap.Round)
                drawLine(Color(0xFF70563E), Offset(25f, 6f), Offset(25f, 28f), 5f, StrokeCap.Round)
            }
            GardenDecoration.FOUNTAIN -> {
                drawOval(Color(0xFF92B9C4), Offset(-37f, 8f), Size(74f, 25f))
                drawOval(Color(0xFFBFE8EC), Offset(-29f, 10f), Size(58f, 14f))
                drawRoundRect(Color(0xFF88ACB9), Offset(-6f, -9f), Size(12f, 29f), CornerRadius(4f))
                repeat(5) { jet ->
                    val x = (jet - 2) * 13f
                    drawPath(Path().apply { moveTo(0f, 2f); quadraticTo(x * 0.8f, -48f, x, 10f) }, Color(0xFF9CDBE8), style = Stroke(2.5f))
                    val t = (phase * 5f + jet * 0.2f) % 1f
                    drawCircle(Color(0xFFDCF4F6), 2f, Offset(x * t, -30f * kotlin.math.sin(t * kotlin.math.PI.toFloat()) + 8f))
                }
            }
            GardenDecoration.SPARKLES -> repeat(5) { index ->
                drawShopStar(Offset(cos(index * 2.4f) * 28f, sin(index * 2.4f) * 22f),
                    7f + index % 2 * 3f, listOf(Color(0xFFFFDC85), Color(0xFFD6B9F4), Color(0xFFB7E2EE))[index % 3]
                        .copy(0.45f + 0.5f * ((sin(phase * PI.toFloat() * 4f + index) + 1f) / 2f)))
            }
        }
    }
}

private fun DrawScope.drawShopButterfly(phase: Float) {
    val wing = 7f + abs(sin(phase * PI.toFloat() * 16f)) * 14f
    drawOval(Color(0xFFAA83D8), Offset(-wing, -18f), Size(wing, 25f))
    drawOval(Color(0xFFD9B5EE), Offset(0f, -18f), Size(wing, 25f))
    drawOval(Color(0xFFF3C482), Offset(-wing * 0.8f, 2f), Size(wing * 0.8f, 16f))
    drawOval(Color(0xFFF0ACCF), Offset(0f, 2f), Size(wing * 0.8f, 16f))
    drawLine(Color(0xFF69516C), Offset(0f, -15f), Offset(0f, 15f), 3f, StrokeCap.Round)
    drawLine(Color(0xFF69516C), Offset(0f, -12f), Offset(-6f, -23f), 1.5f)
    drawLine(Color(0xFF69516C), Offset(0f, -12f), Offset(6f, -23f), 1.5f)
}

private fun DrawScope.drawShopBird(phase: Float) {
    drawOval(Color(0xFF80B4CF), Offset(-22f, -10f), Size(42f, 27f))
    drawCircle(Color(0xFFA9D6E4), 12f, Offset(15f, -14f))
    rotate(sin(phase * PI.toFloat() * 10f) * 15f, Offset(-3f, 2f)) { drawOval(Color(0xFF557DAA), Offset(-18f, -4f), Size(24f, 14f)) }
    drawCircle(Color(0xFF253F5B), 2f, Offset(19f, -16f))
    drawPath(Path().apply { moveTo(26f, -13f); lineTo(36f, -9f); lineTo(25f, -6f); close() }, Color(0xFFEABD69))
    drawPath(Path().apply { moveTo(-17f, 0f); lineTo(-35f, -11f); lineTo(-27f, 9f); close() }, Color(0xFF557DAA))
    drawLine(Color(0xFF7D6251), Offset(3f, 15f), Offset(1f, 23f), 2f)
}

private fun DrawScope.drawShopLantern(night: Boolean, phase: Float) {
    val glow = (if (night) 0.5f else 0.25f) + sin(phase * PI.toFloat() * 4f) * 0.06f
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD17D).copy(glow), Color.Transparent), Offset.Zero, 58f), 58f, Offset.Zero)
    drawPath(Path().apply { moveTo(-13f, -22f); lineTo(0f, -34f); lineTo(13f, -22f); close() }, Color(0xFFA77C42))
    drawRoundRect(Color(0xFFF8D18A), Offset(-13f, -21f), Size(26f, 39f), CornerRadius(5f))
    drawRoundRect(Color(0xFFAE834B), Offset(-13f, -21f), Size(26f, 39f), CornerRadius(5f), style = Stroke(3f))
    drawPath(Path().apply { moveTo(0f, -15f); lineTo(8f, -2f); lineTo(0f, 11f); lineTo(-8f, -2f); close() }, Color(0xFFFFF2C9))
    drawLine(Color(0xFFAE834B), Offset(-16f, 19f), Offset(16f, 19f), 5f, StrokeCap.Round)
    drawCircle(Color(0xFFAE834B), 4f, Offset(0f, -38f), style = Stroke(2f))
}

internal fun DrawScope.drawShopStar(center: Offset, radius: Float, color: Color) {
    val star = Path().apply {
        repeat(8) { index ->
            val angle = index * PI.toFloat() / 4f
            val r = if (index % 2 == 0) radius else radius * 0.3f
            val point = center + Offset(cos(angle), sin(angle)) * r
            if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
        }
        close()
    }
    drawPath(star, color)
}
