package com.menna.nabatan_7asena.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.usecase.ResetAppUseCase
import com.menna.nabatan_7asena.domain.usecase.UpdateUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appDao: AppDao,
    private val resetAppUseCase: ResetAppUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val globalAlarmManager: GlobalAlarmManager
) : ViewModel(), SettingsActions {

    val uiState = appDao.getUserStats().map { stats ->
        SettingsUiState(stats?.name.orEmpty(), stats?.prayerNotifications ?: true,
            stats?.azkarNotifications ?: true, stats?.quranNotifications ?: true)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState()
    )

    private val _uiEvent = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _uiEvent.receiveAsFlow()

    override fun toggleNotification(category: String, isEnabled: Boolean) {
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

    override fun resetProgress() {
        viewModelScope.launch {
            when (val res = resetAppUseCase()) {
                is Resource.Success -> {
                    _uiEvent.send(SettingsEffect.NavigateToLogin)
                }
                is Resource.Error -> {
                    _uiEvent.send(SettingsEffect.ShowError(res.throwable?.message ?: "Failed to reset progress"))
                }
                else -> {}
            }
        }
    }

    override fun updateUserName(newName: String) {
        viewModelScope.launch {
            when (val res = updateUserNameUseCase(newName)) {
                is Resource.Success -> {
                    _uiEvent.send(SettingsEffect.ShowInfo("تم تحديث الاسم"))
                }
                is Resource.Error -> {
                    _uiEvent.send(SettingsEffect.ShowError(res.throwable?.message ?: "Failed to update name"))
                }
                else -> {}
            }
        }
    }
}
