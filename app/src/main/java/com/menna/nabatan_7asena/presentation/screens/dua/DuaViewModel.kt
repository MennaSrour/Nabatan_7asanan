package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.lifecycle.ViewModel
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel() {

    fun playAudio(soundResId: Int) {
        audioPlayerManager.playDuaOrZekrSound(soundResId)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stopAnyPlayingSound()
    }
}
