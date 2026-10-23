package com.menna.nabatan_7asena.presentation.screens.quran

interface QuranActions {
    fun onSurahChanged(surahId: Int)
    fun goToJuz(juz: Int)
    fun goToBookmark()
    fun downloadSurah(suraId: Int)
    fun downloadFullQuran()
    fun stopAudio()
    fun playAyahAudio(suraNo: Int, ayaNo: Int)
    fun saveBookmark(surahId: Int, ayahNo: Int)
    fun onSurahVisible(surahId: Int)
}
