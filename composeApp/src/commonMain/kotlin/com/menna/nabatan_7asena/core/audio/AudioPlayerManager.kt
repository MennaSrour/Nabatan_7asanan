package com.menna.nabatan_7asena.core.audio

import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState

interface AudioPlayerManager {
    fun playTaskSound(item: HomeUiState.UiTaskItem, onCompletion: () -> Unit)
    fun playWelcomeSound(onCompletion: () -> Unit = {})
    fun playCelebrationSound(onCompletion: () -> Unit = {})
    fun playCorrectAnswerSound(onCompletion: () -> Unit = {})
    fun playAlarmSound(soundResId: Int, onCompletion: () -> Unit = {})
    fun playDuaOrZekrSound(soundResId: Int, onCompletion: () -> Unit = {})
    fun stopAnyPlayingSound()
}
