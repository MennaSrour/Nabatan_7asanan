package com.menna.nabatan_7asena.presentation.screens.quran

import com.menna.nabatan_7asena.core.audio.QuranAudioPlayer
import org.junit.Assert.*
import org.junit.Test

class QuranPlaybackControllerTest {
    private val player = FakePlayer()
    private val playback = QuranPlaybackController(player) { surah, ayah -> "$surah/$ayah" }

    @Test fun customRangePlaysThreeCompletePassesThenStops() {
        customRange(start = 2, end = 3, count = 3)
        repeat(6) { player.complete() }
        assertEquals(listOf("2/2", "2/3", "2/2", "2/3", "2/2", "2/3"), player.urls)
        assertFalse(playback.state.value.isRepeatModeActive)
        assertEquals(0, playback.state.value.remainingRepeats)
        assertEquals(QuranAudioState.Idle, playback.state.value.audioState)
        assertEquals(3, playback.state.value.repeatCount)
    }

    @Test fun fullSurahIgnoresCustomBoundsAndPlaysEveryAyahOnce() {
        playback.openRepeatSheet(2, 4)
        playback.setStartAyah(2)
        playback.setEndAyah(3)
        playback.setRepeatCount(1)
        playback.startRepeat()
        repeat(4) { player.complete() }
        assertEquals(listOf("2/1", "2/2", "2/3", "2/4"), player.urls)
        assertFalse(playback.state.value.isRepeatModeActive)
    }

    @Test fun singleAyahRangeWorksAndCannotRunPastItsEnd() {
        customRange(3, 3, 5)
        repeat(5) { player.complete() }
        assertEquals(List(5) { "2/3" }, player.urls)
        assertFalse(playback.state.value.isRepeatModeActive)
    }

    @Test fun infiniteLoopStopsOnlyWhenExplicitlyStopped() {
        customRange(2, 3, INFINITE_REPEATS)
        repeat(20) { player.complete() }
        assertTrue(playback.state.value.isRepeatModeActive)
        assertEquals(INFINITE_REPEATS, playback.state.value.remainingRepeats)
        val oldTrack = player.track
        playback.stop()
        val count = player.urls.size
        oldTrack.ended()
        oldTrack.ready()
        assertEquals(count, player.urls.size)
        assertEquals(QuranAudioState.Idle, playback.state.value.audioState)
    }

    @Test fun rangeValidationKeepsBoundsOrderedAndWithinTheSurah() {
        playback.openRepeatSheet(2, 4)
        playback.setEndAyah(2)
        playback.setStartAyah(3)
        assertEquals(3, playback.state.value.endAyah)
        playback.setStartAyah(0)
        playback.setEndAyah(5)
        playback.setEndAyah(2)
        assertEquals(3, playback.state.value.startAyah)
        assertEquals(3, playback.state.value.endAyah)
        playback.setRepeatCount(0)
        assertEquals(3, playback.state.value.repeatCount)
    }

    @Test fun pauseResumeKeepsTheRepeatSessionAndReadyCannotUndoPause() {
        customRange(2, 3, 3)
        playback.playAyah(2, 2)
        player.track.ready()
        player.track.buffering()
        assertEquals(QuranAudioState.Paused(2, 2), playback.state.value.audioState)
        assertEquals(1, player.pauses)
        assertTrue(playback.state.value.isRepeatModeActive)
        playback.playAyah(2, 2)
        assertEquals(1, player.resumes)
        player.complete()
        assertEquals("2/3", player.urls.last())
    }

    @Test fun choosingADifferentAyahCancelsRepeatAndInvalidatesOldCallbacks() {
        customRange(2, 3, 3)
        val oldTrack = player.track
        playback.playAyah(2, 4)
        oldTrack.ended()
        oldTrack.ready()
        oldTrack.failed(IllegalStateException("Old failure"))
        assertEquals(listOf("2/2", "2/4"), player.urls)
        assertEquals(QuranAudioState.Loading(2, 4), playback.state.value.audioState)
        assertFalse(playback.state.value.isRepeatModeActive)
        player.complete()
        assertEquals(QuranAudioState.Idle, playback.state.value.audioState)
    }

