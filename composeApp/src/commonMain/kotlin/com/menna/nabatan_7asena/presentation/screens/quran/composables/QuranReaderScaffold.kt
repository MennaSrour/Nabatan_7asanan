package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScaffold(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val snackbarState = layout.snackbar
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost   = { SnackbarHost(hostState = snackbarState) },
        bottomBar = { state.playlistPlayback?.let { QuranPlaylistPlayer(it, state.audioState, actions) } },
        topBar = {
            QuranReaderTopBar(state, actions, layout)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            QuranBackground()

            if (!state.isReaderReady) androidx.compose.material3.CircularProgressIndicator(Modifier.align(androidx.compose.ui.Alignment.Center))
            else if (state.viewMode == com.menna.nabatan_7asena.presentation.screens.quran.QuranViewMode.MUSHAF)
                QuranMushafPager(state, actions, layout, padding)
            else QuranSurahPager(state, actions, layout, padding)
        }
    }
}
