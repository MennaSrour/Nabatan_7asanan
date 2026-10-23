package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.core.audio.EveryAyahUrlBuilder
import com.menna.nabatan_7asena.core.audio.QuranAudioDownloadManager
import com.menna.nabatan_7asena.core.audio.QuranAudioPlayer
import com.menna.nabatan_7asena.core.audio.SurahDownloadProgress
import com.menna.nabatan_7asena.data.local.QuranBookmarkStore
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.domain.repository.QuranRepository
import com.menna.nabatan_7asena.domain.usecase.GetQuranProgressUseCase
import com.menna.nabatan_7asena.domain.usecase.SaveQuranProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuranAudioState {
    data object Idle : QuranAudioState()

    data class Loading(val suraNo: Int, val ayaNo: Int) : QuranAudioState()
    data class Playing(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    data class Paused(val suraNo: Int, val ayaNo: Int) : QuranAudioState()

    data class Error(val message: String) : QuranAudioState()
}

data class SurahDownloadState(
    val downloadedAyahs: Int = 0,
    val totalAyahs: Int = 0,
    val isDownloading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

data class FullQuranDownloadState(
    val isDownloading: Boolean = false,
    val completedSurahs: Int = 0,
    val totalSurahs: Int = 114,
    val currentSurahName: String = "",
    val isComplete: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val saveUseCase: SaveQuranProgressUseCase,
    private val getUseCase: GetQuranProgressUseCase,
    private val repository: QuranRepository,
    private val audioPlayer: QuranAudioPlayer,
    private val downloadManager: QuranAudioDownloadManager,
    private val bookmarkStore: QuranBookmarkStore
) : ViewModel() {

    val lastSavedPage = getUseCase.getLastPage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    private val _bookmarkedSurah = MutableStateFlow(-1)
    val bookmarkedSurah = _bookmarkedSurah.asStateFlow()

    private val _bookmarkedAyah = MutableStateFlow(-1)
    val bookmarkedAyah = _bookmarkedAyah.asStateFlow()

   data class QuranScrollTarget(val surahIndex: Int, val ayaNo: Int)

    private val _scrollTarget = MutableSharedFlow<QuranScrollTarget>(extraBufferCapacity = 1)
    val scrollTarget = _scrollTarget.asSharedFlow()
    private val _audioState = MutableStateFlow<QuranAudioState>(QuranAudioState.Idle)
    val audioState = _audioState.asStateFlow()
    private val _surahDownloadStates = MutableStateFlow<Map<Int, SurahDownloadState>>(emptyMap())
    val surahDownloadStates = _surahDownloadStates.asStateFlow()

    private val _fullDownloadState = MutableStateFlow(FullQuranDownloadState())
    val fullDownloadState = _fullDownloadState.asStateFlow()

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
        bookmarkStore.saveBookmark(surahId, ayahNo)
        _bookmarkedSurah.value = surahId
        _bookmarkedAyah.value = ayahNo
    }

    fun loadBookmark() {
        val bookmark = bookmarkStore.getBookmark()
        _bookmarkedSurah.value = bookmark?.surahId ?: -1
        _bookmarkedAyah.value = bookmark?.ayahNo ?: -1
    }

     fun goToJuz(juz: Int) {
        val startPage = getStartPageForJuz(juz)
        val surah = QuranConstants.getSurahByPage(startPage)
        viewModelScope.launch {
            val ayahs = repository.getSurahAyahs(surah.id)
            val targetAyah = ayahs.firstOrNull { it.jozz == juz }
                ?: ayahs.firstOrNull { it.page >= startPage }
            val ayaNo = targetAyah?.ayaNo ?: 1
            _scrollTarget.emit(QuranScrollTarget(surah.id - 1, ayaNo))
        }
    }

    fun goToBookmark() {
        val surah = _bookmarkedSurah.value
        val ayah  = _bookmarkedAyah.value
        if (surah > 0 && ayah > 0) {
            viewModelScope.launch {
                _scrollTarget.emit(QuranScrollTarget(surah - 1, ayah))
            }
        }
    }

     fun playAyahAudio(suraNo: Int, ayaNo: Int) {
        val current = _audioState.value

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

          val url = downloadManager.localUriIfExists(suraNo, ayaNo)
            ?: EveryAyahUrlBuilder.buildUrl(suraNo, ayaNo)
        _audioState.value = QuranAudioState.Loading(suraNo, ayaNo)

        audioPlayer.play(
            url = url,
            onBuffering = {
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

    fun refreshSurahDownloadState(suraId: Int) {
        val ayahsCount = getSurahAyahs(suraId).size
        if (ayahsCount == 0) return
        val done = downloadManager.downloadedAyahsCount(suraId, ayahsCount)
        val current = _surahDownloadStates.value.toMutableMap()
        if (current[suraId]?.isDownloading == true) return
        current[suraId] = SurahDownloadState(
            downloadedAyahs = done,
            totalAyahs = ayahsCount,
            isComplete = done == ayahsCount
        )
        _surahDownloadStates.value = current
    }

    private fun setSurahState(suraId: Int, state: SurahDownloadState) {
        val current = _surahDownloadStates.value.toMutableMap()
        current[suraId] = state
        _surahDownloadStates.value = current
    }
    fun downloadSurah(suraId: Int) {
        val ayahsCount = getSurahAyahs(suraId).size
        if (ayahsCount == 0 || downloadManager.isSurahDownloaded(suraId, ayahsCount)) return
        if (_surahDownloadStates.value[suraId]?.isDownloading == true) return

        viewModelScope.launch {
            downloadManager.downloadSurah(suraId, ayahsCount).collect { progress ->
                setSurahState(
                    suraId,
                    when (progress) {
                        is SurahDownloadProgress.InProgress -> SurahDownloadState(
                            downloadedAyahs = progress.downloaded,
                            totalAyahs = progress.total,
                            isDownloading = true
                        )
                        is SurahDownloadProgress.Completed -> SurahDownloadState(
                            downloadedAyahs = ayahsCount,
                            totalAyahs = ayahsCount,
                            isComplete = true
                        )
                        is SurahDownloadProgress.Failed -> SurahDownloadState(
                            downloadedAyahs = downloadManager.downloadedAyahsCount(suraId, ayahsCount),
                            totalAyahs = ayahsCount,
                            error = "فشل تنزيل الآية ${progress.ayaNo}"
                        )
                    }
                )
            }
        }
    }

    fun deleteSurahDownload(suraId: Int) {
        val ayahsCount = getSurahAyahs(suraId).size
        downloadManager.deleteSurah(suraId, ayahsCount)
        setSurahState(suraId, SurahDownloadState(totalAyahs = ayahsCount))
    }
    fun downloadFullQuran() {
        if (_fullDownloadState.value.isDownloading) return

        viewModelScope.launch {
            _fullDownloadState.value = FullQuranDownloadState(
                isDownloading = true,
                totalSurahs = QuranConstants.surahs.size
            )
            for (surah in QuranConstants.surahs) {
                val ayahsCount = getSurahAyahs(surah.id).size
                if (ayahsCount == 0) continue

                if (!downloadManager.isSurahDownloaded(surah.id, ayahsCount)) {
                    var failed = false
                    downloadManager.downloadSurah(surah.id, ayahsCount).collect { progress ->
                        setSurahState(
                            surah.id,
                            when (progress) {
                                is SurahDownloadProgress.InProgress -> SurahDownloadState(
                                    downloadedAyahs = progress.downloaded,
                                    totalAyahs = progress.total,
                                    isDownloading = true
                                )
                                is SurahDownloadProgress.Completed -> SurahDownloadState(
                                    downloadedAyahs = ayahsCount,
                                    totalAyahs = ayahsCount,
                                    isComplete = true
                                )
                                is SurahDownloadProgress.Failed -> {
                                    failed = true
                                    SurahDownloadState(
                                        downloadedAyahs = downloadManager.downloadedAyahsCount(surah.id, ayahsCount),
                                        totalAyahs = ayahsCount,
                                        error = "فشل تنزيل الآية ${progress.ayaNo}"
                                    )
                                }
                            }
                        )
                    }
                    if (failed) {
                        _fullDownloadState.value = _fullDownloadState.value.copy(
                            isDownloading = false,
                            error = "فشل التنزيل عند سورة ${surah.name}"
                        )
                        return@launch
                    }
                }

                _fullDownloadState.value = _fullDownloadState.value.copy(
                    completedSurahs = surah.id,
                    currentSurahName = surah.name
                )
            }
            _fullDownloadState.value = _fullDownloadState.value.copy(
                isDownloading = false,
                isComplete = true
            )
        }
    }
}