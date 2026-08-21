package com.menna.nabata_7asena.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.sin

object SummerTheme {
    val colors: SummerColors
        @Composable
        get() = LocalSummerColors.current

    val typography: Typography
        @Composable
        get() = LocalSummerTypography.current

    val shapes: SummerShapes
        @Composable
        get() = LocalSummerShapes.current

    val dimensions: SummerDimensions
        @Composable
        get() = LocalSummerDimensions.current


    object Colors {
        val PrimaryGold = Color(0xFFFFC83D)
        val PrimaryPink = Color(0xFFEC5FA3)
        val PrimaryTeal = Color(0xFF26A69A)
        val PrimarySummerBlue = Color(0xFF29B6F6)
        val LeafGreen = Color(0xFF34C08C)
        val FlowerCoral = Color(0xFFFF7A6B)
        val BackgroundSunny = Color(0xFFFFF8E7)
        val TaskCompletedGreen = Color(0xFF22C55E)
    }

    object Shapes {
        val ExtraRounded: Shape = RoundedCornerShape(24.dp)
        val MediumRounded: Shape = RoundedCornerShape(16.dp)
        val SmallRounded: Shape = RoundedCornerShape(10.dp)
    }

    @Composable
    fun rememberFloatingAnimation(): Float {
        val infinite = rememberInfiniteTransition(label = "floating")
        val offset by infinite.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatOffset"
        )
        return offset
    }

    @Composable
    fun rememberParticleAlphas(count: Int): List<Float> {
        val infinite = rememberInfiniteTransition(label = "particles")
        val phase by infinite.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing)
            ),
            label = "particlePhase"
        )
        return remember(phase) {
            List(count) { i ->
                val stepOffset = i * (2f * Math.PI.toFloat() / count)
                (0.35f + 0.35f * sin(phase + stepOffset)).coerceIn(0.15f, 0.85f)
            }
        }
    }
}

@Composable
fun SummerTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSummerColors provides SummerColors(),
        LocalSummerTypography provides Typography(),
        LocalSummerShapes provides SummerShapes(),
        LocalSummerDimensions provides SummerDimensions(),
        content = content
    )
}