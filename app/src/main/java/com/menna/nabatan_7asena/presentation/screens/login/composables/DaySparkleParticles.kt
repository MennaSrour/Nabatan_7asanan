package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun DaySparkleParticles(modifier: Modifier = Modifier) {
    val sparkleAlphas = SummerTheme.rememberParticleAlphas(count = 5)
    val sparkleColor = SummerTheme.colors.goldWarm
    val positions = remember {
        listOf(
            Offset(0.10f, 0.08f), Offset(0.88f, 0.12f), Offset(0.50f, 0.05f),
            Offset(0.20f, 0.20f), Offset(0.80f, 0.25f)
        )
    }

    Canvas(modifier = modifier) {
        positions.forEachIndexed { i, off ->
            drawCircle(
                color = sparkleColor,
                radius = 3.dp.toPx(),
                center = Offset(size.width * off.x, size.height * off.y),
                alpha = sparkleAlphas.getOrElse(i) { 0.5f }
            )
        }
    }
}