    @Test fun changingSurahStopsRepeatAndClosesItsSheet() {
        customRange(2, 3, 3)
        playback.openRepeatSheet(2, 4)
        playback.onSurahChanged(3)
        assertFalse(playback.state.value.isRepeatModeActive)
        assertFalse(playback.state.value.isRepeatBottomSheetVisible)
    }

    @Test fun editingDraftDoesNotMutateTheRunningPassage() {
        customRange(2, 3, 3)
        playback.openRepeatSheet(2, 4)
        playback.setStartAyah(4)
        playback.setRepeatCount(1)
        playback.dismissRepeatSheet()
        player.complete()
        player.complete()
        assertEquals(listOf("2/2", "2/3", "2/2"), player.urls)
        assertEquals(2, playback.state.value.remainingRepeats)
    }

    @Test fun repeatedStartAndCompletionCallbacksCannotCreateParallelLoops() {
        customRange(2, 3, 3)
        playback.startRepeat() // Sheet is already closed.
        val completed = player.track
        completed.ended()
        completed.ended()
        assertEquals(listOf("2/2", "2/3"), player.urls)
    }

    @Test fun playerFailureEndsRepeatAndOldCompletionCannotRestartIt() {
        customRange(2, 3, INFINITE_REPEATS)
        val track = player.track
        track.failed(IllegalStateException("Offline"))
        track.ended()
        assertTrue(playback.state.value.audioState is QuranAudioState.Error)
        assertFalse(playback.state.value.isRepeatModeActive)
        assertEquals(1, player.urls.size)
    }

    @Test fun unavailableOrInvalidSurahCannotStartRepeat() {
        playback.openRepeatSheet(2, 0)
        playback.openRepeatSheet(115, 4)
        playback.startRepeat()
        assertTrue(player.urls.isEmpty())
        assertFalse(playback.state.value.isRepeatBottomSheetVisible)
    }

    @Test fun synchronousAudioSourceFailureEndsRepeat() {
        val failing = QuranPlaybackController(player) { _, _ -> error("Source unavailable") }
        failing.openRepeatSheet(2, 4)
        failing.startRepeat()
        assertTrue(failing.state.value.audioState is QuranAudioState.Error)
        assertFalse(failing.state.value.isRepeatModeActive)
    }

    @Test fun releaseInvalidatesCallbacksAndReleasesPlayer() {
        customRange(2, 3, INFINITE_REPEATS)
        val track = player.track
        playback.release()
        track.ended()
        assertTrue(player.released)
        assertEquals(1, player.urls.size)
    }

    @Test fun duplicateErrorsProduceOneEffectPerTrack() {
        var errors = 0
        val owner = QuranPlaybackController(player, onPlaybackError = { errors++ }) { _, _ -> "url" }
        owner.playAyah(2, 1)
        val old = player.track
        old.failed(IllegalStateException("Offline"))
        old.failed(IllegalStateException("Duplicate"))
        assertEquals(1, errors)
        owner.playAyah(2, 1)
        player.track.failed(IllegalStateException("Retry failed"))
        assertEquals(2, errors)
    }

    @Test fun playlistPlaysAllAyahsAndSurahsInOrderThenStops() {
        playback.startPlaylist("list", "Morning", listOf(PlaylistTrack(93, 2), PlaylistTrack(89, 1)))
        repeat(3) { player.complete() }
        assertEquals(listOf("93/1", "93/2", "89/1"), player.urls)
        assertNull(playback.state.value.playlist)
        assertEquals(QuranAudioState.Idle, playback.state.value.audioState)
    }

    @Test fun playlistRepeatRestartsWholeQueueAndCanBeDisabled() {
        playback.startPlaylist("list", "Morning", listOf(PlaylistTrack(93, 1), PlaylistTrack(89, 1)))
        playback.togglePlaylistRepeat()
        repeat(2) { player.complete() }
        assertEquals(listOf("93/1", "89/1", "93/1"), player.urls)
        assertEquals(0, playback.state.value.playlist?.currentIndex)
        playback.togglePlaylistRepeat()
        repeat(2) { player.complete() }
        assertNull(playback.state.value.playlist)
    }

