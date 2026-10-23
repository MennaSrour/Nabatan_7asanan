package com.menna.nabatan_7asena.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.random.Random



private object DayCore {
    val gold = Color(0xFFFFC83D)
    val goldDeep = Color(0xFFFF9F1C)
    val coral = Color(0xFFFF7A59)
    val leaf = Color(0xFF4FBE8D)
    val leafDeep = Color(0xFF2E9968)
    val violet = Color(0xFF7A5FD1)
    val cream = Color(0xFFFFF8EC)
    val creamDeep = Color(0xFFFDEFDB)
    val brownText = Color(0xFF6D4C41)
    val brownTextDark = Color(0xFF3E2723)
    val slateText = Color(0xFF37474F)
}

private object NightCore {
    val gold = Color(0xFFFFD54F)
    val goldDeep = Color(0xFFFFB300)
    val accent = Color(0xFF9B7EDE)
    val leaf = Color(0xFF34D399)
    val bg = Color(0xFF1A1B3A)
    val surface = Color(0xFF242853)

    val surfaceElevated = Color(0xFF2E3164)
    val text = Color(0xFFF5F0E8)
    val textMuted = Color(0xFFB8B4D9)
}

data class SummerColors(
    val backgroundLight: Color = DayCore.cream,
    val goldWarm: Color = DayCore.gold,
    val goldDeep: Color = DayCore.goldDeep,
    val textPrimary: Color = DayCore.slateText,
    val textBrown: Color = DayCore.brownText,
    val textDarkBrown: Color = DayCore.brownTextDark,
    val chipUnselected: Color = Color(0xFFF3E9D6),
    val cardBackground: Color = Color.White,
    val transparent: Color = Color.Transparent,
    val white: Color = Color.White,
    val overlayDark: Color = Color(0xFF071A52),
    val dialogBackground: Color = DayCore.cream,
    val errorRed: Color = Color(0xFFEF5350),
    val successGreen: Color = DayCore.leaf,

    val wisdomStart: Color = Color(0xFFF3EEFB),
    val wisdomEnd: Color = Color(0xFFEFE6FA),
    val wisdomAccent: Color = DayCore.violet,
    val wisdomTextPrimary: Color = Color(0xFF373e5a),
    val wisdomTextSecondary: Color = Color(0xFF5e6074),

    val surfaceElevated: Color = Color.White,
     val quizOptionIdleText: Color = DayCore.slateText,
    val quizOptionAnsweredText: Color = Color.White,

    val statGreenStart: Color = DayCore.leaf,
    val statGreenEnd: Color = DayCore.leafDeep,
    val statPurpleStart: Color = Color(0xFFA78EE8),
    val statPurpleEnd: Color = DayCore.violet,

    val headerTextDark: Color = Color(0xFF04000E),
    val settingsIconDark: Color = Color(0xFF41464D),
    val taskCompletedBase: Color = Color(0xFFEDEBF4),

    val fajrStart: Color = Color(0xFF2B2F6B),
    val fajrMid: Color = Color(0xFF4B4F92),
    val fajrEnd: Color = Color(0xFF8C7BAE),
    val fajrAccent: Color = Color(0xFF4B3F86),

    val chestLockedStart: Color = Color(0xFFDCD8EC),
    val chestLockedEnd: Color = Color(0xFFC7C1E0),
    val chestLockedText: Color = Color(0xFF5B5470),
    val dayBackground: Color = DayCore.creamDeep,
    val sectionTitleDark: Color = Color(0xFF071A52),
    val addBtnBorder: Color = DayCore.leaf.copy(alpha = 0.4f),
    val addBtnShadow: Color = DayCore.leaf.copy(alpha = 0.15f),
    val addBtnText: Color = Color(0xFF071A52),

     val secondaryAccent: Color = DayCore.coral,
    val highlightAccent: Color = DayCore.coral,

    val primaryGold: Color = DayCore.gold,
    val primaryPink: Color = DayCore.coral,
    val primaryTeal: Color = DayCore.coral,
    val primarySummerBlue: Color = DayCore.coral,
    val leafGreen: Color = DayCore.leaf,
    val flowerCoral: Color = DayCore.coral,
    val backgroundSunny: Color = DayCore.cream,
    val taskCompletedGreen: Color = DayCore.leaf
)

