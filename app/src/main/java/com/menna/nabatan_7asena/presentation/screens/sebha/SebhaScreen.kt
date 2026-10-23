package com.menna.nabatan_7asena.presentation.screens.sebha

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.sebha.composables.SebhaContent

@Composable
fun SebhaScreen(viewModel: SebhaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) { onDispose { viewModel.onScreenLeft() } }
    SebhaContent(state, viewModel)
}
