package com.menna.nabatan_7asena.presentation.screens.quran

sealed interface QuranEffect {
    data class ScrollToAyah(val surahIndex: Int, val ayaNo: Int) : QuranEffect
    data object LoadFailed : QuranEffect
    data object BookmarkSaved : QuranEffect
}
