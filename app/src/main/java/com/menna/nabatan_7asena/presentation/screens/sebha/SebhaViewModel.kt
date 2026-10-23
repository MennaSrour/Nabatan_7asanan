package com.menna.nabatan_7asena.presentation.screens.sebha

import androidx.lifecycle.ViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SebhaViewModel @Inject constructor(
    private val audioPlayer: ScreenAudioPlayer
) : ViewModel(), SebhaActions {

    private val azkarList = listOf(
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

    private val _state = MutableStateFlow(SebhaUiState(azkarList = azkarList))
    val state = _state.asStateFlow()

    override fun selectZikr(zikr: ZikrItem) {
        stopAudio()
        _state.value = _state.value.copy(
            isSelectionMode = false,
            currentZikr = zikr,
            currentCount = 0,
            totalCycles = 0,
            showCelebration = false
        )
    }

    override fun startFreeTasbeeh(text: String, targetCount: Int?) {
        stopAudio()
        val zikr = ZikrItem(
            id = -1,
            text = text.ifBlank { "تسبيح حر" },
            target = (targetCount ?: 0).coerceAtLeast(0),
            audioResId = 0,
            color = 0xFF8D6E63,
            isCustom = true
        )
        _state.value = SebhaUiState(
            azkarList = azkarList,
            isSelectionMode = false,
            currentZikr = zikr,
            currentCount = 0,
            totalCycles = 0,
            isPlayingAudio = false,
            showCelebration = false
        )
    }

    override fun backToMenu() {
        stopAudio()
        _state.value = _state.value.copy(isSelectionMode = true)
    }

    override fun onTasbeehClick() {
        val currentState = _state.value
        val zikr = currentState.currentZikr ?: return

        if (currentState.showCelebration) {
            dismissCelebration()
            return
        }

        val newCount = currentState.currentCount + 1

        if (zikr.target <= 0) {
            _state.value = currentState.copy(currentCount = newCount)
            return
        }

        if (newCount >= zikr.target) {
            stopAudio()
            audioPlayer.play(R.raw.good)

            _state.value = currentState.copy(
                currentCount = zikr.target,
                totalCycles = currentState.totalCycles + 1,
                showCelebration = true
            )
        } else {
            _state.value = currentState.copy(currentCount = newCount)
        }
    }

    override fun resetCounter() {
        _state.value = _state.value.copy(currentCount = 0, showCelebration = false)
    }

    override fun toggleAudio() {
        val currentState = _state.value
        val zikr = currentState.currentZikr ?: return

        if (currentState.isPlayingAudio) {
            stopAudio()
        } else {
            playAudio(zikr.audioResId)
        }
    }

    override fun dismissCelebration() {
        stopAudio()
        _state.value = _state.value.copy(
            showCelebration = false,
            currentCount = 0
        )
    }

    private fun playAudio(resId: Int) {
        if (resId == 0) return
        _state.value = _state.value.copy(isPlayingAudio = true)
        audioPlayer.play(resId) {
            _state.value = _state.value.copy(isPlayingAudio = false)
        }
    }

    fun onScreenLeft() = stopAudio()

    private fun stopAudio() {
        audioPlayer.stop()
        _state.value = _state.value.copy(isPlayingAudio = false)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}
