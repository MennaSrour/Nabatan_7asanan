package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

@Composable
fun HomeContent(uiState: HomeUiState, actions: HomeActions, snackbar: SnackbarHostState, onSettings: () -> Unit) {
    var showAddSheet by remember { mutableStateOf(false) }
    HomeLayout(uiState, actions, snackbar, onSettings,
        onAddTask = { showAddSheet = true; actions.loadSuggestions() })
    HomeOverlays(uiState, actions, showAddSheet) { showAddSheet = false }
}
