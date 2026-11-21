package com.menna.nabatan_7asena.presentation.screens.start.composables

import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.resources.AppResources as R

val ColorTrunk = Color(0xFF8C6A4E)

val NabatanFontFamily
    @androidx.compose.runtime.Composable get() = FontFamily(
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_medium, FontWeight.Medium),
    Font(R.font.cairo_semibold, FontWeight.SemiBold),
    Font(R.font.cairo_bold, FontWeight.Bold)
)
