package com.menna.nabatan_7asena.ui.theme

import com.menna.nabatan_7asena.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.resources.AppResources as R

/** Bundled Baloo Bhaijaan 2 weights shared with the Friday Challenge. */
val KidsFontFamily
    @androidx.compose.runtime.Composable get() = FontFamily(
    Font(R.font.baloo_bhaijaan2_regular, FontWeight.Normal),
    Font(R.font.baloo_bhaijaan2_medium, FontWeight.Medium),
    Font(R.font.baloo_bhaijaan2_semibold, FontWeight.SemiBold),
    Font(R.font.baloo_bhaijaan2_bold, FontWeight.Bold),
    Font(R.font.baloo_bhaijaan2_extrabold, FontWeight.ExtraBold)
)