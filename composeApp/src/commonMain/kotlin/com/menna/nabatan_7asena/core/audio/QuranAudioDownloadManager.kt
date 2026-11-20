package com.menna.nabatan_7asena.core.audio

import kotlinx.coroutines.flow.Flow

sealed class SurahDownloadProgress {
    data class InProgress(val downloaded: Int, val total: Int) : SurahDownloadProgress()
    data object Completed : SurahDownloadProgress()
    data class Failed(val ayaNo: Int, val message: String) : SurahDownloadProgress()
}


interface QuranAudioDownloadManager {
    fun localUriIfExists(suraNo: Int, ayaNo: Int): String?
    fun isAyahDownloaded(suraNo: Int, ayaNo: Int): Boolean
    fun isSurahDownloaded(suraNo: Int, ayahsCount: Int): Boolean
    fun downloadedAyahsCount(suraNo: Int, ayahsCount: Int): Int
    fun downloadSurah(suraNo: Int, ayahsCount: Int): Flow<SurahDownloadProgress>
    fun deleteSurah(suraNo: Int, ayahsCount: Int)
}
