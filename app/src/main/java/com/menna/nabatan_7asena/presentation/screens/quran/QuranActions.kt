package com.menna.nabatan_7asena.presentation.screens.quran

interface QuranActions {
    fun onOpenRepeatBottomSheet(surahId: Int)
    fun onDismissRepeatBottomSheet()
    fun onPlaybackScopeChanged(scope: PlaybackScope)
    fun onStartAyahChanged(ayah: Int)
    fun onEndAyahChanged(ayah: Int)
    fun onRepeatCountChanged(count: Int)
    fun onStartRepeatClicked()
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
