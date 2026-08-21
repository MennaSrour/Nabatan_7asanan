package com.menna.nabata_7asena.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class NabataColors(
    val primaryPurple: Color,
    val primaryGold: Color,
    val primaryTeal: Color,
    val primaryPink: Color,
    val backgroundLight: Color,
    val backgroundSky: Color,
    val backgroundMoon: Color,
    val backgroundPaperWarm: Color,
    val backgroundWarmAccent: Color,
    val dialogWarningBg: Color,
    val successGreen: Color,
    val pendingBlue: Color,
    val specialPurple: Color,
    val errorRed: Color,
    val warningOrange: Color,
    val starYellow: Color,
    val starGold: Color,
    val textDark: Color,
    val textAlmostBlack: Color,
    val neutralSlate: Color,
    val neutralDarkBlueText: Color,
    val nightBlueDeep: Color,
    val nightBlueMid: Color,
    val nightBlueSoft: Color,
    val nightBlueLight: Color,
    val accentGoldBright: Color,
    val accentGoldDeep: Color,
    val accentGoldWarm: Color,
    val headerPurple: Color,
    val headerPurpleDarker: Color,
    val blueDarker: Color,
    val mintLight: Color,
    val prayerFajr: Color,
    val prayerFajrDark: Color,
    val prayerZuhr: Color,
    val prayerZuhrDark: Color,
    val prayerAsr: Color,
    val prayerAsrDark: Color,
    val prayerMaghrib: Color,
    val prayerMaghribDark: Color,
    val prayerIsha: Color,
    val prayerIshaDark: Color,

    val quranGreen: Color,
    val quranGreenDark: Color,
    val challengeBlue: Color,
    val challengeBlueDark: Color,
    val azkarOrange: Color,
    val azkarOrangeDark: Color,
    val extraPurple: Color,
    val extraPurpleDark: Color,
    val podiumBlue: Color,
    val podiumBlueDarker: Color,
    val podiumLightBlue: Color,
    val podiumBlueDarkVariant: Color,
    val nightSkyGradient: Brush,
    val sunsetGradient: Brush,
    val moonGlowGradient: Brush,
    val lanternGradient: Brush,
    val headerGradient: Brush,
    val quranBackgroundGradient: Brush,
    val doneGradient: Brush,
    val prayerFajrGradient: Brush,
    val prayerZuhrGradient: Brush,
    val prayerAsrGradient: Brush,
    val prayerMaghribGradient: Brush,
    val prayerIshaGradient: Brush,
    val quranGradient: Brush,
    val challengeGradient: Brush,
    val azkarGradient: Brush,
    val extraGradient: Brush
)

@Immutable
data class NabataTypography(
    val material: Typography
)

