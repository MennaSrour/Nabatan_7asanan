package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

/** Fit tactile, readable cards without making tablet tiles disproportionately wide. */
internal fun gardenGridColumns(widthDp: Float, gapDp: Float): Int =
    ((widthDp - gapDp) / (128f + gapDp)).toInt().coerceIn(1, 8)
