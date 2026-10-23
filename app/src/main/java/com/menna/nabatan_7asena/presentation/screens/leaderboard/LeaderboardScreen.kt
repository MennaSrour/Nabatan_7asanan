package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.leaderboard.composables.LeaderboardContent

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) { is LeaderboardEffect.ShowMessage -> snackbar.showSnackbar(effect.message) }
    }
    LeaderboardContent(state, snackbar)
}