val LocalSummerColors = staticCompositionLocalOf { SummerColors() }

val LocalIsNight = staticCompositionLocalOf { false }


val NightGalaxyColors = SummerColors(
    backgroundLight = NightCore.bg,
    goldWarm = NightCore.gold,
    goldDeep = NightCore.goldDeep,
    textPrimary = NightCore.text,
    textBrown = NightCore.textMuted,
    textDarkBrown = NightCore.text,
    chipUnselected = NightCore.surface,
    cardBackground = NightCore.surface,
    transparent = Color.Transparent,
    white = NightCore.text,
    overlayDark = Color(0xFF16132E),
    dialogBackground = NightCore.surface,
    errorRed = Color(0xFFFF6B6B),
    successGreen = NightCore.leaf,

    wisdomStart = Color(0xFF352D5E),
    wisdomEnd = Color(0xFF3D3468),
    wisdomAccent = NightCore.accent,
    wisdomTextPrimary = NightCore.text,
    wisdomTextSecondary = NightCore.textMuted,

    surfaceElevated = NightCore.surfaceElevated,
    quizOptionIdleText = NightCore.text,

    statGreenStart = NightCore.leaf,
    statGreenEnd = Color(0xFF10B981),
    statPurpleStart = Color(0xFFB794F6),
    statPurpleEnd = NightCore.accent,

    headerTextDark = NightCore.text,
    settingsIconDark = NightCore.textMuted,
    taskCompletedBase = NightCore.surface,

    fajrStart = NightCore.bg,
    fajrMid = Color(0xFF2D2A6E),
    fajrEnd = NightCore.accent,
    fajrAccent = NightCore.accent,

    chestLockedStart = NightCore.surface,
    chestLockedEnd = Color(0xFF3D3573),
    chestLockedText = NightCore.textMuted,
    dayBackground = Color(0xFF221E3D),
    sectionTitleDark = NightCore.text,
    addBtnBorder = NightCore.leaf.copy(alpha = 0.4f),
    addBtnShadow = NightCore.leaf.copy(alpha = 0.15f),
    addBtnText = NightCore.text,

    secondaryAccent = NightCore.accent,
    highlightAccent = NightCore.accent,

    primaryGold = NightCore.gold,
    primaryPink = NightCore.accent,
    primaryTeal = NightCore.accent,
    primarySummerBlue = NightCore.accent,
    leafGreen = NightCore.leaf,
    flowerCoral = NightCore.accent,
    backgroundSunny = NightCore.surface,
    taskCompletedGreen = NightCore.leaf
)


@Immutable
data class QuranColors(
    val backgroundTop: Color,
    val backgroundMid: Color,
    val backgroundEnd: Color,
    val topBar: Color,
    val drawer: Color,
    val card: Color,
    val cardActive: Color,
    val headerStart: Color,
    val headerEnd: Color,
    val border: Color,
    val accent: Color,
    val accentStrong: Color,
    val accentSoft: Color,
    val textPrimary: Color,
    val textMuted: Color
)

val QuranDayColors = QuranColors(
    backgroundTop = Color(0xFFFFFFFF),
    backgroundMid = Color(0xFFFBF6EE),
    backgroundEnd = Color(0xFFEDE7FB).copy(alpha = 0.5f),
    topBar = Color(0xFFFBF6EE),
    drawer = Color(0xFFFBF6EE),
    card = Color.White.copy(alpha = 0.95f),
    cardActive = Color(0xFFEDE7FB),
    headerStart = Color(0xFFEDE7FB),
    headerEnd = Color(0xFFFBF6EE),
    border = Color(0xFFE4DEF2),
    accent = Color(0xFF9B87F5),
    accentStrong = Color(0xFF6C56D9),
    accentSoft = Color(0xFFEDE7FB),
    textPrimary = Color(0xFF3C3550),
    textMuted = Color(0xFF8F88A6)
)

