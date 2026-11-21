package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.ui.graphics.Color

data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val color: Color
)

val MedalBronze = Color(0xFFCD7F32)

val MedalSilver = Color(0xFF90A4AE)
