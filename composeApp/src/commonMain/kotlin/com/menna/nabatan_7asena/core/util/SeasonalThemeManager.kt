package com.menna.nabatan_7asena.core.util

import com.menna.nabatan_7asena.core.config.SeasonalThemeConfig
import com.menna.nabatan_7asena.core.config.SeasonalThemeOverride

enum class SeasonalMode { DEFAULT, RAMADAN, EID }

data class HijriDate(val day: Int, val month: Int, val year: Int)

data class RamadanSeason(val year: Int, val unlockedThrough: Int, val isPreview: Boolean = false)

object SeasonalThemeManager {
    fun mode(
        date: HijriDate?,
        override: SeasonalThemeOverride = SeasonalThemeConfig.override
    ): SeasonalMode = when (override) {
        SeasonalThemeOverride.NONE -> SeasonalMode.DEFAULT
        SeasonalThemeOverride.FORCE_RAMADAN -> SeasonalMode.RAMADAN
        SeasonalThemeOverride.FORCE_EID -> SeasonalMode.EID
        SeasonalThemeOverride.AUTO -> when {
            date == null -> SeasonalMode.DEFAULT
            date.month == 9 && date.day in 1..30 -> SeasonalMode.RAMADAN
            date.month == 10 && date.day in 1..3 -> SeasonalMode.EID
            date.month == 12 && date.day in 10..13 -> SeasonalMode.EID
            else -> SeasonalMode.DEFAULT
        }
    }

    fun ramadan(
        date: HijriDate?,
        override: SeasonalThemeOverride = SeasonalThemeConfig.override
    ): RamadanSeason? {
        if (mode(date, override) != SeasonalMode.RAMADAN) return null
        return if (override == SeasonalThemeOverride.FORCE_RAMADAN) RamadanSeason(0, 30, isPreview = true)
        else date?.let { RamadanSeason(it.year, it.day) }
    }
}
