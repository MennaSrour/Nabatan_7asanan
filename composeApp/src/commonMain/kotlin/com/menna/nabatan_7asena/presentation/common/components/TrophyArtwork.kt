package com.menna.nabatan_7asena.presentation.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun TrophyArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = size.minDimension / 100f
        val origin = center - Offset(50 * unit, 50 * unit)
        fun point(x: Int, y: Int) = origin + Offset(x * unit, y * unit)
        val gold = Color(0xFFFFBC35)
        val amber = Color(0xFFC77425)
        for (x in listOf(15, 59)) {
            drawOval(amber, point(x, 23), Size(26 * unit, 27 * unit), style = Stroke(7 * unit))
            drawOval(gold, point(x, 21), Size(26 * unit, 26 * unit), style = Stroke(5 * unit))
        }
        drawRoundRect(amber, point(44, 53), Size(12 * unit, 27 * unit), CornerRadius(5 * unit))
        drawRoundRect(amber, point(31, 77), Size(38 * unit, 10 * unit), CornerRadius(4 * unit))
        drawLine(gold, point(35, 79), point(65, 79), 3 * unit)
        val cup = Path().apply {
            moveTo(point(24, 17).x, point(24, 17).y)
            lineTo(point(76, 17).x, point(76, 17).y)
            cubicTo(point(74, 50).x, point(74, 50).y, point(66, 64).x, point(66, 64).y, point(50, 65).x, point(50, 65).y)
            cubicTo(point(33, 64).x, point(33, 64).y, point(25, 48).x, point(25, 48).y, point(24, 17).x, point(24, 17).y)
            close()
        }
        drawPath(cup, Brush.linearGradient(listOf(Color(0xFFFFF1A1), gold, amber), point(24, 17), point(76, 64)))
        drawLine(Color(0xFFFFF3BD), point(30, 20), point(70, 20), 3 * unit)
        for ((x, y) in listOf(50 to 39, 11 to 11, 86 to 62, 18 to 61)) {
            val radius = if (x == 50) 9 else 5
            val sparkle = Path().apply {
                moveTo(point(x, y - radius).x, point(x, y - radius).y)
                lineTo(point(x + radius, y).x, point(x + radius, y).y)
                lineTo(point(x, y + radius).x, point(x, y + radius).y)
                lineTo(point(x - radius, y).x, point(x - radius, y).y)
                close()
            }
            drawPath(sparkle, if (x == 50) Color.White else gold)
        }
    }
}
