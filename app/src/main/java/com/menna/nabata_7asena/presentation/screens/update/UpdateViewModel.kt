package com.menna.nabata_7asena.presentation.screens.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.data.update.ApkDownloader
import com.menna.nabata_7asena.domain.usecase.CheckForUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState = _downloadState.asStateFlow()

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    fun openPlayStore(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "market://details?id=com.menna.nabata_7asena".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            try {
                Log.d("UPDATE", "Checking for updates...")
                _uiState.value = UpdateUiState.Loading

                val result = checkForUpdateUseCase()
                Log.d("UPDATE", "Update check result: $result")
                Log.d("UPDATE", "Download URL: ${result.downloadUrl}")

                if (result.isUpdateAvailable) {
                    _uiState.value = UpdateUiState.UpdateAvailable(result)
                    Log.d("UPDATE", "Update available: ${result.latestVersionName}")
                } else {
                    _uiState.value = UpdateUiState.Idle
                    Log.d("UPDATE", "No update available")
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "Error checking for updates", e)
                _uiState.value = UpdateUiState.Idle
            }
        }
    }

    fun dismissUpdate() {
        _uiState.value = UpdateUiState.Idle
    }
}