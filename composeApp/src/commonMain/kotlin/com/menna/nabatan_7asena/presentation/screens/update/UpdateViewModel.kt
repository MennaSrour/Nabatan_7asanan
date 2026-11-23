package com.menna.nabatan_7asena.presentation.screens.update

import com.menna.nabatan_7asena.platform.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.usecase.CheckForUpdateUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

open class UpdateViewModel constructor(
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
                _uiState.value = UpdateUiState.Loading

                val result = checkForUpdateUseCase()

                if (result.isUpdateAvailable) {
                    _uiState.value = UpdateUiState.UpdateAvailable(result)
                } else {
                    _uiState.value = UpdateUiState.Idle
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
