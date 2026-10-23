package com.menna.nabatan_7asena.presentation.screens.update

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.presentation.common.components.CollectEffects
import com.menna.nabatan_7asena.presentation.screens.update.composables.UpdateContent

@Composable
fun UpdateDialog(viewModel: UpdateViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            UpdateEffect.OpenStore -> {
                try { context.startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=com.menna.nabatan_7asena".toUri())) }
                catch (_: ActivityNotFoundException) { context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.menna.nabatan_7asena".toUri())) }
            }
        }
    }
    (state as? UpdateUiState.UpdateAvailable)?.let { UpdateContent(it.info, viewModel) }
}
