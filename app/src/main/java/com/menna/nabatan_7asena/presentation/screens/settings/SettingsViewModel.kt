package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.repository.UserRepository
import com.menna.nabatan_7asena.domain.usecase.ResetAppUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateProfileImageUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateUserNameUseCase
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
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
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val resetAppUseCase: ResetAppUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase
) : ViewModel(), SettingsActions {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update {
                    it.copy(name = user.name, userId = user.id, gender = user.gender,
                        avatarIndex = user.avatarIndex,
                        prayerNotifications = user.prayerNotifications,
                        azkarNotifications = user.azkarNotifications,
                        quranNotifications = user.quranNotifications)
                }
            }
        }
    }

    override fun onEditAvatarClicked() {
        _uiState.update {
            if (it.userId == null || it.isSavingAvatar) it else it.copy(showImagePickerDialog = true)
        }
    }

    override fun onImagePickerDismissed() {
        _uiState.update { it.copy(showImagePickerDialog = false) }
    }

    override fun onAvatarSelected(imageIdentifier: Int) {
        val profile = _uiState.value
        val userId = profile.userId ?: return
        if (!profile.showImagePickerDialog || profile.isSavingAvatar) return
        if (imageIdentifier !in AvatarCatalog.listFor(profile.gender).indices) return
        if (imageIdentifier == profile.avatarIndex) { onImagePickerDismissed(); return }
        _uiState.update { it.copy(isSavingAvatar = true) }
        viewModelScope.launch { saveAvatar(profile, userId, imageIdentifier) }
    }

    private suspend fun saveAvatar(profile: SettingsUiState, userId: String, index: Int) {
        try {
            when (updateProfileImageUseCase(userId, profile.gender, index)) {
                is Resource.Success -> _uiState.update {
                    if (it.userId != userId || it.gender != profile.gender) it else it.copy(
                        avatarIndex = index,
                        showImagePickerDialog = false)
                }
                else -> _uiEvent.send(SettingsEffect.AvatarSaveFailed)
            }
        } finally {
            _uiState.update { it.copy(isSavingAvatar = false) }
        }
    }

    override fun toggleNotification(category: String, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.updateNotificationPreference(category, isEnabled)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiEvent.send(SettingsEffect.ShowError(exception.message ?: "Failed to update notifications"))
            }
        }
    }

    override fun resetProgress() {
        viewModelScope.launch {
            when (val result = resetAppUseCase()) {
                is Resource.Success -> _uiEvent.send(SettingsEffect.NavigateToLogin)
                is Resource.Error -> _uiEvent.send(SettingsEffect.ShowError(result.throwable?.message ?: "Failed to reset progress"))
                else -> Unit
            }
        }
    }

    override fun updateUserName(newName: String) {
        viewModelScope.launch {
            when (val result = updateUserNameUseCase(newName)) {
                is Resource.Success -> _uiEvent.send(SettingsEffect.ShowInfo("تم تحديث الاسم"))
                is Resource.Error -> _uiEvent.send(SettingsEffect.ShowError(result.throwable?.message ?: "Failed to update name"))
                else -> Unit
            }
        }
    }
}
