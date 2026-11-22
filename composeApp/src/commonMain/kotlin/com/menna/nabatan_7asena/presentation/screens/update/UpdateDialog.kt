package com.menna.nabatan_7asena.presentation.screens.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.menna.nabatan.shared.generated.resources.Res
import com.menna.nabatan.shared.generated.resources.update_store_unavailable
import com.menna.nabatan_7asena.platform.openAppStore
import com.menna.nabatan_7asena.platform.platformUpdateViewModel
import org.jetbrains.compose.resources.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.update.composables.UpdateContent

@Composable
fun UpdateDialog(viewModel: UpdateViewModel = platformUpdateViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val update = (state as? UpdateUiState.UpdateAvailable)?.info
    var storeUnavailable by remember(update) { mutableStateOf(false) }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            UpdateEffect.OpenStore -> storeUnavailable = !openAppStore()
        }
    }
    if (update != null) {
        val info = if (storeUnavailable) update.copy(
            isForceUpdate = false,
            updateMessage = stringResource(Res.string.update_store_unavailable)
        ) else update
        UpdateContent(info, viewModel)
    }
}
