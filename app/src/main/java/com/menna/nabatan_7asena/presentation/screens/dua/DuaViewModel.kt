package com.menna.nabatan_7asena.presentation.screens.dua

import androidx.lifecycle.ViewModel
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel() {

    // الـ resId بتاع الصوت الشغال دلوقتي، أو null لو مفيش حاجة شغالة.
    // بنستخدمه عشان نعرف نرسم أيقونة "تشغيل" ولا "إيقاف" على كل كارت.
    private val _playingResId = MutableStateFlow<Int?>(null)
    val playingResId = _playingResId.asStateFlow()

    /**
     * نفس الزرار للتشغيل والإيقاف:
     * - لو الصوت ده هو اللي شغال بالفعل → دوس تاني يوقفه.
     * - لو صوت تاني شغال أو مفيش حاجة شغالة → يشغّل ده (وبيقفل القديم تلقائي).
     * - لو الصوت خلص لوحده، onCompletion بترجع الأيقونة لحالة "تشغيل" تلقائيًا.
     */
    fun toggleAudio(soundResId: Int) {
        if (_playingResId.value == soundResId) {
            audioPlayerManager.stopAnyPlayingSound()
            _playingResId.value = null
        } else {
            audioPlayerManager.playDuaOrZekrSound(soundResId) {
                _playingResId.value = null
            }
            _playingResId.value = soundResId
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stopAnyPlayingSound()
    }
}