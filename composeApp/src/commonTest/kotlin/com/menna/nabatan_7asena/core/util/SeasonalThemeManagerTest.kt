package com.menna.nabatan_7asena.core.util

import com.menna.nabatan_7asena.core.config.SeasonalThemeOverride
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SeasonalThemeManagerTest {
    @Test
    fun automaticModesFollowHijriBoundaries() {
        val dates = listOf(
            Triple(8, 29, SeasonalMode.DEFAULT),
            Triple(9, 1, SeasonalMode.RAMADAN),
            Triple(9, 30, SeasonalMode.RAMADAN),
            Triple(10, 1, SeasonalMode.EID),
            Triple(10, 3, SeasonalMode.EID),
            Triple(10, 4, SeasonalMode.DEFAULT),
            Triple(12, 9, SeasonalMode.DEFAULT),
            Triple(12, 10, SeasonalMode.EID),
            Triple(12, 13, SeasonalMode.EID),
            Triple(12, 14, SeasonalMode.DEFAULT)
        )
        dates.forEach { (month, day, expected) ->
            assertEquals(expected, SeasonalThemeManager.mode(HijriDate(day, month, 1448), SeasonalThemeOverride.AUTO))
        }
        assertEquals(SeasonalMode.DEFAULT, SeasonalThemeManager.mode(null, SeasonalThemeOverride.AUTO))
    }

    @Test
    fun forceModesWorkOutsideTheirSeasons() {
        val ramadan = HijriDate(12, 9, 1448)
        assertEquals(RamadanSeason(1448, 12), SeasonalThemeManager.ramadan(ramadan, SeasonalThemeOverride.AUTO))
        assertEquals(RamadanSeason(0, 30, true), SeasonalThemeManager.ramadan(null, SeasonalThemeOverride.FORCE_RAMADAN))
        assertEquals(SeasonalMode.EID, SeasonalThemeManager.mode(ramadan, SeasonalThemeOverride.FORCE_EID))
        assertEquals(SeasonalMode.DEFAULT, SeasonalThemeManager.mode(ramadan, SeasonalThemeOverride.NONE))
        assertNull(SeasonalThemeManager.ramadan(ramadan, SeasonalThemeOverride.NONE))
    }
}
