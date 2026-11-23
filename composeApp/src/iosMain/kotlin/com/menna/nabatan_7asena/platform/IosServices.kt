@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.platform

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.menna.nabatan_7asena.data.local.room.AppDatabase
import com.menna.nabatan_7asena.data.local.room.MIGRATION_5_6
import com.menna.nabatan_7asena.data.local.room.MIGRATION_6_7
import com.menna.nabatan_7asena.core.audio.*
import com.menna.nabatan_7asena.core.alarm.GlobalAlarmManager
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.*

val iosServices: SharedServices by lazy {
    val directory = NSFileManager.defaultManager.URLForDirectory(NSDocumentDirectory, NSUserDomainMask, null, true, null)
        ?: error("Documents directory is unavailable")
    val database = Room.databaseBuilder<AppDatabase>(name = directory.path + "/nabata_db")
        .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
        .setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.Default).build()
    SharedServices(database.appDao(), IosLocationAccess, IosAudio(), IosQuranAudio(), IosDownloads(),
        IosAlarms(), IosPermissions, IosScheduling(), { IosScreenAudio() })
}

// Quran streaming/downloads and local alarm adapters remain release blockers on iOS.
private class IosScheduling : SchedulingPlatform {
    override fun enableDailyAutoScheduling(replace: Boolean) = Unit
    override fun scheduleImmediately() = Unit
    override fun cancelDailyAutoScheduling() = Unit
}
private class IosAlarms : GlobalAlarmManager {
    override fun cancelPrayerAlarms() = Unit
    override fun cancelAzkarAlarms() = Unit
    override fun cancelQuranAlarms() = Unit
    override fun cancelAllAlarms() = Unit
}
private class IosQuranAudio : QuranAudioPlayer {
    override fun play(url: String, onReady: () -> Unit, onBuffering: () -> Unit, onEnded: () -> Unit, onError: (Exception) -> Unit) {
        onError(UnsupportedOperationException("Quran audio is not implemented on iOS"))
    }
    override fun pause() = Unit
    override fun resume() = Unit
    override fun stop() = Unit
    override fun release() = Unit
}
private class IosDownloads : QuranAudioDownloadManager {
    override fun localUriIfExists(suraNo: Int, ayaNo: Int): String? = null
    override fun isAyahDownloaded(suraNo: Int, ayaNo: Int) = false
    override fun isSurahDownloaded(suraNo: Int, ayahsCount: Int) = false
    override fun downloadedAyahsCount(suraNo: Int, ayahsCount: Int) = 0
    override fun downloadSurah(suraNo: Int, ayahsCount: Int) = flowOf(SurahDownloadProgress.Failed(1, "Audio downloads are not implemented on iOS"))
    override fun deleteSurah(suraNo: Int, ayahsCount: Int) = Unit
}
