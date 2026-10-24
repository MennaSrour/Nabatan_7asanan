package com.menna.nabatan_7asena.presentation.screens.quran

enum class PlaybackScope { FULL_SURAH, CUSTOM_RANGE }

/** Negative sentinel keeps infinite playback distinct from an exhausted finite repeat. */
const val INFINITE_REPEATS = -1
internal val repeatCountOptions = listOf(1, 3, 5, INFINITE_REPEATS)
