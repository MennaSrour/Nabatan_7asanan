package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.menna.nabatan_7asena.presentation.common.components.ParticleBackground
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SebhaSummerBackground(particleAlphas: List<Float>) {
    val positions = remember { listOf(Offset(.10f,.06f) to 3f, Offset(.80f,.04f) to 4f, Offset(.45f,.02f) to 3f, Offset(.65f,.10f) to 5f, Offset(.20f,.09f) to 3f, Offset(.90f,.13f) to 4f) }
    ParticleBackground(positions, SummerTheme.Colors.PrimaryGold.copy(alpha = .75f), particleAlphas = particleAlphas)
}
