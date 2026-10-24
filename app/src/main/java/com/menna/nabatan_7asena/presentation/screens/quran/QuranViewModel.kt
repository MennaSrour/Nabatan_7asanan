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
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val saveUseCase: SaveQuranProgressUseCase,
    private val getUseCase: GetQuranProgressUseCase,
    private val repository: QuranRepository,
    private val audioPlayer: QuranAudioPlayer,
    private val downloadManager: QuranAudioDownloadManager,
    private val bookmarkStore: QuranBookmarkStore
) : ViewModel(), QuranActions {

    private val lastSavedPage = getUseCase.getLastPage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    private val _bookmarkedSurah = MutableStateFlow(-1)

    private val _bookmarkedAyah = MutableStateFlow(-1)

    private val _effects = Channel<QuranEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private val playback = QuranPlaybackController(audioPlayer, onPlaybackError = {
        _effects.trySend(QuranEffect.PlaybackFailed)
    }) { surah, ayah ->
        downloadManager.localUriIfExists(surah, ayah) ?: EveryAyahUrlBuilder.buildUrl(surah, ayah)
    }
    private val _surahDownloadStates = MutableStateFlow<Map<Int, SurahDownloadState>>(emptyMap())

    private val _fullDownloadState = MutableStateFlow(FullQuranDownloadState())

    private val _ayahsBySurah = MutableStateFlow<Map<Int, List<AyahModel>>>(emptyMap())
    private val _position = MutableStateFlow(QuranReadingPosition())
    private val loadingSurahs = mutableSetOf<Int>()
    private val playbackState = combine(lastSavedPage, playback.state, _surahDownloadStates, _fullDownloadState) { page, audio, downloads, full ->
        QuranUiState(savedPage = page, audioState = audio.audioState, downloadStates = downloads, fullDownloadState = full,
            isRepeatBottomSheetVisible = audio.isRepeatBottomSheetVisible, repeatSurahId = audio.repeatSurahId,
            repeatAyahCount = audio.repeatAyahCount, playbackScope = audio.playbackScope,
            startAyah = audio.startAyah, endAyah = audio.endAyah, repeatCount = audio.repeatCount,
            isRepeatModeActive = audio.isRepeatModeActive, remainingRepeats = audio.remainingRepeats)
    }
    val uiState = combine(playbackState, _bookmarkedSurah, _bookmarkedAyah, _ayahsBySurah, _position) { playback, surah, ayah, content, position ->
        playback.copy(bookmarkedSurahId = surah, bookmarkedAyahNo = ayah, ayahsBySurah = content,
            activeJuz = position.juz, currentPageNumber = position.page)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuranUiState())

    override fun onSurahVisible(surahId: Int) {
        if (_ayahsBySurah.value.containsKey(surahId) || !loadingSurahs.add(surahId)) return
        viewModelScope.launch {
            try {
                val ayahs = withContext(Dispatchers.IO) { repository.getSurahAyahs(surahId) }
                _ayahsBySurah.update { it + (surahId to ayahs) }
                withContext(Dispatchers.IO) { refreshSurahDownloadState(surahId) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _effects.send(QuranEffect.LoadFailed)
            } finally { loadingSurahs.remove(surahId) }
        }
    }

    fun onVisibleAyahChanged(surahId: Int, visibleIndex: Int) {
        val ayahs = _ayahsBySurah.value[surahId].orEmpty()
        readingPosition(ayahs, surahId, visibleIndex)?.let { _position.value = it }
    }

    init {
        loadBookmark()
    }

    override fun onCleared() {
        super.onCleared()
        playback.release()
    }

    override fun onSurahChanged(surahId: Int) {
        playback.onSurahChanged(surahId)
        onSurahVisible(surahId)
        val surah = QuranConstants.surahs.getOrNull(surahId - 1) ?: return
        viewModelScope.launch {
            saveUseCase.invoke(surah.startPage)
        }
    }

    private fun getSurahAyahs(suraNumber: Int): List<AyahModel> = repository.getSurahAyahs(suraNumber)

    private fun getStartPageForJuz(juz: Int): Int {
        val juzStartPages = listOf(
            1, 22, 42, 62, 82, 102, 122, 142, 162, 182,
            202, 222, 242, 262, 282, 302, 322, 342, 362, 382,
            402, 422, 442, 462, 482, 502, 522, 542, 562, 582
        )
        return juzStartPages.getOrNull(juz - 1) ?: 1
    }

    override fun saveBookmark(surahId: Int, ayahNo: Int) {
        bookmarkStore.saveBookmark(surahId, ayahNo)
        _bookmarkedSurah.value = surahId
        _bookmarkedAyah.value = ayahNo
        _effects.trySend(QuranEffect.BookmarkSaved)
    }

    fun loadBookmark() {
        val bookmark = bookmarkStore.getBookmark()
        _bookmarkedSurah.value = bookmark?.surahId ?: -1
        _bookmarkedAyah.value = bookmark?.ayahNo ?: -1
    }

    override fun goToJuz(juz: Int) {
        val startPage = getStartPageForJuz(juz)
        val surah = QuranConstants.getSurahByPage(startPage)
        viewModelScope.launch {
            val ayahs = withContext(Dispatchers.IO) { repository.getSurahAyahs(surah.id) }
            val targetAyah = ayahs.firstOrNull { it.jozz == juz }
            ?: ayahs.firstOrNull { it.page >= startPage }
            val ayaNo = targetAyah?.ayaNo ?: 1
            _effects.send(QuranEffect.ScrollToAyah(surah.id - 1, ayaNo))
        }
    }

    override fun goToBookmark() {
        val surah = _bookmarkedSurah.value
        val ayah  = _bookmarkedAyah.value
        if (surah > 0 && ayah > 0) {
            viewModelScope.launch {
                _effects.send(QuranEffect.ScrollToAyah(surah - 1, ayah))
            }
        }
    }

    override fun onOpenRepeatBottomSheet(surahId: Int) {
        playback.openRepeatSheet(surahId, _ayahsBySurah.value[surahId]?.size ?: 0)
    }
    override fun onDismissRepeatBottomSheet() = playback.dismissRepeatSheet()
    override fun onPlaybackScopeChanged(scope: PlaybackScope) = playback.setScope(scope)
    override fun onStartAyahChanged(ayah: Int) = playback.setStartAyah(ayah)
    override fun onEndAyahChanged(ayah: Int) = playback.setEndAyah(ayah)
    override fun onRepeatCountChanged(count: Int) = playback.setRepeatCount(count)
    override fun onStartRepeatClicked() = playback.startRepeat()
    override fun playAyahAudio(suraNo: Int, ayaNo: Int) = playback.playAyah(suraNo, ayaNo)
    override fun stopAudio() = playback.stop()

    private fun refreshSurahDownloadState(suraId: Int) {
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
        _surahDownloadStates.update { it + (suraId to state) }
    }
    override fun downloadSurah(suraId: Int) {
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

    override fun downloadFullQuran() {
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
