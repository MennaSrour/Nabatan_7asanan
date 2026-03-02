package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "blob1"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE1F5FE))) {
        drawCircle(
            color = Color(0xFFFFF9C4),
            radius = size.width * 0.6f,
            center = Offset(size.width, offset1),
            alpha = 0.5f
        )
        drawCircle(
            color = Color(0xFFE1BEE7),
            radius = size.width * 0.4f,
            center = Offset(0f, size.height * 0.4f - offset1),
            alpha = 0.4f
        )
    }
}
