package com.menna.nabata_7asena.presentation.screens.update

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

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase,
    private val downloader: ApkDownloader
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState = _downloadState.asStateFlow()

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    fun startDownload(url: String) {
        viewModelScope.launch {
            try {
                Log.d("UPDATE", "Starting download from: $url")
                _downloadState.value = DownloadState.Progress(0)

                if (!downloader.canInstallApks()) {
                    Log.w("UPDATE", "Cannot install APKs - requesting permission")
                    _downloadState.value = DownloadState.Error(
                        "يجب السماح بتثبيت التطبيقات من مصادر غير معروفة"
                    )
                    downloader.requestInstallPermission()
                    return@launch
                }

                val result = downloader.downloadApk(url) { progress ->
                    _downloadState.value = DownloadState.Progress(progress)
                }

                result.onSuccess { file ->
                    Log.d("UPDATE", "Download completed: ${file.absolutePath}")
                    _downloadState.value = DownloadState.Completed

                    val installResult = downloader.installApk(file)
                    installResult.onFailure { error ->
                        Log.e("UPDATE", "Installation failed", error)
                        _downloadState.value = DownloadState.Error(
                            error.message ?: "فشل التثبيت"
                        )
                    }
                }.onFailure { error ->
                    Log.e("UPDATE", "Download failed", error)
                    _downloadState.value = DownloadState.Error(
                        error.message ?: "فشل التحميل، حاول مرة أخرى"
                    )
                }

            } catch (e: Exception) {
                Log.e("UPDATE", "Unexpected error during download", e)
                _downloadState.value = DownloadState.Error(
                    "حدث خطأ غير متوقع: ${e.message}"
                )
            }
        }
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