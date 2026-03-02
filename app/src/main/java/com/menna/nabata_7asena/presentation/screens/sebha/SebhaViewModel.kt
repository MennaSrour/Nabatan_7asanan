package com.menna.nabata_7asena.presentation.screens.sebha

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.menna.nabata_7asena.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ZikrItem(
    val id: Int,
    val text: String,
    val target: Int = 33,
    val audioResId: Int = 0,
    val color: Long = 0xFF42A5F5
)

data class AzkarState(
    val isSelectionMode: Boolean = true,
    val currentZikr: ZikrItem? = null,
    val currentCount: Int = 0,
    val totalCycles: Int = 0,
    val isPlayingAudio: Boolean = false,
    val showCelebration: Boolean = false
)

@HiltViewModel
class SebhaViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val azkarList = listOf(
        ZikrItem(1, "سبحان الله", 100, R.raw.sub7an__100, 0xFFEF5350),
        ZikrItem(2, "الحمد لله", 100, R.raw.alhamd, 0xFF66BB6A),
        ZikrItem(3, "الله أكبر", 100, R.raw.allah_akbar, 0xFFFFA726),
        ZikrItem(4, "لا إله إلا الله", 100, R.raw.la_illah_ila_allah_100, 0xFF42A5F5),
        ZikrItem(5, "لا حول ولا قوة إلا بالله", 100, R.raw.la_hawl_100, 0xFFAB47BC),
        ZikrItem(
            6,
            "سبحان الله وبحمده سبحان الله العظيم",
            10,
            R.raw.subhan_7md_azem_10,
            0xFF26C6DA
        ),
        ZikrItem(7, "اللهم صلِّ على محمد", 100, R.raw.saly_100, 0xFFEC407A),
        ZikrItem(8, "أستغفر الله", 100, R.raw.yala_zekr, 0xFFFFA726)
    )

    private val _state = MutableStateFlow(AzkarState())
    val state = _state.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    fun selectZikr(zikr: ZikrItem) {
        _state.value = _state.value.copy(
            isSelectionMode = false,
            currentZikr = zikr,
            currentCount = 0,
            showCelebration = false
        )
    }

    fun backToMenu() {
        stopAudio()
        _state.value = _state.value.copy(isSelectionMode = true)
    }

    fun onTasbeehClick() {
        val currentState = _state.value
        val zikr = currentState.currentZikr ?: return


        if (currentState.showCelebration) {
            dismissCelebration()
            return
        }

        val newCount = currentState.currentCount + 1

        if (newCount >= zikr.target) {

            _state.value = currentState.copy(
                currentCount = zikr.target,
                totalCycles = currentState.totalCycles + 1,
                showCelebration = true
            )
        } else {
            _state.value = currentState.copy(currentCount = newCount)
        }
    }

    fun resetCounter() {
        _state.value = _state.value.copy(currentCount = 0, showCelebration = false)
    }

    fun toggleAudio() {
        val currentState = _state.value
        val zikr = currentState.currentZikr ?: return

        if (currentState.isPlayingAudio) {
            stopAudio()
        } else {
            playAudio(zikr.audioResId)
        }
    }


    fun dismissCelebration() {
        _state.value = _state.value.copy(
            showCelebration = false,
            currentCount = 0
        )
    }

    private fun playAudio(resId: Int) {
        if (resId == 0) return
        stopAudio()
        try {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.setOnCompletionListener {
                _state.value = _state.value.copy(isPlayingAudio = false)
            }
            mediaPlayer?.start()
            _state.value = _state.value.copy(isPlayingAudio = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _state.value = _state.value.copy(isPlayingAudio = false)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}