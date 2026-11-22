package com.menna.nabatan_7asena.presentation.screens.quran

sealed interface QuranEffect {
    data object PlaybackFailed : QuranEffect
    data class ScrollToAyah(val surahIndex: Int, val ayaNo: Int, val page: Int = 1) : QuranEffect
    data object JourneySaveFailed : QuranEffect
    data object LibrarySaveFailed : QuranEffect
    data object LoadFailed : QuranEffect
    data object BookmarkSaved : QuranEffect
}
