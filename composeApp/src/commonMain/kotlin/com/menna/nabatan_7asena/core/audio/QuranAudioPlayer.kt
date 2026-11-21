package com.menna.nabatan_7asena.core.audio

interface QuranAudioPlayer {
    fun play(url: String, onReady: () -> Unit, onBuffering: () -> Unit, onEnded: () -> Unit, onError: (Exception) -> Unit = {})
    fun pause()
    fun resume()
    fun stop()
    fun release()
}


object EveryAyahUrlBuilder {
    private const val BASE_URL = "https://everyayah.com/data/Husary_128kbps"

    fun buildUrl(suraNo: Int, ayaNo: Int): String {
        val sura = suraNo.toString().padStart(3, '0')
        val aya  = ayaNo.toString().padStart(3, '0')
        return "$BASE_URL/$sura$aya.mp3"
    }
}