package com.menna.nabata_7asena.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.User 
import com.menna.nabata_7asena.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun onStartClicked() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(LoginEvent.ShowError("اكتب اسمك يا بطل! ✍️")) }
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true)

            val result = registerUserUseCase(currentState.name, currentState.selectedGender)
            when (result) {
                is Resource.Success -> {
                    _uiEvent.emit(LoginEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiEvent.emit(LoginEvent.ShowError(result.throwable?.message ?: "حدث خطأ أثناء التسجيل"))
                }
                else -> {
                    _uiEvent.emit(LoginEvent.ShowError("حدث خطأ غير متوقع"))
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }
}


data class LoginState(
    val name: String = "",
    val selectedGender: User.Gender = User.Gender.BOY,
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    object NavigateToHome : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}