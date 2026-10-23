package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun LeaderboardBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
            Brush.verticalGradient(
                listOf(
                    SummerTheme.colors.dialogBackground,
                    SummerTheme.colors.backgroundLight
                )
            )
        )
    )
}
