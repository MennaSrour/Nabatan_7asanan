package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import com.menna.nabatan_7asena.ui.theme.LocalIsNight
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun NabatanLoginBackground() {
    val isNight = LocalIsNight.current
    val gradientStart = SummerTheme.colors.wisdomStart
    val gradientEnd = SummerTheme.colors.dayBackground

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientStart, gradientEnd)
                )
            )
        }

        if (isNight) {
            NightStarsAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f)
            )
        } else {
            DaySparkleParticles(modifier = Modifier.fillMaxSize())
        }
    }
}
