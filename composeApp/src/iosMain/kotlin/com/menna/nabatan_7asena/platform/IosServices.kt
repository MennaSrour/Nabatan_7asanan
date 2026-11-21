@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.menna.nabatan_7asena.platform

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.menna.nabatan_7asena.data.local.room.AppDatabase
import com.menna.nabatan_7asena.data.local.room.MIGRATION_5_6
import com.menna.nabatan_7asena.domain.LocationTracker
import com.menna.nabatan_7asena.domain.entity.UserLocation
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
        .addMigrations(MIGRATION_5_6)
        .setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.Default).build()
    SharedServices(database.appDao(), IosLocation(), IosAudio(), IosQuranAudio(), IosDownloads(),
        IosAlarms(), IosPermissions(), IosScheduling(), { IosScreenAudio() })
}

// Explicit unsupported-capability adapters: no fake downloads, playback completion, or permission grants.
private class IosLocation : LocationTracker {
    override suspend fun getCurrentLocation(): UserLocation? = null
}
private class IosPermissions : PermissionPlatform {
    override val supportsAndroidPermissions = false
    override fun locationGranted() = false
    override fun notificationsGranted() = false
    override fun batteryExempt() = false
}
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
private class IosScreenAudio : ScreenAudioPlayer {
    override fun play(resource: Int, onFinished: () -> Unit) { onFinished() }
    override fun stop() = Unit
}
private class IosAudio : AudioPlayerManager {
    override fun playTaskSound(item: HomeUiState.UiTaskItem, onCompletion: () -> Unit) { onCompletion() }
    override fun playWelcomeSound(onCompletion: () -> Unit) { onCompletion() }
    override fun playCelebrationSound(onCompletion: () -> Unit) { onCompletion() }
    override fun playCorrectAnswerSound(onCompletion: () -> Unit) { onCompletion() }
    override fun playAlarmSound(soundResId: Int, onCompletion: () -> Unit) { onCompletion() }
    override fun playDuaOrZekrSound(soundResId: Int, onCompletion: () -> Unit) { onCompletion() }
    override fun stopAnyPlayingSound() = Unit
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
