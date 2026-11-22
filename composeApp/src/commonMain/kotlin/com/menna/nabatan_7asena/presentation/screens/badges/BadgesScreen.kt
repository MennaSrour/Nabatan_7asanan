package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.badges.composables.BadgesContent

@Composable
fun BadgesScreen(onBack: () -> Unit, viewModel: BadgesViewModel = platformBadgesViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BadgesContent(state, onBack)
}
