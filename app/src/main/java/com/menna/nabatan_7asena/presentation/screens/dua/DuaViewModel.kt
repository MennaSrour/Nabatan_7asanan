package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.lifecycle.ViewModel
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel(), DuaActions {

    // الـ resId بتاع الصوت الشغال دلوقتي، أو null لو مفيش حاجة شغالة.
    // بنستخدمه عشان نعرف نرسم أيقونة "تشغيل" ولا "إيقاف" على كل كارت.
    private val _uiState = MutableStateFlow(DuaUiState())
    val uiState = _uiState.asStateFlow()

    /**
    * نفس الزرار للتشغيل والإيقاف:
    * - لو الصوت ده هو اللي شغال بالفعل → دوس تاني يوقفه.
    * - لو صوت تاني شغال أو مفيش حاجة شغالة → يشغّل ده (وبيقفل القديم تلقائي).
    * - لو الصوت خلص لوحده، onCompletion بترجع الأيقونة لحالة "تشغيل" تلقائيًا.
    */
    override fun toggleAudio(soundResId: Int) {
        if (_uiState.value.playingResId == soundResId) {
            audioPlayerManager.stopAnyPlayingSound()
            _uiState.value = DuaUiState()
        } else {
            audioPlayerManager.playDuaOrZekrSound(soundResId) {
                _uiState.value = DuaUiState()
            }
            _uiState.value = DuaUiState(soundResId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stopAnyPlayingSound()
    }
}
