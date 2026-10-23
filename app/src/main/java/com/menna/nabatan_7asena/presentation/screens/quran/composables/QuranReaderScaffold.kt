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
internal fun QuranReaderScaffold(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val snackbarState = layout.snackbar
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost   = { SnackbarHost(hostState = snackbarState) },
        topBar = {
            QuranReaderTopBar(state, actions, layout)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            QuranBackground()

            QuranSurahPager(state, actions, layout, padding)
        }
    }
}