val SummerPalette = NabataColors(
    primaryPurple = Color(0xFF0288D1),
    primaryGold = Color(0xFFFFD54F),
    primaryTeal = Color(0xFF26C6DA),
    primaryPink = Color(0xFFFF8A80),

    backgroundLight = Color(0xFFF9FBE7),
    backgroundSky = Color(0xFFE1F5FE),
    backgroundMoon = Color(0xFFFFFDE7),
    backgroundPaperWarm = Color(0xFFFFFDF9),
    backgroundWarmAccent = Color(0xFFFFE082),
    dialogWarningBg = Color(0xFFFFF3E0),

    successGreen = Color(0xFF4CAF50),
    pendingBlue = Color(0xFF29B6F6),
    specialPurple = Color(0xFFAB47BC),
    errorRed = Color(0xFFE57373),
    warningOrange = Color(0xFFFFB74D),
    starYellow = Color(0xFFFFF176),
    starGold = Color(0xFFFFB300),

    textDark = Color(0xFF37474F),
    textAlmostBlack = Color(0xFF2D1B18),
    neutralSlate = Color(0xFF455A64),
    neutralDarkBlueText = Color(0xFF01579B),

    nightBlueDeep = Color(0xFF0288D1),
    nightBlueMid = Color(0xFF03A9F4),
    nightBlueSoft = Color(0xFF29B6F6),
    nightBlueLight = Color(0xFF81D4FA),

    accentGoldBright = Color(0xFFFFD54F),
    accentGoldDeep = Color(0xFFFFB300),
    accentGoldWarm = Color(0xFFFF8F00),

    headerPurple = Color(0xFF0288D1),
    headerPurpleDarker = Color(0xFF01579B),
    blueDarker = Color(0xFF0288D1),

    mintLight = Color(0xFFE8F5E9),
    prayerFajr = Color(0xFF0288D1),
    prayerFajrDark = Color(0xFF01579B),
    prayerZuhr = Color(0xFF9CBD77),
    prayerZuhrDark = Color(0xFF559809),
    prayerAsr = Color(0xFFC0A44F),
    prayerAsrDark = Color(0xFFEA910E),
    prayerMaghrib = Color(0xFFEF5350),
    prayerMaghribDark = Color(0xFFC62828),
    prayerIsha = Color(0xFFAB47BC),
    prayerIshaDark = Color(0xFF7B1FA2),

    quranGreen = Color(0xFF26A69A),
    quranGreenDark = Color(0xFF00897B),
    challengeBlue = Color(0xFF42A5F5),
    challengeBlueDark = Color(0xFF1E88E5),
    azkarOrange = Color(0xFFFF7043),
    azkarOrangeDark = Color(0xFFF4511E),
    extraPurple = Color(0xFF7E57C2),
    extraPurpleDark = Color(0xFF5E35B1),

    podiumBlue = Color(0xFF81C784),
    podiumBlueDarker = Color(0xFF4CAF50),
    podiumLightBlue = Color(0xFFA5D6A7),
    podiumBlueDarkVariant = Color(0xFF388E3C),

    nightSkyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0288D1),
            Color(0xFF03A9F4),
            Color(0xFF29B6F6),
            Color(0xFF81D4FA)
        )
    ),
    sunsetGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFCA28),
            Color(0xFFFFB300),
            Color(0xFFFF8F00)
        )
    ),
    moonGlowGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFDE7),
            Color(0xFFFFF9C4),
            Color(0xFFFFF59D).copy(alpha = 0.3f)
        )
    ),
    lanternGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD54F),
            Color(0xFFFFB300),
            Color(0xFFFFA726)
        )
    ),
    headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0288D1),
            Color(0xFF03A9F4),
            Color(0xFF29B6F6)
        )
    ),
    quranBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFDF9),
            Color(0xFFFFFDE7),
            Color(0xFFFFF9C4).copy(alpha = 0.3f)
        )
    ),
    doneGradient = Brush.linearGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))),
    prayerFajrGradient = Brush.linearGradient(listOf(Color(0xFF0288D1), Color(0xFF03A9F4))),
    prayerZuhrGradient = Brush.linearGradient(listOf(Color(0xFF9CBD77), Color(0xFF559809))),
    prayerAsrGradient = Brush.linearGradient(listOf(Color(0xFFC0A44F), Color(0xFFEA910E))),
    prayerMaghribGradient = Brush.linearGradient(listOf(Color(0xFFEF5350), Color(0xFFC62828))),
    prayerIshaGradient = Brush.linearGradient(listOf(Color(0xFFAB47BC), Color(0xFF7B1FA2))),

    quranGradient = Brush.linearGradient(listOf(Color(0xFF26A69A), Color(0xFF00897B))),
    challengeGradient = Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))),
    azkarGradient = Brush.linearGradient(listOf(Color(0xFFFF7043), Color(0xFFF4511E))),
    extraGradient = Brush.linearGradient(listOf(Color(0xFF7E57C2), Color(0xFF5E35B1)))
)

val LocalNabataColors: CompositionLocal<NabataColors> = staticCompositionLocalOf { SummerPalette }
val LocalNabataTypography: CompositionLocal<NabataTypography> = staticCompositionLocalOf { NabataTypography(Typography()) }

object NabataTheme {
    @Composable
    fun colors(): NabataColors = LocalNabataColors.current

    @Composable
    fun typography(): NabataTypography = LocalNabataTypography.current
}