package com.menna.nabatan_7asena.domain.entity

import kotlinx.datetime.LocalDate
import kotlin.test.*

class QuranJourneyTest {
    private val today = LocalDate(2026, 11, 22)
    private val tomorrow = LocalDate(2026, 11, 23)

    @Test
    fun dailyGoalRoundsUpAndCapsTheFinalRange() {
        assertEquals(listOf(604, 302, 121, 61, 31, 16), WirdGoal.entries.map { it.estimatedDays })
        val progress = QuranJourney(activeKhatmah = Khatmah(WirdGoal.QUARTER, pagesRead = 600))
        assertEquals(Wird(601, 604), progress.wirdFor(today))
        val completed = progress.completeWird(today, Wird(601, 604))
        assertNull(completed.activeKhatmah)
        assertEquals(1, completed.completedKhatmahs)
        assertTrue(completed.celebrationPending)
        assertEquals(Wird(601, 604), completed.wirdFor(today))
        assertNull(completed.wirdFor(tomorrow))
        assertEquals(completed, completed.completeWird(today, Wird(601, 604)))
    }

    @Test
    fun plannerRejectsDuplicateStaleAndFutureRangeTaps() {
        val initial = QuranJourney(activeKhatmah = Khatmah(WirdGoal.QUARTER))
        val first = Wird(1, 5)
        val second = Wird(6, 10)
        assertEquals(initial, initial.completeWird(today, second))
        val completed = initial.completeWird(today, first)
        assertEquals(first, completed.wirdFor(today))
        assertEquals(second, completed.wirdFor(tomorrow))
        assertEquals(completed, completed.completeWird(today, first))
        assertEquals(completed, completed.completeWird(today, second))
        assertEquals(completed, completed.completeWird(tomorrow, first))
        assertEquals(10, completed.completeWird(tomorrow, second).activeKhatmah?.pagesRead)
    }

    @Test
    fun memorizationCanBeEditedWithoutResettingFirstTimeReward() {
        val inProgress = QuranJourney().updateSurah(114, MemorizationStatus.IN_PROGRESS, 3, 6)
        assertEquals(3, inProgress.memorization.getValue(114).ayahs)
        val memorized = inProgress.updateSurah(114, MemorizationStatus.MEMORIZED, 3, 6)
        assertEquals(1, memorized.memorizedSurahs)
        assertEquals(SurahMemorization(MemorizationStatus.MEMORIZED, 6, true), memorized.memorization[114])
        val reset = memorized.updateSurah(114, MemorizationStatus.NOT_STARTED, 6, 6)
        assertEquals(0, reset.memorizedSurahs)
        assertEquals(SurahMemorization(bonusAwarded = true), reset.memorization[114])
        assertEquals(memorized, reset.updateSurah(114, MemorizationStatus.MEMORIZED, 0, 6))
    }
}
