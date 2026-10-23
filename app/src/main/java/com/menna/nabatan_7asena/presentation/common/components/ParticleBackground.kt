package com.menna.nabatan_7asena.presentation.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun ParticleBackground(
    positions: List<Pair<Offset, Float>>,
    particleColor: Color,
    particleAlphas: List<Float> = emptyList(),
    defaultAlpha: Float = 0.7f,
    showClouds: Boolean = true
) {
    val colors = SummerTheme.colors
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Brush.verticalGradient(listOf(colors.wisdomStart, colors.dialogBackground, colors.dayBackground)))
            positions.forEachIndexed { index, (position, radius) ->
                drawCircle(particleColor, radius.dp.toPx(),
                    Offset(size.width * position.x, size.height * position.y),
                    alpha = particleAlphas.getOrElse(index) { defaultAlpha })
            }
        }
        if (showClouds) BackgroundClouds()
    }
}

@Composable
private fun BackgroundClouds() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))
    LottieAnimation(composition, iterations = LottieConstants.IterateForever,
        modifier = Modifier.fillMaxWidth().height(140.dp).alpha(0.5f))
}