val QuranNightColors = QuranColors(
    backgroundTop = Color(0xFF2D2849),
    backgroundMid = Color(0xFF292545),
    backgroundEnd = Color(0xFF322C55),
    topBar = Color(0xFF2D2849),
    drawer = Color(0xFF322D52),
    card = Color(0xFF3A3360),
    cardActive = Color(0xFF463D70),
    headerStart = Color(0xFF463D70),
    headerEnd = Color(0xFF373060),
    border = Color(0xFF564D80),
    accent = Color(0xFFB9A2F2),
    accentStrong = Color(0xFFD3C4F8),
    accentSoft = Color(0xFF463D70),
    textPrimary = Color(0xFFE9E3F2),
    textMuted = Color(0xFFB5ABCE)
)

val LocalQuranColors = staticCompositionLocalOf { QuranDayColors }



object SummerTheme {
    val colors: SummerColors
        @Composable get() = LocalSummerColors.current

    val shapes: SummerShapes
        @Composable get() = LocalSummerShapes.current

    val dimensions: SummerDimensions
        @Composable get() = LocalSummerDimensions.current

    val typography: Typography
        @Composable get() = LocalSummerTypography.current

    val quran: QuranColors
        @Composable get() = LocalQuranColors.current


    object Colors {
        val PrimaryGold: Color @Composable get() = LocalSummerColors.current.primaryGold
        val PrimaryPink: Color @Composable get() = LocalSummerColors.current.primaryPink
        val PrimaryTeal: Color @Composable get() = LocalSummerColors.current.primaryTeal
        val PrimarySummerBlue: Color @Composable get() = LocalSummerColors.current.primarySummerBlue
        val LeafGreen: Color @Composable get() = LocalSummerColors.current.leafGreen
        val FlowerCoral: Color @Composable get() = LocalSummerColors.current.flowerCoral
        val BackgroundSunny: Color @Composable get() = LocalSummerColors.current.backgroundSunny
        val TaskCompletedGreen: Color @Composable get() = LocalSummerColors.current.taskCompletedGreen
    }

    object Shapes {
        val ExtraRounded: Shape = RoundedCornerShape(24.dp)
        val MediumRounded: Shape = RoundedCornerShape(16.dp)
        val SmallRounded: Shape = RoundedCornerShape(10.dp)
    }

    @Composable
    fun rememberParticleAlphas(count: Int): List<Float> {
        val infinite = rememberInfiniteTransition(label = "particleTwinkle")
        return List(count) { i ->
            val duration = remember { 900 + Random.nextInt(1400) }
            val delay = remember { Random.nextInt(1200) }
            val alpha by infinite.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particle_$i"
            )
            alpha
        }
    }

    @Composable
    fun rememberFloatingAnimation(range: Float = 8f, durationMillis: Int = 1600): Float {
        val infinite = rememberInfiniteTransition(label = "floatAnim")
        val offset by infinite.animateFloat(
            initialValue = -range,
            targetValue = range,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatOffset"
        )
        return offset
    }

    @Composable
    fun rememberSlowRotation(durationMillis: Int = 12000): Float {
        val infinite = rememberInfiniteTransition(label = "slowRotate")
        val angle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotateAngle"
        )
        return angle
    }
}

@Composable
fun NabataAppTheme(
    isNight: Boolean,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalSummerColors provides if (isNight) NightGalaxyColors else SummerColors(),
        LocalIsNight provides isNight,
        LocalQuranColors provides if (isNight) QuranNightColors else QuranDayColors,
        LocalSummerTypography provides Typography(),
        LocalSummerShapes provides SummerShapes(),
        LocalSummerDimensions provides SummerDimensions(),
        content = content
    )
}

fun isNightFallback(): Boolean {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return hour >= 18 || hour < 5
}