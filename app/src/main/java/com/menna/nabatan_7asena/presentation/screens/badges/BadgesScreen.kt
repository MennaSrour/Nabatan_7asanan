package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.badges.composables.BadgesContent

@Composable
fun BadgesScreen(viewModel: BadgesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BadgesContent(state)
}
