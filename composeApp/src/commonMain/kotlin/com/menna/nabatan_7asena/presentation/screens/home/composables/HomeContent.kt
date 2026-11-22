package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.menna.nabatan_7asena.core.util.SeasonalMode
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

@Composable
fun HomeContent(uiState: HomeUiState, actions: HomeActions, snackbar: SnackbarHostState, onSettings: () -> Unit, onPrayerGuide: () -> Unit, onBadges: () -> Unit, badgeCount: String, seasonalMode: SeasonalMode, onRamadanCalendar: () -> Unit, onFriends: () -> Unit = {}) {
    var showAddSheet by remember { mutableStateOf(false) }
    HomeLayout(uiState, actions, snackbar, onSettings,
        onPrayerGuide = onPrayerGuide, onBadges = onBadges, badgeCount = badgeCount,
        seasonalMode = seasonalMode, onRamadanCalendar = onRamadanCalendar, onFriends = onFriends,
        onAddTask = { showAddSheet = true; actions.loadSuggestions() })
    HomeOverlays(uiState, actions, showAddSheet) { showAddSheet = false }
}
