package com.menna.nabatan_7asena.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * ثيم "المجرة" — تصميم فضائي طفولي للأطفال.
 * نفس شكل SummerTheme في الاستخدام عشان نقدر نستبدله بسهولة.
 */
object GalaxyTheme {

    object Colors {
        // خلفية الفضاء
        val DeepSpace = Color(0xFF0A0E2A)
        val SpaceIndigo = Color(0xFF1B1F4D)
        val SpaceNavy = Color(0xFF11142E)

        // ألوان الكواكب والنجوم
        val NebulaPurple = Color(0xFF7B2FF7)
        val NebulaPink = Color(0xFFFF4FA3)
        val CometCyan = Color(0xFF31E1F7)
        val StarGold = Color(0xFFFFD54F)
        val MarsOrange = Color(0xFFFF7043)
        val MoonSilver = Color(0xFFE8EAF6)

        // نصوص
        val TextLight = Color(0xFFF5F5FF)
        val TextMuted = Color(0xFFB8BEE0)
    }

    object Shapes {
        val MediumRounded = RoundedCornerShape(18.dp)
        val SmallRounded = RoundedCornerShape(10.dp)
    }

    /** حركة "طفو" بسيطة لأي عنصر (صاروخ، رائد فضاء، إلخ) */
    @Composable
    fun rememberFloatingAnimation(range: Float = 8f, durationMillis: Int = 1600): Float {
        val infinite = rememberInfiniteTransition(label = "galaxyFloat")
        val offset by infinite.animateFloat(
            initialValue = -range,
            targetValue = range,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "galaxyFloatOffset"
        )
        return offset
    }

    /** مجموعة قيم شفافية متغيرة عشوائيًا لعمل تأثير "تلألؤ نجوم" */
    @Composable
    fun rememberTwinkleAlphas(count: Int): List<Float> {
        val infinite = rememberInfiniteTransition(label = "galaxyTwinkle")
        return List(count) { i ->
            val duration = remember { 900 + Random.nextInt(1400) }
            val delay = remember { Random.nextInt(1200) }
            val alpha by infinite.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "twinkle_$i"
            )
            alpha
        }
    }

    /** حركة دوران بطيئة (تُستخدم لحلقات الكواكب) */
    @Composable
    fun rememberSlowRotation(durationMillis: Int = 12000): Float {
        val infinite = rememberInfiniteTransition(label = "galaxyRotate")
        val angle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "galaxyAngle"
        )
        return angle
    }
}