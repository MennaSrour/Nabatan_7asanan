package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaActions
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaUiState

@Composable
fun SebhaContent(state: SebhaUiState, actions: SebhaActions) {
    var showFreeTasbeehDialog by remember { mutableStateOf(false) }

    AnimatedContent(targetState = state.isSelectionMode, label = "azkar_transition") { isSelection ->
        if (isSelection) {
            ZikrSelectionMenuSummer(
                azkarList = state.azkarList,
                onSelect = { actions.selectZikr(it) },
                onFreeTasbeehClick = { showFreeTasbeehDialog = true }
            )
        } else {
            ZikrCounterViewSummer(
                state = state,
                onBack = { actions.backToMenu() },
                onTasbeeh = { actions.onTasbeehClick() },
                onReset = { actions.resetCounter() },
                onToggleSound = { actions.toggleAudio() },
                onDismissCelebration = { actions.dismissCelebration() }
            )
        }
    }

    if (showFreeTasbeehDialog) {
        FreeTasbeehDialog(
            onDismiss = { showFreeTasbeehDialog = false },
            onConfirm = { text, target ->
                showFreeTasbeehDialog = false
                actions.startFreeTasbeeh(text, target)
            }
        )
    }
}
