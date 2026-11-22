package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.home.composables.HomeContent

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToSebha: () -> Unit,
    onNavigateToGarden: () -> Unit,
    completedExternalTaskId: Int? = null,
    viewModel: HomeViewModel = platformHomeViewModel(),
    onNavigateToFriends: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(completedExternalTaskId) { completedExternalTaskId?.let(viewModel::markTaskCompletedExternally) }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is HomeEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            HomeEffect.NavigateToGarden -> onNavigateToGarden()
            HomeEffect.NavigateToQuran -> onNavigateToQuran()
            HomeEffect.NavigateToSebha -> onNavigateToSebha()
        }
    }
    HomeContent(state, viewModel, snackbar, onNavigateToSettings, onNavigateToFriends)
}
