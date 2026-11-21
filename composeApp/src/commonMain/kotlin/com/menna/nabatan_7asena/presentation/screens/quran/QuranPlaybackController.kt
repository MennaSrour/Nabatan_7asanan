package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.core.audio.QuranAudioPlayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Owned by QuranViewModel. One audio owner handles both single ayahs and repeat sessions. */
class QuranPlaybackController(
    private val player: QuranAudioPlayer,
    private val onPlaybackError: () -> Unit = {},
    private val audioUrl: (Int, Int) -> String
) {
    private val _state = MutableStateFlow(QuranPlaybackState())
    val state = _state.asStateFlow()
    private var generation = 0L
    private var session: RepeatSession? = null
    private var currentAyah: Pair<Int, Int>? = null
    private var paused = false

    fun openRepeatSheet(surahId: Int, ayahCount: Int) {
        if (surahId !in 1..114 || ayahCount < 1) return
        _state.update {
            it.copy(isRepeatBottomSheetVisible = true, repeatSurahId = surahId,
                repeatAyahCount = ayahCount, startAyah = 1, endAyah = ayahCount)
        }
    }

    fun dismissRepeatSheet() { _state.update { it.copy(isRepeatBottomSheetVisible = false) } }
    fun setScope(scope: PlaybackScope) { _state.update { it.copy(playbackScope = scope) } }
    fun setRepeatCount(count: Int) {
        if (count in repeatCountOptions) _state.update { it.copy(repeatCount = count) }
    }
    fun setStartAyah(ayah: Int) {
        _state.update {
            if (ayah !in 1..it.repeatAyahCount) it else it.copy(startAyah = ayah, endAyah = maxOf(ayah, it.endAyah))
        }
    }
    fun setEndAyah(ayah: Int) {
        _state.update {
            if (ayah !in it.startAyah..it.repeatAyahCount) it else it.copy(endAyah = ayah)
        }
    }

    fun startRepeat() {
        val settings = _state.value
        if (!settings.isRepeatBottomSheetVisible || settings.repeatAyahCount < 1) return
        val start = if (settings.playbackScope == PlaybackScope.FULL_SURAH) 1 else settings.startAyah
        val end = if (settings.playbackScope == PlaybackScope.FULL_SURAH) settings.repeatAyahCount else settings.endAyah
        stop()
        session = RepeatSession(settings.repeatSurahId, start, end, settings.repeatCount)
        _state.update { it.copy(isRepeatBottomSheetVisible = false, isRepeatModeActive = true,
            remainingRepeats = settings.repeatCount) }
        playTrack(settings.repeatSurahId, start)
    }

    fun playAyah(surahId: Int, ayah: Int) {
        if (currentAyah == (surahId to ayah) && _state.value.audioState !is QuranAudioState.Error) {
            paused = !paused
            if (paused) player.pause() else player.resume()
            _state.update { it.copy(audioState = if (paused) QuranAudioState.Paused(surahId, ayah)
                else QuranAudioState.Playing(surahId, ayah)) }
        } else {
            stop()
            playTrack(surahId, ayah)
        }
    }

    fun startPlaylist(id: String, name: String, tracks: List<PlaylistTrack>) {
        if (tracks.isEmpty() || tracks.any { it.surahId !in 1..114 || it.ayahCount < 1 }) return
        stop()
        _state.update { it.copy(playlist = PlaylistPlaybackState(id, name, tracks.toList())) }
        playTrack(tracks.first().surahId, 1)
    }

    fun togglePlaylistPlayback() {
        val current = currentAyah ?: return
        if (_state.value.playlist != null) playAyah(current.first, current.second)
    }

    fun togglePlaylistRepeat() {
        _state.update { it.copy(playlist = it.playlist?.let { queue -> queue.copy(repeat = !queue.repeat) }) }
    }

    fun nextPlaylistSurah() = selectPlaylistTrack(1)
    fun previousPlaylistSurah() = selectPlaylistTrack(-1)

    private fun selectPlaylistTrack(delta: Int) {
        val queue = _state.value.playlist ?: return
        val next = queue.currentIndex + delta
        val index = when {
            next in queue.tracks.indices -> next
            queue.repeat -> if (next < 0) queue.tracks.lastIndex else 0
            next < 0 -> 0
            else -> { stop(); return }
        }
        generation++
        player.stop()
        _state.update { it.copy(playlist = queue.copy(currentIndex = index)) }
        playTrack(queue.tracks[index].surahId, 1)
    }

    fun onSurahChanged(surahId: Int) {
        if (session?.surahId?.let { it != surahId } == true) stop()
        if (_state.value.repeatSurahId != surahId) dismissRepeatSheet()
    }

    fun stop() {
        generation++
        session = null
        currentAyah = null
        paused = false
        player.stop()
        _state.update { it.copy(audioState = QuranAudioState.Idle, isRepeatModeActive = false, remainingRepeats = 0, playlist = null) }
    }

    fun release() { stop(); player.release() }

    private fun playTrack(surahId: Int, ayah: Int) {
        val token = ++generation
        currentAyah = surahId to ayah
        paused = false
        _state.update { it.copy(audioState = QuranAudioState.Loading(surahId, ayah)) }
        try {
            player.play(audioUrl(surahId, ayah),
                onReady = { if (token == generation) updateAudio(surahId, ayah, loading = false) },
                onBuffering = { if (token == generation) updateAudio(surahId, ayah, loading = true) },
                onEnded = { if (token == generation) onAyahEnded(ayah) },
                onError = { if (token == generation) onPlaybackFailed(it) })
        } catch (exception: CancellationException) {
            stop()
            throw exception
        } catch (exception: Exception) {
            if (token == generation) onPlaybackFailed(exception)
        }
    }

    private fun updateAudio(surahId: Int, ayah: Int, loading: Boolean) {
        val audio = when {
            paused -> QuranAudioState.Paused(surahId, ayah)
            loading -> QuranAudioState.Loading(surahId, ayah)
            else -> QuranAudioState.Playing(surahId, ayah)
        }
        _state.update { it.copy(audioState = audio) }
    }

    private fun onAyahEnded(ayah: Int) {
        generation++ // Ignore duplicate completion callbacks from the finished track.
        val queue = _state.value.playlist
        if (queue != null) {
            val track = queue.tracks[queue.currentIndex]
            if (ayah < track.ayahCount) playTrack(track.surahId, ayah + 1)
            else nextPlaylistSurah()
            return
        }
        val active = session ?: run { stop(); return }
        if (ayah < active.endAyah) {
            playTrack(active.surahId, ayah + 1)
            return
        }
        val remaining = if (active.remaining == INFINITE_REPEATS) INFINITE_REPEATS else active.remaining - 1
        if (remaining == 0) { stop(); return }
        session = active.copy(remaining = remaining)
        _state.update { it.copy(remainingRepeats = remaining) }
        playTrack(active.surahId, active.startAyah)
    }

    private fun onPlaybackFailed(exception: Exception) {
        stop()
        _state.update { it.copy(audioState = QuranAudioState.Error(exception.message.orEmpty())) }
        onPlaybackError()
    }

    private data class RepeatSession(val surahId: Int, val startAyah: Int, val endAyah: Int, val remaining: Int)
}
