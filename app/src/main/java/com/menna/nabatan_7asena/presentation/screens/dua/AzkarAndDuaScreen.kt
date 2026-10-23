package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.screens.dua.composables.DuaContent

@Composable
fun AzkarAndDuaScreen(viewModel: DuaViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DuaContent(state, viewModel)
}
