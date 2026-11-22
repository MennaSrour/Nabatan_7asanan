package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.core.util.SeasonalMode
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(state: HomeUiState, actions: HomeActions, snackbar: SnackbarHostState, onSettings: () -> Unit, onAddTask: () -> Unit, onPrayerGuide: () -> Unit, onBadges: () -> Unit, badgeCount: String, seasonalMode: SeasonalMode, onRamadanCalendar: () -> Unit, onFriends: () -> Unit = {}) {
    val background = when (seasonalMode) {
        SeasonalMode.DEFAULT -> if (state.isNightTheme) NightBackgroundGradient
            else Brush.verticalGradient(listOf(SummerTheme.colors.dayBackground, SummerTheme.colors.dayBackground))
        SeasonalMode.RAMADAN -> Brush.verticalGradient(
            if (state.isNightTheme) listOf(Color(0xFF302246), Color(0xFF534377))
            else listOf(Color(0xFFF0E7FC), Color(0xFFFFF7E4))
        )
        SeasonalMode.EID -> Brush.verticalGradient(
            if (state.isNightTheme) listOf(Color(0xFF123C49), Color(0xFF265B65))
            else listOf(Color(0xFFE0F6ED), Color(0xFFFFF2DF))
        )
    }
    Scaffold(containerColor = SummerTheme.colors.transparent, snackbarHost = { HomeSnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().background(background)) {
            HomeTaskFeed(state, actions, onSettings, onAddTask, padding.calculateBottomPadding(), onPrayerGuide, onBadges, badgeCount, seasonalMode, onRamadanCalendar, onFriends)
        }
    }
}
