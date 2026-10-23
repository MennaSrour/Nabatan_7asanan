package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.components.NoInternetState
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTaskFeed(state: HomeUiState, actions: HomeActions, onSettings: () -> Unit, onAddTask: () -> Unit, bottomPadding: androidx.compose.ui.unit.Dp) {
    val tint = if (state.isNightTheme) SummerTheme.colors.white else SummerTheme.colors.sectionTitleDark
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = bottomPadding + 90.dp)) {
        item { ColorfulHeader(state.user, state.hijriDate, onSettings, actions::playWelcomeSound, state.isNightTheme) }
        if (state.isLoading) item { HomeLoadingIndicator() } else {
            homeSummaryItems(state)
            if (state.hasLoadError && state.tasks.isEmpty()) {
                item { NoInternetState(title = genderedStringResource(GenderedStrings.homeLoadError), onRetry = actions::retryLoadingData) }
            } else {
                homePrayerItems(state, actions, tint)
                homeDailyTaskItems(state, actions, tint)
                item { HomeTreasureSummary(state, actions::openDailyChallenge) }
            }
            item { HomeAddTaskButton(onAddTask) }
        }
    }
}
