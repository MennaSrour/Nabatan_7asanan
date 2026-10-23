package com.menna.nabatan_7asena.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.RegisterUserUseCase
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val audioPlayer: ScreenAudioPlayer
) : ViewModel(), LoginActions {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<LoginEffect>(Channel.BUFFERED)
    val effects = _uiEvent.receiveAsFlow()

    override fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    override fun onGenderSelected(gender: User.Gender) {
        _state.value = _state.value.copy(selectedGender = gender)
    }

    override fun onAvatarSelected(index: Int) {
        _state.value = if (_state.value.selectedGender == User.Gender.BOY) {
            _state.value.copy(selectedBoyAvatarIndex = index)
        } else {
            _state.value.copy(selectedGirlAvatarIndex = index)
        }
    }

    override fun onStartClicked() {
        val currentState = _state.value
        if (currentState.isLoading) return
        if (currentState.name.isBlank()) {
            viewModelScope.launch { _uiEvent.send(LoginEffect.ShowError(GenderedStrings.loginNameRequired)) }
            return
        }

        // ⬅️ الأفاتار المختار فعليًا حسب النوع المختار، بدل ما يتمسح عند التسجيل
        val avatarIndex = if (currentState.selectedGender == User.Gender.BOY) {
            currentState.selectedBoyAvatarIndex
        } else {
            currentState.selectedGirlAvatarIndex
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val result = registerUserUseCase(currentState.name, currentState.selectedGender, avatarIndex)
                when (result) {
                    is Resource.Success -> {
                        _uiEvent.send(LoginEffect.NavigateToHome)
                    }
                    is Resource.Error -> {
                        _uiEvent.send(LoginEffect.ShowError(GenderedStrings.loginRegistrationError))
                    }
                    else -> {
                        _uiEvent.send(LoginEffect.ShowError(GenderedStrings.unexpectedError))
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    fun onScreenEntered() = audioPlayer.play(R.raw.name)
    fun onScreenLeft() = audioPlayer.stop()
    override fun onCleared() { audioPlayer.stop() }

}
