package com.menna.nabatan_7asena.presentation.screens.start

import com.menna.nabatan_7asena.platform.PlatformPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

open class StartViewModel constructor(
    private val dao: AppDao,
    private val introPlayer: ScreenAudioPlayer
) : ViewModel() {
    private val preferences = PlatformPreferences("app_prefs")
    private val _uiState = MutableStateFlow(StartUiState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<StartEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private val audioFinished = MutableStateFlow(false)
    private var hasStarted = false

    fun onScreenEntered() {
        if (hasStarted) return
        hasStarted = true
        introPlayer.play(R.raw.welcome_kids) { audioFinished.value = true }
        viewModelScope.launch {
            delay(2_000)
            val destination = destination()
            audioFinished.first { it }
            _uiState.value = StartUiState(isVisible = false)
            delay(800)
            _effects.send(destination)
        }
    }

    private suspend fun destination(): StartEffect = when {
        !preferences.getBoolean("is_onboarding_completed", false) -> StartEffect.NavigateToOnboarding
        !dao.getUserStats().firstOrNull()?.userId.isNullOrBlank() -> StartEffect.NavigateToHome
        else -> StartEffect.NavigateToLogin
    }

    fun onScreenLeft() { introPlayer.stop(); audioFinished.value = true }
    override fun onCleared() { introPlayer.stop() }
}
