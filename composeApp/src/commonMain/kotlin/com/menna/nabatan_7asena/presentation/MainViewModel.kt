package com.menna.nabatan_7asena.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabatan_7asena.platform.SchedulingPlatform
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

open class MainViewModel(getCurrentUser: GetCurrentUserUseCase, private val scheduling: SchedulingPlatform) : ViewModel() {
    val uiState = getCurrentUser().map { MainUiState(it.gender) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())
    fun enableDailyAutoScheduling() = scheduling.enableDailyAutoScheduling()
    fun startManagerWorkerImmediately() = scheduling.scheduleImmediately()
}
data class MainUiState(val gender: User.Gender = User.Gender.BOY)
