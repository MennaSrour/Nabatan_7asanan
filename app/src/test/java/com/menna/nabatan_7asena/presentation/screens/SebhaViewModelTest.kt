package com.menna.nabatan_7asena.presentation.screens

import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import com.menna.nabatan_7asena.presentation.screens.sebha.SebhaViewModel
import org.junit.Assert.*
import org.junit.Test

class SebhaViewModelTest {
    private val audio = FakeAudioPlayer()
    private val viewModel = SebhaViewModel(audio)

    @Test fun reachingTargetCelebratesOnceAndDismissStartsNextCycle() {
        viewModel.startFreeTasbeeh("ذكر", 2)
        viewModel.onTasbeehClick()
        assertFalse(viewModel.state.value.showCelebration)
        viewModel.onTasbeehClick()
        assertTrue(viewModel.state.value.showCelebration)
        assertEquals(1, viewModel.state.value.totalCycles)
        assertEquals(listOf(R.raw.good), audio.played)
        viewModel.onTasbeehClick()
        assertFalse(viewModel.state.value.showCelebration)
        assertEquals(0, viewModel.state.value.currentCount)
        assertEquals(listOf(R.raw.good), audio.played)
    }

    @Test fun unlimitedTasbeehDoesNotTriggerTargetCelebration() {
        viewModel.startFreeTasbeeh("ذكر", null)
        repeat(200) { viewModel.onTasbeehClick() }
        assertEquals(200, viewModel.state.value.currentCount)
        assertEquals(0, viewModel.state.value.totalCycles)
        assertFalse(viewModel.state.value.showCelebration)
        assertTrue(audio.played.isEmpty())
    }

    @Test fun completionAndLeavingScreenClearPlaybackState() {
        viewModel.selectZikr(viewModel.state.value.azkarList.first())
        viewModel.toggleAudio()
        assertTrue(viewModel.state.value.isPlayingAudio)
        audio.finish()
        assertFalse(viewModel.state.value.isPlayingAudio)
        viewModel.toggleAudio()
        viewModel.onScreenLeft()
        assertFalse(viewModel.state.value.isPlayingAudio)
        assertTrue(audio.stopped)
    }

    private class FakeAudioPlayer : ScreenAudioPlayer {
        val played = mutableListOf<Int>()
        var stopped = false
        var finish: () -> Unit = {}
        override fun play(resource: Int, onFinished: () -> Unit) {
            played += resource
            stopped = false
            finish = onFinished
        }
        override fun stop() { stopped = true }
    }
}
