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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import com.menna.nabatan_7asena.data.local.QuranLibraryStore
import com.menna.nabatan_7asena.data.repository.QuranJourneyRepository
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.platform.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

open class QuranViewModel constructor(
    private val saveUseCase: SaveQuranProgressUseCase,
    private val getUseCase: GetQuranProgressUseCase,
    private val repository: QuranRepository,
    private val audioPlayer: QuranAudioPlayer,
    private val downloadManager: QuranAudioDownloadManager,
    private val bookmarkStore: QuranBookmarkStore,
    private val library: QuranLibraryStore,
    private val journeyRepository: QuranJourneyRepository
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
    private var navigationRequest: kotlinx.coroutines.Job? = null
    private val playbackState = combine(lastSavedPage, playback.state, _surahDownloadStates, _fullDownloadState) { page, audio, downloads, full ->
        QuranUiState(savedPage = page, audioState = audio.audioState, downloadStates = downloads, fullDownloadState = full,
            isRepeatBottomSheetVisible = audio.isRepeatBottomSheetVisible, repeatSurahId = audio.repeatSurahId,
            repeatAyahCount = audio.repeatAyahCount, playbackScope = audio.playbackScope,
            startAyah = audio.startAyah, endAyah = audio.endAyah, repeatCount = audio.repeatCount,
            isRepeatModeActive = audio.isRepeatModeActive, remainingRepeats = audio.remainingRepeats, playlistPlayback = audio.playlist)
    }
    private val baseUiState = combine(playbackState, _bookmarkedSurah, _bookmarkedAyah, _ayahsBySurah, _position) { playback, surah, ayah, content, position ->
        playback.copy(bookmarkedSurahId = surah, bookmarkedAyahNo = ayah, ayahsBySurah = content,
            activeJuz = position.juz, currentPageNumber = position.page)
    }

    private val pages = MutableStateFlow<Map<Int, List<AyahModel>>>(emptyMap())
    private val readerReady = MutableStateFlow(false)
    private val editingPlaylist = MutableStateFlow<String?>(null)
    private val _requestedWirdPage = MutableStateFlow<Int?>(null)
    val requestedWirdPage = _requestedWirdPage.asStateFlow()
    private val temporaryMushafMode = MutableStateFlow(false)
    private val selectedMode = combine(library.state, temporaryMushafMode) { saved, useMushaf ->
        saved.copy(viewMode = if (useMushaf) "MUSHAF" else saved.viewMode)
    }
    private val day = flow {
        while (true) {
            emit(AppClock.today())
            delay(60_000L - AppClock.nowMillis() % 60_000L)
        }
    }.distinctUntilChanged()
    val journey = combine(journeyRepository.activeProfile, day) { profile, date ->
        profile?.let { QuranJourneyUiState(it.first, it.second, date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val uiState = combine(baseUiState, selectedMode, pages, readerReady, editingPlaylist) { ui, saved, content, ready, editing ->
        ui.copy(viewMode = QuranViewMode.entries.firstOrNull { it.name == saved.viewMode } ?: QuranViewMode.CARDS,
            playlists = saved.playlists, mushafPages = content, isReaderReady = ready, editingPlaylistId = editing)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuranUiState())

    override fun toggleViewMode() = saveLibrary {
        val useCards = temporaryMushafMode.value || library.state.value.viewMode == "MUSHAF"
        library.setMode(if (useCards) "CARDS" else "MUSHAF")
        temporaryMushafMode.value = false
        _requestedWirdPage.value = null
    }
    fun openWirdPage(page: Int) {
        require(page in 1..MUSHAF_PAGE_COUNT)
        navigationRequest?.cancel()
        temporaryMushafMode.value = true
        _requestedWirdPage.value = page
    }

    fun readerPage(): Int = _position.value.page

    fun onWirdPageOpened(page: Int) {
        onMushafPageChanged(page)
        _requestedWirdPage.compareAndSet(page, null)
    }

    fun startKhatmah(goal: WirdGoal) = updateJourney { journeyRepository.start(it.profileId, goal) }

    fun completeWird(date: LocalDate, wird: Wird) = updateJourney {
        journeyRepository.complete(it.profileId, date, wird)
    }

    fun dismissKhatmahCelebration() = updateJourney { journeyRepository.dismissCelebration(it.profileId) }

    fun updateMemorization(surahId: Int, status: MemorizationStatus, ayahs: Int, total: Int) =
        updateJourney { journeyRepository.updateSurah(it.profileId, surahId, status, ayahs, total) }

    private fun updateJourney(change: suspend (QuranJourneyUiState) -> Unit) {
        val current = journey.value ?: return
        viewModelScope.launch {
            try {
                change(current)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _effects.send(QuranEffect.JourneySaveFailed)
            }
        }
    }

    override fun selectSurah(surahId: Int) = navigateToAyah(surahId, 1)
    override fun createPlaylist(name: String) = saveLibrary { editingPlaylist.value = library.create(name) }
    override fun renamePlaylist(id: String, name: String) = saveLibrary { library.rename(id, name) }
    override fun deletePlaylist(id: String) = saveLibrary {
        library.delete(id)
        if (editingPlaylist.value == id) editingPlaylist.value = null
        if (playback.state.value.playlist?.id == id) playback.stop()
    }
    override fun editPlaylist(id: String?) { editingPlaylist.value = id }
    override fun addPlaylistSurah(id: String, surah: Int) = saveLibrary { library.addSurah(id, surah) }
    override fun removePlaylistSurah(id: String, surah: Int) = saveLibrary { library.removeSurah(id, surah) }
    override fun movePlaylistSurah(id: String, from: Int, to: Int) = saveLibrary { library.moveSurah(id, from, to) }
    override fun startPlaylist(id: String) {
        val playlist = library.state.value.playlists.firstOrNull { it.id == id } ?: return
        if (!readerReady.value || playlist.surahIds.isEmpty()) return
        val counts = pages.value.values.flatten().groupingBy { it.suraNo }.eachCount()
        val tracks = playlist.surahIds.map { PlaylistTrack(it, counts[it] ?: 0) }
        playback.startPlaylist(playlist.id, playlist.name, tracks)
    }
    override fun togglePlaylistPlayback() = playback.togglePlaylistPlayback()
    override fun nextPlaylistSurah() = playback.nextPlaylistSurah()
    override fun previousPlaylistSurah() = playback.previousPlaylistSurah()
    override fun setPlaylistRepeatCount(count: Int) = playback.setPlaylistRepeatCount(count)
    override fun togglePlaylistRepeat() = playback.togglePlaylistRepeat()

    private fun saveLibrary(action: () -> Unit) {
        try { action() }
        catch (e: CancellationException) { throw e }
        catch (_: Exception) { _effects.trySend(QuranEffect.LibrarySaveFailed) }
    }

    private fun navigateToAyah(surahId: Int, ayahNo: Int) {
        navigationRequest?.cancel()
        navigationRequest = viewModelScope.launch {
            try {
                val target = withContext(com.menna.nabatan_7asena.platform.ioDispatcher) {
                    repository.getSurahAyahs(surahId).firstOrNull { it.ayaNo == ayahNo }
                } ?: return@launch
                _position.value = QuranReadingPosition(target.page, target.jozz)
                _effects.send(QuranEffect.ScrollToAyah(surahId - 1, ayahNo, target.page))
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _effects.send(QuranEffect.LoadFailed) }
        }
    }

    fun onMushafPageChanged(page: Int) {
        val first = pages.value[page]?.firstOrNull() ?: return
        _position.value = QuranReadingPosition(page, first.jozz)
        onSurahVisible(first.suraNo)
    }

    override fun onSurahVisible(surahId: Int) {
        if (_ayahsBySurah.value.containsKey(surahId) || !loadingSurahs.add(surahId)) return
        viewModelScope.launch {
            try {
                val ayahs = withContext(com.menna.nabatan_7asena.platform.ioDispatcher) { repository.getSurahAyahs(surahId) }
                _ayahsBySurah.update { it + (surahId to ayahs) }
                withContext(com.menna.nabatan_7asena.platform.ioDispatcher) { refreshSurahDownloadState(surahId) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _effects.send(QuranEffect.LoadFailed)
            } finally { loadingSurahs.remove(surahId) }
        }
    }

    fun onVisibleAyahChanged(surahId: Int, visibleIndex: Int) {
        val ayahs = _ayahsBySurah.value[surahId].orEmpty()
        readingPosition(ayahs, surahId, visibleIndex)?.let {
            if (_position.value != it) {
                _position.value = it
            }
        }
    }

    init {
        loadBookmark()
        if (library.loadFailed) _effects.trySend(QuranEffect.LoadFailed)
        viewModelScope.launch {
            _position.map { it.page }.drop(1).distinctUntilChanged().collect { page ->
                try { saveUseCase(page) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { _effects.send(QuranEffect.LoadFailed) }
            }
        }
        viewModelScope.launch {
            playback.state.map { it.playlist?.let { queue -> queue.id to queue.tracks[queue.currentIndex].surahId } }
                .distinctUntilChanged().collect { track -> track?.let { navigateToAyah(it.second, 1) } }
        }
        viewModelScope.launch {
            try {
                val saved = getUseCase.getLastPage().first().coerceIn(1, 604)
                val content = withContext(com.menna.nabatan_7asena.platform.ioDispatcher) {
                    (1..114).flatMap { repository.getSurahAyahs(it) }.groupBy { it.page }
                }
                pages.value = content
                _position.value = QuranReadingPosition(saved, content[saved]?.firstOrNull()?.jozz ?: 1)
                readerReady.value = true
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _effects.send(QuranEffect.LoadFailed) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playback.release()
    }

    override fun onSurahChanged(surahId: Int) {
        playback.onSurahChanged(surahId)
        onSurahVisible(surahId)
        val surah = QuranConstants.surahs.getOrNull(surahId - 1) ?: return
        if (pages.value[_position.value.page]?.none { it.suraNo == surahId } != false)
            _position.value = QuranReadingPosition(surah.startPage, surah.juz)
    }

    private fun getSurahAyahs(suraNumber: Int): List<AyahModel> = repository.getSurahAyahs(suraNumber)

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
        navigationRequest?.cancel()
        val target = pages.value.values.asSequence().flatten().filter { it.jozz == juz }.minByOrNull { it.id } ?: return
        _position.value = QuranReadingPosition(target.page, target.jozz)
        _effects.trySend(QuranEffect.ScrollToAyah(target.suraNo - 1, target.ayaNo, target.page))
    }

    override fun goToBookmark() {
        val surah = _bookmarkedSurah.value
        val ayah  = _bookmarkedAyah.value
        if (surah > 0 && ayah > 0) {
            navigateToAyah(surah, ayah)
        }
    }

    override fun onOpenRepeatBottomSheet(surahId: Int) {
        playback.openRepeatSheet(surahId, _ayahsBySurah.value[surahId]?.size
            ?: pages.value.values.sumOf { ayahs -> ayahs.count { it.suraNo == surahId } })
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
