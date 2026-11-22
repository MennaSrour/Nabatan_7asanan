package com.menna.nabatan_7asena.presentation.screens.badges

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BadgeShowcaseTest {
    @Test
    fun badgeCountReflectsAllEightExistingUnlockConditions() {
        val stats = BadgesUiState(
            totalStars = 50, currentStreak = 7, quranPartsFinished = 30,
            fajrDays = 3, azkarDays = 3, wateredGarden = true, finishedFriday = true
        )

        assertEquals("٠ من ٨ أوسمة", BadgesUiState().badgeCountLabel())
        assertEquals("٨ من ٨ أوسمة", stats.badgeCountLabel())
        assertEquals("٧ من ٨ أوسمة", stats.copy(finishedFriday = false).badgeCountLabel())
    }

    @Test
    fun lockedMedalShowsPartialProgressAndTheFillCapsAtTheGoal() {
        assertFalse(Badge.Dawn.isUnlocked(BadgesUiState(fajrDays = 2)))
        assertEquals(2f / 3, Badge.Dawn.fraction(BadgesUiState(fajrDays = 2)))
        assertTrue(Badge.Dawn.isUnlocked(BadgesUiState(fajrDays = 3)))
        assertEquals(1f, Badge.Dawn.fraction(BadgesUiState(fajrDays = 10)))
    }
}
