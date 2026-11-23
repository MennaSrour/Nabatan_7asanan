package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

internal val MushafGold = Color(0xFFB99A5B)

@Composable
internal fun MushafSurahBanner(title: String, style: TextStyle, horizontalInset: Dp, verticalInset: Dp, modifier: Modifier = Modifier) {
    val palette = SummerTheme.quran
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val radius = CornerRadius(12.dp.toPx())
            drawRoundRect(palette.accentSoft, cornerRadius = radius)
            drawRoundRect(MushafGold.copy(alpha = 0.75f), cornerRadius = radius, style = Stroke(1.dp.toPx()))
            val inset = 3.dp.toPx()
            drawRoundRect(palette.accent.copy(alpha = 0.5f), topLeft = Offset(inset, inset),
                size = Size((size.width - inset * 2).coerceAtLeast(0f), (size.height - inset * 2).coerceAtLeast(0f)),
                cornerRadius = radius, style = Stroke(0.5.dp.toPx()))
            listOf(horizontalInset.toPx() / 2, size.width - horizontalInset.toPx() / 2).forEach { x ->
                val y = center.y
                val d = minOf(4.dp.toPx(), size.height / 5, horizontalInset.toPx() / 3)
                drawPath(Path().apply { moveTo(x, y - d); lineTo(x + d, y); lineTo(x, y + d); lineTo(x - d, y); close() }, MushafGold)
            }
        }
        BasicText(title, style = style.copy(color = palette.accentStrong), modifier = Modifier.padding(horizontal = horizontalInset, vertical = verticalInset))
    }
}
