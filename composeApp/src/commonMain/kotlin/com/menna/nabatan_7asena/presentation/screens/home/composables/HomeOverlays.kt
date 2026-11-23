package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOverlays(state: HomeUiState, actions: HomeActions, showAddSheet: Boolean, onCloseSheet: () -> Unit) {
    if (state.showCelebration) CelebrationOverlay(
        onDismiss = actions::dismissCelebration,
        onOpenDailyGift = { actions.openDailyGift("أكمل مهام اليوم لفتح العجلة") }
    )
    if (showAddSheet) AddTaskBottomSheet(onDismiss = onCloseSheet, suggestions = state.suggestions, existingTitles = state.tasks.map { it.title },
        onTaskSelected = { actions.onAddExtraTask(it); onCloseSheet() })
}
