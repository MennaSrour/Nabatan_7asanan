package com.menna.nabata_7asena.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SummerShapes(
    val bottomSheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val chip: Shape = RoundedCornerShape(50),
    val taskCard: Shape = RoundedCornerShape(14.dp)
)

data class SummerDimensions(
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 10.dp,
    val paddingLarge: Dp = 14.dp,
    val paddingScreenHorizontal: Dp = 20.dp,
    val paddingScreenBottom: Dp = 48.dp,
    val iconSmall: Dp = 18.dp,
    val iconLarge: Dp = 26.dp,
    val bottomSheetMaxHeight: Dp = 380.dp
)

val LocalSummerShapes = staticCompositionLocalOf { SummerShapes() }
val LocalSummerDimensions = staticCompositionLocalOf { SummerDimensions() }