package com.menna.nabatan_7asena.presentation.screens.update

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.usecase.CheckForUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase,
) : ViewModel(), UpdateActions {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<UpdateEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init { checkForUpdates() }

    override fun onUpdateClicked() { viewModelScope.launch { _effects.send(UpdateEffect.OpenStore) } }

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("UPDATE", "Error checking for updates", e)
                _uiState.value = UpdateUiState.Idle
            }
        }
    }

    override fun dismissUpdate() {
        _uiState.value = UpdateUiState.Idle
    }
}
