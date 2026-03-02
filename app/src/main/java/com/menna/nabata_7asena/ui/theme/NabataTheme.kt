package com.menna.nabata_7asena.ui.theme

import android.R.attr.category
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocal

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

val RamadanPalette = NabataColors(
    primaryPurple = Color(0xFF3A1875),
    primaryGold = Color(0xFFB093E5),
    primaryTeal = Color(0xFF26C6DA),
    primaryPink = Color(0xFFEC407A),

    backgroundLight = Color(0xFFFFF9E6),
    backgroundSky = Color(0xFFE1F5FE),
    backgroundMoon = Color(0xFFFFFDE7),
    backgroundPaperWarm = Color(0xFFFFFDF7),
    backgroundWarmAccent = Color(0xFFFFECB3),
    dialogWarningBg = Color(0xFFFFF5F5),

    successGreen = Color(0xFF66BB6A),
    pendingBlue = Color(0xFF42A5F5),
    specialPurple = Color(0xFFAB47BC),
    errorRed = Color(0xFFE57373),
    warningOrange = Color(0xFFFFB74D),
    starYellow = Color(0xFFFFF176),
    starGold = Color(0xFFFFB300),

    textDark = Color(0xFF37474F),
    textAlmostBlack = Color(0xFF1A1A1A),
    neutralSlate = Color(0xFF455A64),
    neutralDarkBlueText = Color(0xFF2E3E5C),

    nightBlueDeep = Color(0xFF1A237E),
    nightBlueMid = Color(0xFF283593),
    nightBlueSoft = Color(0xFF3949AB),
    nightBlueLight = Color(0xFF5C6BC0),

    accentGoldBright = Color(0xFFFFD54F),
    accentGoldDeep = Color(0xFFFFB300),
    accentGoldWarm = Color(0xFFFFA726),

    headerPurple = Color(0xFF7E57C2),
    headerPurpleDarker = Color(0xFF512DA8),
    blueDarker = Color(0xFF1976D2),

    mintLight = Color(0xFFE8F5E9),
    prayerFajr = Color(0xFF0E6EBB),
    prayerFajrDark = Color(0xFF156081),
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

    podiumBlue = Color(0xFF283593),
    podiumBlueDarker = Color(0xFF3949AB),
    podiumLightBlue = Color(0xFF5C6BC0),
    podiumBlueDarkVariant = Color(0xFF283593),

    nightSkyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A237E),
            Color(0xFF283593),
            Color(0xFF3949AB),
            Color(0xFF5C6BC0)
        )
    ),
    sunsetGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF6F00),
            Color(0xFFFF8F00),
            Color(0xFFFFCA28)
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
            Color(0xFF7E57C2),
            Color(0xFF9575CD),
            Color(0xFFB39DDB)
        )
    ),
    quranBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFDF7),
            Color(0xFFFFF9E6),
            Color(0xFFFFECB3).copy(alpha = 0.3f)
        )
    ),
    doneGradient = Brush.linearGradient(listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8))),
    prayerFajrGradient = Brush.linearGradient(listOf(Color(0xFF0E6EBB), Color(0xFF156081))),
    prayerZuhrGradient = Brush.linearGradient(listOf(Color(0xFF9CBD77), Color(0xFF559809))),
    prayerAsrGradient = Brush.linearGradient(listOf(Color(0xFFC0A44F), Color(0xFFEA910E))),
    prayerMaghribGradient = Brush.linearGradient(listOf(Color(0xFFEF5350), Color(0xFFC62828))),
    prayerIshaGradient = Brush.linearGradient(listOf(Color(0xFFAB47BC), Color(0xFF7B1FA2))),

    quranGradient = Brush.linearGradient(listOf(Color(0xFF26A69A), Color(0xFF00897B))),
    challengeGradient = Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))),
    azkarGradient = Brush.linearGradient(listOf(Color(0xFFFF7043), Color(0xFFF4511E))),
    extraGradient = Brush.linearGradient(listOf(Color(0xFF7E57C2), Color(0xFF5E35B1)))
)

val LocalNabataColors: CompositionLocal<NabataColors> = staticCompositionLocalOf { RamadanPalette }
val LocalNabataTypography: CompositionLocal<NabataTypography> = staticCompositionLocalOf { NabataTypography(Typography()) }

object NabataTheme {
    @Composable
    fun colors(): NabataColors = LocalNabataColors.current

    @Composable
    fun typography(): NabataTypography = LocalNabataTypography.current
}