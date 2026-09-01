package com.menna.nabatan_7asena.presentation.screens.update

sealed class DownloadState {
    object Idle : DownloadState()
    data class Progress(val percent: Int) : DownloadState()
    object Completed : DownloadState()
    data class Error(val message: String) : DownloadState()
}