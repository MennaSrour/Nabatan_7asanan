package com.menna.nabatan_7asena.presentation.screens.home.composables

import kotlin.test.*

class TreasureProgressTest {
    @Test fun remainingTasksMatchProgressIncludingPrayers() {
        val progress = treasureProgress(6, 10)
        assertEquals(4, progress.remaining)
        assertEquals(0.6f, progress.fraction)
    }

    @Test fun emptyOrInvalidTotalsNeverShowCompletedProgress() {
        listOf(0, -1).forEach { total ->
            val progress = treasureProgress(3, total)
            assertEquals(0, progress.completed)
            assertEquals(0, progress.remaining)
            assertEquals(0f, progress.fraction)
        }
    }

    @Test fun boundsChangedTaskCountsWithoutNegativeRemainingOrOverfullProgress() {
        assertEquals(0f, treasureProgress(-2, 10).fraction)
        val finished = treasureProgress(15, 10)
        assertEquals(10, finished.completed)
        assertEquals(0, finished.remaining)
        assertEquals(1f, finished.fraction)
    }
}
