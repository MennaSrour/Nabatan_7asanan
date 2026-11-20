package com.menna.nabatan_7asena.presentation.screens.sebha

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.sebha.composables.SebhaContent

@Composable
fun SebhaScreen(viewModel: SebhaViewModel = platformSebhaViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) { onDispose { viewModel.onScreenLeft() } }
    SebhaContent(state, viewModel)
}