    @Test fun skipIgnoresOldCallbacksAndPauseKeepsQueuePosition() {
        playback.startPlaylist("list", "Morning", listOf(PlaylistTrack(93, 2), PlaylistTrack(89, 1)))
        val previous = player.track
        playback.nextPlaylistSurah()
        previous.ended()
        previous.failed(IllegalStateException("stale"))
        assertEquals(1, playback.state.value.playlist?.currentIndex)
        playback.togglePlaylistPlayback()
        assertEquals(QuranAudioState.Paused(89, 1), playback.state.value.audioState)
        playback.togglePlaylistPlayback()
        assertEquals(QuranAudioState.Playing(89, 1), playback.state.value.audioState)
        playback.previousPlaylistSurah()
        assertEquals("93/1", player.urls.last())
    }

    @Test fun repeatMemorizationAndManualAyahTakeOverPlaylistCleanly() {
        playback.startPlaylist("list", "Morning", listOf(PlaylistTrack(93, 2)))
        val old = player.track
        customRange(2, 3, 1)
        assertNull(playback.state.value.playlist)
        old.ended()
        assertEquals("2/2", player.urls.last())
        playback.startPlaylist("list", "Morning", listOf(PlaylistTrack(93, 2)))
        playback.playAyah(89, 1)
        assertNull(playback.state.value.playlist)
        assertFalse(playback.state.value.isRepeatModeActive)
    }

    @Test fun playlistStopsAfterExactlyThreeCompleteLoops() {
        playback.startPlaylist("counted", "Test", listOf(PlaylistTrack(1, 2), PlaylistTrack(2, 1)))
        playback.setPlaylistRepeatCount(3)
        repeat(3) { player.complete() }
        assertEquals(1, playback.state.value.playlist?.completedLoops)
        assertEquals(0, playback.state.value.playlist?.currentIndex)
        repeat(5) { player.complete() }
        assertEquals(2, playback.state.value.playlist?.completedLoops)
        val last = player.track
        player.complete()
        assertNull(playback.state.value.playlist)
        assertEquals(QuranAudioState.Idle, playback.state.value.audioState)
        assertEquals(9, player.urls.size)
        last.ended()
        assertEquals(9, player.urls.size)
    }

    @Test fun customRepeatCountIsValidatedAndResetsLoopBudgetOnlyWhenChanged() {
        playback.startPlaylist("counted", "Test", listOf(PlaylistTrack(1, 1)))
        playback.setPlaylistRepeatCount(7)
        repeat(2) { player.complete() }
        assertEquals(2, playback.state.value.playlist?.completedLoops)
        playback.setPlaylistRepeatCount(0)
        playback.setPlaylistRepeatCount(100)
        assertEquals(7, playback.state.value.playlist?.repeatCount)
        playback.setPlaylistRepeatCount(2)
        assertEquals(0, playback.state.value.playlist?.completedLoops)
        repeat(2) { player.complete() }
        assertNull(playback.state.value.playlist)
    }

    private fun customRange(start: Int, end: Int, count: Int) {
        playback.openRepeatSheet(2, 4)
        playback.setScope(PlaybackScope.CUSTOM_RANGE)
        playback.setStartAyah(start)
        playback.setEndAyah(end)
        playback.setRepeatCount(count)
        playback.startRepeat()
        assertFalse(playback.state.value.isRepeatBottomSheetVisible)
    }

    private class FakePlayer : QuranAudioPlayer {
        data class Track(val ready: () -> Unit, val buffering: () -> Unit,
            val ended: () -> Unit, val failed: (Exception) -> Unit)
        val urls = mutableListOf<String>()
        lateinit var track: Track
        var pauses = 0
        var resumes = 0
        var released = false
        override fun play(url: String, onReady: () -> Unit, onBuffering: () -> Unit,
            onEnded: () -> Unit, onError: (Exception) -> Unit) {
            urls += url
            track = Track(onReady, onBuffering, onEnded, onError)
        }
        fun complete() { track.ended() }
        override fun pause() { pauses++ }
        override fun resume() { resumes++ }
        override fun stop() = Unit
        override fun release() { released = true }
    }
}
