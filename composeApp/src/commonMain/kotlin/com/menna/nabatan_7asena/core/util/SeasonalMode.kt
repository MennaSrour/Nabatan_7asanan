package com.menna.nabatan_7asena.core.util

enum class SeasonalMode { DEFAULT, RAMADAN, EID }

data class RamadanSeason(val year: Int, val unlockedThrough: Int, val isPreview: Boolean = false)
