package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.menna.nabatan_7asena.presentation.common.components.ParticleBackground
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerSettingsBackground() {
    val positions = remember { listOf(Offset(.10f,.15f) to 3f, Offset(.90f,.20f) to 3f, Offset(.30f,.50f) to 3f, Offset(.80f,.60f) to 3f) }
    ParticleBackground(positions, SummerTheme.Colors.PrimaryGold.copy(alpha = .4f), defaultAlpha = 1f, showClouds = false)
}
