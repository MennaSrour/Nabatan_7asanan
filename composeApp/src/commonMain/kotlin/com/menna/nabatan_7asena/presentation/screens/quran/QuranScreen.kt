package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.quran.composables.QuranContent

@Composable
fun QuranScreen(viewModel: QuranViewModel = platformQuranViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val layout = rememberQuranLayoutState(state.savedPage)
    QuranScreenEffects(state, layout, viewModel)
    QuranContent(state, viewModel, layout)
}
