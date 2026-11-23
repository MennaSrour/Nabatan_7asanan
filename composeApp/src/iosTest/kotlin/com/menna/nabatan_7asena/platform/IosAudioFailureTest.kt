package com.menna.nabatan_7asena.platform

import kotlin.test.Test
import kotlin.test.assertEquals

class IosAudioFailureTest {
    @Test fun missingResourceReleasesTheWaitingScreenOnceAndStopDoesNotReplayIt() {
        val audio = IosScreenAudio()
        var completions = 0
        audio.play(Int.MIN_VALUE) { completions++ }
        assertEquals(1, completions)
        audio.stop()
        audio.stop()
        assertEquals(1, completions)
    }
}
