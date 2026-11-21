package com.menna.nabatan_7asena.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography as MaterialTypography

internal fun playfulMaterialTypography(fontFamily: FontFamily): MaterialTypography {
    val defaults = MaterialTypography()
    return MaterialTypography(
        displayLarge = defaults.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = defaults.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = defaults.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = defaults.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = defaults.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = defaults.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = defaults.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = defaults.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = defaults.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = defaults.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = defaults.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = defaults.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = defaults.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = defaults.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = defaults.labelSmall.copy(fontFamily = fontFamily),
    )
}
