package com.menna.nabatan_7asena.presentation.screens.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.menna.nabatan_7asena.platform.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.update.composables.UpdateContent

@Composable
fun UpdateDialog(viewModel: UpdateViewModel = platformUpdateViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            UpdateEffect.OpenStore -> com.menna.nabatan_7asena.platform.openAppStore()

        }
    }
    (state as? UpdateUiState.UpdateAvailable)?.let { UpdateContent(it.info, viewModel) }
}
