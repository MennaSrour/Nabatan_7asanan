package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.resources.AppResources as R

enum class AyahAudioState { IDLE, LOADING, PLAYING, PAUSED }

val FloatingBarClearance = 92.dp

val DownloadLabelFont
    @androidx.compose.runtime.Composable get() = FontFamily(Font(R.font.cairo_semibold))
