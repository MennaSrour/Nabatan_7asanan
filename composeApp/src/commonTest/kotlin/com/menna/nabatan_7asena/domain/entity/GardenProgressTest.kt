package com.menna.nabatan_7asena.domain.entity

import kotlinx.datetime.LocalDate
import kotlin.test.*

class GardenProgressTest {
    private val day = LocalDate(2026, 11, 21)

    @Test fun completedTaskEarnsOneDropOnlyOnceDespiteUncheckingAndRechecking() {
        val earned = awardGardenTasks(GardenProgress(), day, setOf(1, 2))
        val watered = waterGarden(earned, day)
        assertEquals(1, watered.waterDrops)
        assertEquals(1, watered.xp)
        assertEquals(watered, awardGardenTasks(watered, day, setOf(1)))
        assertEquals(watered, awardGardenTasks(watered, day, setOf(1, 2)))
        assertEquals(2, awardGardenTasks(watered, day, setOf(1, 2, 3)).waterDrops)
    }

    @Test fun nextDayCanEarnAgainButOlderDatesCannotReplayRewards() {
        val first = awardGardenTasks(GardenProgress(), day, setOf(1))
        val tomorrow = LocalDate(2026, 11, 22)
        val second = awardGardenTasks(first, tomorrow, setOf(1))
        assertEquals(2, second.waterDrops)
        assertEquals(setOf(1), second.rewardedTaskIds)
        assertEquals(second, awardGardenTasks(second, day, setOf(2)))
    }

    @Test fun stagesHaveSixDistinctThresholdsAndDoNotResetAtFullGrowth() {
        GardenStageThresholds.forEachIndexed { index, threshold ->
            val progress = GardenProgress(xp = threshold)
            assertEquals(index, progress.stage)
            assertEquals(if (index == 5) 1f else 0f, progress.growthFraction)
        }
        assertEquals(5, GardenProgress(xp = 100).stage)
        assertEquals(1f, GardenProgress(xp = 100).growthFraction)
        assertEquals(0.5f, GardenProgress(xp = 5).growthFraction)
    }

    @Test fun missesDoNotRemoveXpOrDropsAndNoWaterMeansNoGrowth() {
        val grown = GardenProgress(xp = 35, waterDrops = 0, lastWateredDate = "2026-11-01")
        assertEquals(grown, awardGardenTasks(grown, day, emptySet()))
        assertEquals(grown, waterGarden(grown, day))
    }

    @Test fun wateringIsOneDropForOneXpWithTheSavedDate() {
        val next = waterGarden(GardenProgress(waterDrops = 3), day)
        assertEquals(2, next.waterDrops)
        assertEquals(1, next.xp)
        assertEquals(day.toString(), next.lastWateredDate)
        assertEquals(1L, next.revision)
    }

    @Test fun malformedSavedStateIsRejectedRatherThanRenderedAsProgress() {
        assertFailsWith<IllegalArgumentException> { GardenProgress(xp = -1).validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(waterDrops = -1).validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(revision = 1, syncedRevision = 2).validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(lastWateredDate = "wrong").validate() }
        assertFailsWith<IllegalArgumentException> { GardenProgress(rewardedTaskIds = setOf(1)).validate() }
    }
}
