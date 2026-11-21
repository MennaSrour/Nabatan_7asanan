package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

internal fun DrawScope.drawGardenLandscape(frame: GardenFrame) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFCEEFFF), Color(0xFFE7E2FC), Color(0xFFFFF1CB)), endY = 640f), size = Size(1000f, 640f))
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFDF89).copy(0.6f), Color.Transparent), Offset(820f, 110f), 145f), 145f, Offset(820f, 110f))
    drawCircle(Color(0xFFFFD365), 58f, Offset(820f, 110f))
    drawCircle(Color(0xFFFFE79C), 42f, Offset(808f, 98f))
    repeat(4) { index ->
        val x = (index * 330f + frame.phase * 1330f) % 1330f - 150f
        val y = 100f + index % 2 * 85f
        drawGardenCloud(x, y, if (index % 2 == 0) 1f else 0.65f)
    }
    val hills = Path().apply { moveTo(0f, 455f); cubicTo(210f, 290f, 420f, 485f, 620f, 400f); cubicTo(820f, 300f, 960f, 400f, 1000f, 410f); lineTo(1000f, 640f); lineTo(0f, 640f); close() }
    drawPath(hills, Color(0xFFA5D4A8))
    val foreground = Path().apply { moveTo(0f, 510f); cubicTo(250f, 415f, 440f, 540f, 680f, 470f); cubicTo(860f, 420f, 950f, 470f, 1000f, 480f); lineTo(1000f, 640f); lineTo(0f, 640f); close() }
    drawPath(foreground, Brush.verticalGradient(listOf(Color(0xFF86BF86), Color(0xFF538C61)), startY = 430f, endY = 640f))
    drawOval(Color(0xFF376E4B).copy(0.25f), Offset(310f, 554f), Size(380f, 59f))
    drawOval(Color(0xFF987051), Offset(325f, 538f), Size(350f, 62f))
    drawOval(Color(0xFFBD9167), Offset(340f, 538f), Size(320f, 38f))
    repeat(13) { index -> drawCircle(Color(0xFF79563F).copy(0.5f), 3f, Offset(360f + index * 23f, 557f + sin(index * 3f) * 12f)) }
}

private fun DrawScope.drawGardenCloud(x: Float, y: Float, scale: Float) {
    val color = Color.White.copy(0.72f)
    drawOval(color, Offset(x, y), Size(170f * scale, 40f * scale))
    drawCircle(color, 36f * scale, Offset(x + 60f * scale, y))
    drawCircle(color, 27f * scale, Offset(x + 105f * scale, y - 5f * scale))
}
