package com.menna.nabatan_7asena.presentation.platform

interface ScreenAudioPlayer {
    fun play(resource: Int, onFinished: () -> Unit = {})
    fun stop()
}
