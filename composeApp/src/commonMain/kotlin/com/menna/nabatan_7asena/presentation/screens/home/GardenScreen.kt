package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.home.composables.garden.GardenContent
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun GardenScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    NabataAppTheme(state.isNightTheme) {
        Surface(Modifier.fillMaxSize(), color = SummerTheme.colors.backgroundLight) {
            Column(Modifier.safeDrawingPadding().verticalScroll(rememberScrollState())) {
                GardenContent(state, viewModel::waterGarden, expanded = true, onToggleSize = onBack)
            }
        }
    }
}
