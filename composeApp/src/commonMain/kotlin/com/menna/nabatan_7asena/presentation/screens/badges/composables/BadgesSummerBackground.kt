package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.menna.nabatan_7asena.presentation.common.components.ParticleBackground
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun BadgesSummerBackground(particleAlphas: List<Float>) {
    val positions = remember { listOf(Offset(.08f,.05f) to 3f, Offset(.85f,.08f) to 4f, Offset(.45f,.03f) to 3f, Offset(.60f,.12f) to 5f, Offset(.20f,.10f) to 3f, Offset(.75f,.20f) to 4f) }
    ParticleBackground(positions, SummerTheme.colors.goldWarm.copy(alpha = .6f), particleAlphas = particleAlphas)
}
