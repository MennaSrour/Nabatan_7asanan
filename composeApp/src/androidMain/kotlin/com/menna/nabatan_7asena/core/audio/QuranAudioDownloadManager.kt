package com.menna.nabatan_7asena.core.audio

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidQuranAudioDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) : QuranAudioDownloadManager {
    private val reciterDirName = "husary_128kbps"

    private val rootDir: File by lazy {
        File(context.filesDir, "quran_audio/$reciterDirName").apply { mkdirs() }
    }

    private fun fileFor(suraNo: Int, ayaNo: Int): File {
        val sura = suraNo.toString().padStart(3, '0')
        val aya = ayaNo.toString().padStart(3, '0')
        return File(rootDir, "$sura$aya.mp3")
    }
    override fun localUriIfExists(suraNo: Int, ayaNo: Int): String? {
        val file = fileFor(suraNo, ayaNo)
        return if (file.exists() && file.length() > 0) Uri.fromFile(file).toString() else null
    }

    override fun isAyahDownloaded(suraNo: Int, ayaNo: Int): Boolean =
        localUriIfExists(suraNo, ayaNo) != null

    override fun isSurahDownloaded(suraNo: Int, ayahsCount: Int): Boolean =
        ayahsCount > 0 && (1..ayahsCount).all { isAyahDownloaded(suraNo, it) }

    override fun downloadedAyahsCount(suraNo: Int, ayahsCount: Int): Int =
        (1..ayahsCount).count { isAyahDownloaded(suraNo, it) }

    private suspend fun downloadOne(suraNo: Int, ayaNo: Int) = withContext(Dispatchers.IO) {
        val target = fileFor(suraNo, ayaNo)
        if (target.exists() && target.length() > 0) return@withContext

        val url = EveryAyahUrlBuilder.buildUrl(suraNo, ayaNo)
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 20_000
        }
        try {
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("HTTP ${connection.responseCode}")
            }
            val temp = File(rootDir, "${target.name}.part")
            connection.inputStream.use { input ->
                temp.outputStream().use { output -> input.copyTo(output) }
            }
            if (temp.length() > 0) {
                temp.renameTo(target)
            } else {
                temp.delete()
                throw IllegalStateException("empty response")
            }
        } finally {
            connection.disconnect()
        }
    }

    override fun downloadSurah(suraNo: Int, ayahsCount: Int): Flow<SurahDownloadProgress> = flow {
        var done = downloadedAyahsCount(suraNo, ayahsCount)
        emit(SurahDownloadProgress.InProgress(done, ayahsCount))
        for (ayaNo in 1..ayahsCount) {
            if (isAyahDownloaded(suraNo, ayaNo)) continue
            try {
                downloadOne(suraNo, ayaNo)
                done++
                emit(SurahDownloadProgress.InProgress(done, ayahsCount))
            } catch (e: Exception) {
                emit(SurahDownloadProgress.Failed(ayaNo, e.message ?: "download error"))
                return@flow
            }
        }
        emit(SurahDownloadProgress.Completed)
    }
    override fun deleteSurah(suraNo: Int, ayahsCount: Int) {
        for (ayaNo in 1..ayahsCount) fileFor(suraNo, ayaNo).delete()
    }
}