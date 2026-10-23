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
import com.menna.nabatan_7asena.presentation.screens.home.HomeActions
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeLayout(state: HomeUiState, actions: HomeActions, snackbar: SnackbarHostState, onSettings: () -> Unit, onAddTask: () -> Unit) {
    val background = if (state.isNightTheme) NightBackgroundGradient
    else Brush.verticalGradient(listOf(SummerTheme.colors.dayBackground, SummerTheme.colors.dayBackground))
    Scaffold(containerColor = SummerTheme.colors.transparent, snackbarHost = { HomeSnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().background(background)) {
            HomeTaskFeed(state, actions, onSettings, onAddTask, padding.calculateBottomPadding())
        }
    }
}
