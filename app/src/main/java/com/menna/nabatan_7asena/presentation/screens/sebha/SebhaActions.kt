package com.menna.nabatan_7asena.presentation.screens.sebha

interface SebhaActions {
    fun selectZikr(zikr: ZikrItem)
    fun backToMenu()
    fun onTasbeehClick()
    fun resetCounter()
    fun toggleAudio()
    fun dismissCelebration()
    fun startFreeTasbeeh(text: String, targetCount: Int?)
}
