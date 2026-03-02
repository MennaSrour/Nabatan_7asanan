package com.menna.nabata_7asena.presentation.screens.update

import com.menna.nabata_7asena.domain.entity.UpdateInfo

sealed class UpdateUiState {
    object Idle : UpdateUiState()
    object Loading : UpdateUiState()
    data class UpdateAvailable(val info: UpdateInfo) : UpdateUiState()
}