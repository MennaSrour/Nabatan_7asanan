package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun OnboardingBackground(isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SummerTheme.colors.wisdomStart,
                        SummerTheme.colors.dayBackground
                    )
                )
            )
        )

        OnboardingAtmosphere(
            isDark = isDark,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SummerTheme.colors.dayBackground.copy(alpha = 0f),
                        SummerTheme.colors.dayBackground
                    )
                )
            )
        )
    }
}
