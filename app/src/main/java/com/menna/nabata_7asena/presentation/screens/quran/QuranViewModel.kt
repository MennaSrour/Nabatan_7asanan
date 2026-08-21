package com.menna.nabata_7asena.presentation.screens.quran

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.core.audio.EveryAyahUrlBuilder
import com.menna.nabata_7asena.core.audio.QuranAudioPlayer
import com.menna.nabata_7asena.domain.QuranConstants
import com.menna.nabata_7asena.domain.entity.AyahModel
import com.menna.nabata_7asena.domain.repository.QuranRepository
import com.menna.nabata_7asena.domain.usecase.GetQuranProgressUseCase
import com.menna.nabata_7asena.domain.usecase.SaveQuranProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────
// حالة تشغيل الصوت
// ─────────────────────────────────────────────────────────────
sealed class QuranAudioState {
    /** مفيش تشغيل */
    data object Idle : QuranAudioState()

    /** بيحمّل — بنعرض Spinner */
    data class Loading(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    /** شغّال — بنعرض Pause */
    data class Playing(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    /** موقف — بنعرض Play */
    data class Paused(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    /** خطأ في التحميل */
    data class Error(val message: String) : QuranAudioState()
}

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val saveUseCase: SaveQuranProgressUseCase,
    private val getUseCase: GetQuranProgressUseCase,
    private val repository: QuranRepository,
    private val audioPlayer: QuranAudioPlayer,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)

    val lastSavedPage = getUseCase.getLastPage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    private val _bookmarkedSurah = MutableStateFlow(-1)
    val bookmarkedSurah = _bookmarkedSurah.asStateFlow()

    private val _bookmarkedAyah = MutableStateFlow(-1)
    val bookmarkedAyah = _bookmarkedAyah.asStateFlow()

    private val _scrollToSurahEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val scrollToSurahEvent = _scrollToSurahEvent.asSharedFlow()

    private val _scrollToAyahEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val scrollToAyahEvent = _scrollToAyahEvent.asSharedFlow()

    // ─── حالة الصوت ───────────────────────────────────────────
    private val _audioState = MutableStateFlow<QuranAudioState>(QuranAudioState.Idle)
    val audioState = _audioState.asStateFlow()

    init {
        loadBookmark()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        _audioState.value = QuranAudioState.Idle
    }

    fun onSurahChanged(surahId: Int) {
        val surah = QuranConstants.surahs.getOrNull(surahId - 1) ?: return
        viewModelScope.launch {
            saveUseCase.invoke(surah.startPage)
        }
    }

    fun getSurahAyahs(suraNumber: Int): List<AyahModel> = repository.getSurahAyahs(suraNumber)

    fun getSurahInfo(suraNumber: Int) =
        QuranConstants.surahs.getOrNull(suraNumber - 1) ?: QuranConstants.surahs.first()

    fun getStartPageForJuz(juz: Int): Int {
        val juzStartPages = listOf(
            1, 22, 42, 62, 82, 102, 122, 142, 162, 182,
            202, 222, 242, 262, 282, 302, 322, 342, 362, 382,
            402, 422, 442, 462, 482, 502, 522, 542, 562, 582
        )
        return juzStartPages.getOrNull(juz - 1) ?: 1
    }

    fun saveBookmark(surahId: Int, ayahNo: Int) {
        sharedPrefs.edit()
            .putInt("bookmark_surah", surahId)
            .putInt("bookmark_ayah", ayahNo)
            .apply()
        _bookmarkedSurah.value = surahId
        _bookmarkedAyah.value = ayahNo
    }

    fun loadBookmark() {
        _bookmarkedSurah.value = sharedPrefs.getInt("bookmark_surah", -1)
        _bookmarkedAyah.value  = sharedPrefs.getInt("bookmark_ayah",  -1)
    }

    fun goToBookmark() {
        val surah = _bookmarkedSurah.value
        val ayah  = _bookmarkedAyah.value
        if (surah > 0 && ayah > 0) {
            viewModelScope.launch {
                _scrollToSurahEvent.emit(surah - 1)
                delay(300)
                _scrollToAyahEvent.emit(ayah)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // تشغيل / إيقاف / استئناف صوت الآية
    // ─────────────────────────────────────────────────────────────
    fun playAyahAudio(suraNo: Int, ayaNo: Int) {
        val current = _audioState.value

        // لو نفس الآية — toggle play/pause
        if (current is QuranAudioState.Playing && current.suraNo == suraNo && current.ayaNo == ayaNo) {
            audioPlayer.pause()
            _audioState.value = QuranAudioState.Paused(suraNo, ayaNo)
            return
        }

        if (current is QuranAudioState.Paused && current.suraNo == suraNo && current.ayaNo == ayaNo) {
            audioPlayer.resume()
            _audioState.value = QuranAudioState.Playing(suraNo, ayaNo)
            return
        }

        // آية جديدة — ابدأ من أول
        val url = EveryAyahUrlBuilder.buildUrl(suraNo, ayaNo)
        _audioState.value = QuranAudioState.Loading(suraNo, ayaNo)

        audioPlayer.play(
            url = url,
            onBuffering = {
                // لو لسه في نفس الآية، ابقى Loading
                val s = _audioState.value
                if (s !is QuranAudioState.Playing || (s.suraNo == suraNo && s.ayaNo == ayaNo)) {
                    _audioState.value = QuranAudioState.Loading(suraNo, ayaNo)
                }
            },
            onReady = {
                _audioState.value = QuranAudioState.Playing(suraNo, ayaNo)
            },
            onEnded = {
                _audioState.value = QuranAudioState.Idle
            }
        )
    }

    fun stopAudio() {
        audioPlayer.stop()
        _audioState.value = QuranAudioState.Idle
    }
}