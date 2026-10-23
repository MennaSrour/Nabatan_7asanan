package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.home.composables.HomeContent

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToSebha: () -> Unit,
    completedExternalTaskId: Int? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(completedExternalTaskId) { completedExternalTaskId?.let(viewModel::markTaskCompletedExternally) }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is HomeEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            HomeEffect.NavigateToQuran -> onNavigateToQuran()
            HomeEffect.NavigateToSebha -> onNavigateToSebha()
        }
    }
    HomeContent(state, viewModel, snackbar, onNavigateToSettings)
}
