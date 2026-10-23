package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeOverlays(state: HomeUiState, actions: HomeActions, showAddSheet: Boolean, onCloseSheet: () -> Unit) {
    if (state.showCelebration) CelebrationOverlay(actions::dismissCelebration)
    if (showAddSheet) AddTaskBottomSheet(onDismiss = onCloseSheet, suggestions = state.suggestions,
        onTaskSelected = { actions.onAddExtraTask(it); onCloseSheet() })
    state.currentRiddle?.takeIf { state.showChallengeDialog }?.let { riddle ->
        DailyChallengeDialog(riddle, actions::closeChallenge) { if (it) actions.onCorrectAnswer() }
    }
}
