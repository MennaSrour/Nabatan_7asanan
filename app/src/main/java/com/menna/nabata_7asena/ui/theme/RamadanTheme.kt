package com.menna.nabata_7asena.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

object RamadanTheme {

    object Colors {
        val PrimaryPurple = Color(0xFF3A1875)
        val PrimaryGold = Color(0xFFB093E5)
        val PrimaryTeal = Color(0xFF26C6DA)
        val PrimaryPink = Color(0xFFEC407A)

        val BackgroundLight = Color(0xFFFFF9E6)
        val BackgroundSky = Color(0xFFE1F5FE)
        val BackgroundMoon = Color(0xFFFFFDE7)

        val StarYellow = Color(0xFFFFF176)
        val StarGold = Color(0xFFFFB300)
        val LanternRed = Color(0xFFE57373)
        val LanternOrange = Color(0xFFFFB74D)

        val TaskCompletedGreen = Color(0xFF66BB6A)
        val TaskPendingBlue = Color(0xFF42A5F5)
        val TaskSpecialPurple = Color(0xFFAB47BC)

        val NightSkyGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A237E),
                Color(0xFF283593),
                Color(0xFF3949AB)
            )
        )

        val SunsetGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF6F00),
                Color(0xFFFF8F00),
                Color(0xFFFFCA28)
            )
        )

        val MoonGlowGradient = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFDE7),
                Color(0xFFFFF9C4),
                Color(0xFFFFF59D).copy(alpha = 0.3f)
            )
        )

        val LanternGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD54F),
                Color(0xFFFFB300),
                Color(0xFFFFA726)
            )
        )

        val HeaderGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF7E57C2),
                Color(0xFF9575CD),
                Color(0xFFB39DDB)
            )
        )
    }

    object Shapes {
        val ExtraRounded = RoundedCornerShape(32.dp)
        val SuperRounded = RoundedCornerShape(28.dp)
        val MediumRounded = RoundedCornerShape(24.dp)
        val SmallRounded = RoundedCornerShape(20.dp)
        val TinyRounded = RoundedCornerShape(16.dp)

        val LanternShape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )

        val MoonShape = RoundedCornerShape(50)
    }
    object Emojis {
        const val CRESCENT = "🌙"
        const val STAR = "⭐"
        const val LANTERN = "🏮"
        const val MOSQUE = "🕌"
        const val PRAYER = "🤲"
        const val QURAN = "📖"
        const val DATES = "🌴"
        const val SPARKLES = "✨"
        const val GIFT = "🎁"
        const val CELEBRATION = "🎉"
    }

    @Composable
    fun rememberStarTwinkleAnimation(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "star")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        return alpha
    }

    @Composable
    fun rememberLanternSwingAnimation(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "lantern")
        val rotation by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        )
        return rotation
    }

    @Composable
    fun rememberMoonGlowAnimation(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "moon")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        return scale
    }

    @Composable
    fun rememberFloatingAnimation(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "floating")
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -15f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "offsetY"
        )
        return offsetY
    }
    @Composable
    fun rememberPulseAnimation(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
        return scale
    }

    @Composable
    fun rememberStarAlphas(count: Int): List<Float> {
        val infiniteTransition = rememberInfiniteTransition(label = "stars")
        return List(count) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f + (index % 3) * 0.1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000 + (index % 5) * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha$index"
            )
            alpha
        }
    }
}