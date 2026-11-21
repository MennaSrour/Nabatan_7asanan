package com.menna.nabatan_7asena.presentation.screens.start.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.screens.start.StartUiState

@Composable
fun StartContent(state: StartUiState) {
    AnimatedVisibility(visible = state.isVisible, exit = fadeOut(tween(500))) {
        NabatanStartContent()
    }
}
