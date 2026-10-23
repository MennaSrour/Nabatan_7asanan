package com.menna.nabatan_7asena.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.RegisterUserUseCase
import com.menna.nabatan_7asena.presentation.common.GenderedString
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun onGenderSelected(gender: User.Gender) {
        _state.value = _state.value.copy(selectedGender = gender)
    }

    fun onAvatarSelected(index: Int) {
        _state.value = if (_state.value.selectedGender == User.Gender.BOY) {
            _state.value.copy(selectedBoyAvatarIndex = index)
        } else {
            _state.value.copy(selectedGirlAvatarIndex = index)
        }
    }

    fun onStartClicked() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(LoginEvent.ShowError(GenderedStrings.loginNameRequired)) }
            return
        }

        // ⬅️ الأفاتار المختار فعليًا حسب النوع المختار، بدل ما يتمسح عند التسجيل
        val avatarIndex = if (currentState.selectedGender == User.Gender.BOY)
            currentState.selectedBoyAvatarIndex
        else
            currentState.selectedGirlAvatarIndex

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true)

            val result = registerUserUseCase(currentState.name, currentState.selectedGender, avatarIndex)
            when (result) {
                is Resource.Success -> {
                    _uiEvent.emit(LoginEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiEvent.emit(LoginEvent.ShowError(GenderedStrings.loginRegistrationError))
                }
                else -> {
                    _uiEvent.emit(LoginEvent.ShowError(GenderedStrings.unexpectedError))
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }
}


data class LoginState(
    val name: String = "",
    val selectedGender: User.Gender = User.Gender.BOY,
    val selectedBoyAvatarIndex: Int = 0,
    val selectedGirlAvatarIndex: Int = 0,
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    object NavigateToHome : LoginEvent()
    data class ShowError(val message: GenderedString) : LoginEvent()
}