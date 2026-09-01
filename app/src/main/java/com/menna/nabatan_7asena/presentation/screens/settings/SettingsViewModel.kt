package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.usecase.ResetAppUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appDao: AppDao,
    private val resetAppUseCase: ResetAppUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val globalAlarmManager: GlobalAlarmManager
) : ViewModel() {

    val userStats = appDao.getUserStats().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    private val _uiEvent = MutableSharedFlow<SettingsEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun toggleNotification(category: String, isEnabled: Boolean) {
        viewModelScope.launch {
            when (category) {
                "prayer" -> {
                    appDao.updatePrayerNotif(isEnabled)
                    // لو المستخدم قفل التنبيه، نلغي أي alarms متجدولة فعلياً في النظام فورًا
                    if (!isEnabled) globalAlarmManager.cancelPrayerAlarms()
                }
                "azkar" -> {
                    appDao.updateAzkarNotif(isEnabled)
                    if (!isEnabled) globalAlarmManager.cancelAzkarAlarms()
                }
                "quran" -> {
                    appDao.updateQuranNotif(isEnabled)
                    if (!isEnabled) globalAlarmManager.cancelQuranAlarms()
                }
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            when (val res = resetAppUseCase()) {
                is Resource.Success -> {
                    _uiEvent.emit(SettingsEvent.ShowInfo("تم إعادة التعيين"))
                }
                is Resource.Error -> {
                    _uiEvent.emit(SettingsEvent.ShowError(res.throwable?.message ?: "Failed to reset progress"))
                }
                else -> {}
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            when (val res = updateUserNameUseCase(newName)) {
                is Resource.Success -> {
                    _uiEvent.emit(SettingsEvent.ShowInfo("تم تحديث الاسم"))
                }
                is Resource.Error -> {
                    _uiEvent.emit(SettingsEvent.ShowError(res.throwable?.message ?: "Failed to update name"))
                }
                else -> {}
            }
        }
    }
}

sealed class SettingsEvent {
    data class ShowError(val message: String) : SettingsEvent()
    data class ShowInfo(val message: String) : SettingsEvent()
}