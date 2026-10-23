package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun OnboardingPageIndicators(totalPages: Int, currentPage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            PageIndicator(
                isSelected = index == currentPage,
                modifier = Modifier.padding(horizontal = 3.dp)
            )
        }
    }
}
