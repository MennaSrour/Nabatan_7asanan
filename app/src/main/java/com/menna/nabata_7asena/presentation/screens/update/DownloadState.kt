package com.menna.nabata_7asena.presentation.screens.update

import com.menna.nabata_7asena.domain.entity.UpdateInfo

sealed class DownloadState {
    object Idle : DownloadState()
    data class Progress(val percent: Int) : DownloadState()
    object Completed : DownloadState()
    data class Error(val message: String) : DownloadState()
}