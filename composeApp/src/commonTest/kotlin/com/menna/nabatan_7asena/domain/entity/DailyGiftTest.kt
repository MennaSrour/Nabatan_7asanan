package com.menna.nabatan_7asena.domain.entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DailyGiftTest {
    @Test
    fun giftRequiresEveryPrayerAndExtraTask() {
        val extra = 501
        assertEquals(8, missingDailyGiftTasks(listOf(extra), emptySet()))
        assertEquals(2, missingDailyGiftTasks(listOf(extra), dailyGiftRequiredTaskIds - 104))
        assertEquals(0, missingDailyGiftTasks(listOf(extra), dailyGiftRequiredTaskIds + extra))
    }

    @Test
    fun wheelOffersAllFiveGamesOnceAndKeepsLegacyResultsReadable() {
        assertEquals(KidsGame.entries.toSet(), WheelPrize.segments.mapNotNull { it.game }.toSet())
        assertEquals(5, WheelPrize.segments.count { it.game != null })
        assertEquals(WheelPrize.MEMORY, WheelPrize.fromStoredCode("BONUS_ROUND"))
        assertEquals(WheelPrize.STARS_50, WheelPrize.fromStoredCode("STARS_50"))
        assertTrue(WheelPrize.RIDDLE in WheelPrize.segments)
    }
}
