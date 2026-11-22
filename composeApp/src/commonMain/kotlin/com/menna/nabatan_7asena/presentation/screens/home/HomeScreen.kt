package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.menna.nabatan_7asena.platform.platformHomeViewModel
import com.menna.nabatan_7asena.platform.platformBadgesViewModel
import com.menna.nabatan_7asena.presentation.screens.badges.badgeCountLabel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.home.composables.HomeContent

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToQuran: (Int?) -> Unit,
    onNavigateToKhatmah: () -> Unit,
    onNavigateToSebha: () -> Unit,
    onNavigateToGarden: () -> Unit,
    onNavigateToWheel: () -> Unit,
    onNavigateToPrayerGuide: () -> Unit,
    onNavigateToBadges: () -> Unit,
    completedExternalTaskId: Int? = null,
    viewModel: HomeViewModel = platformHomeViewModel()
) {
    androidx.lifecycle.compose.LifecycleResumeEffect(Unit) {
        viewModel.refreshPrayerLocation()
        onPauseOrDispose { viewModel.stopPrayerLocationUpdates() }
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val badges by platformBadgesViewModel().uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(completedExternalTaskId) { completedExternalTaskId?.let(viewModel::markTaskCompletedExternally) }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is HomeEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            HomeEffect.NavigateToDailyGift -> onNavigateToWheel()
            HomeEffect.NavigateToGarden -> onNavigateToGarden()
            is HomeEffect.NavigateToQuran -> onNavigateToQuran(effect.page)
            HomeEffect.NavigateToKhatmah -> onNavigateToKhatmah()
            HomeEffect.NavigateToSebha -> onNavigateToSebha()
        }
    }
    HomeContent(state, viewModel, snackbar, onNavigateToSettings, onNavigateToPrayerGuide, onNavigateToBadges, badges.badgeCountLabel())
    if (state.showKhatmahCelebration) {
        com.menna.nabatan_7asena.presentation.screens.quran.composables.KhatmahCelebrationDialog(
            onDismiss = { viewModel.dismissKhatmahCelebration(false) },
            onStartNew = { viewModel.dismissKhatmahCelebration(true) }
        )
    }
}
