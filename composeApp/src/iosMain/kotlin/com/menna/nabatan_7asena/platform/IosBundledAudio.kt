@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, org.jetbrains.compose.resources.ExperimentalResourceApi::class)
package com.menna.nabatan_7asena.platform

import com.menna.nabatan.shared.generated.resources.Res
import com.menna.nabatan_7asena.core.audio.AudioPlayerManager
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.ResourceCatalog
import platform.AVFAudio.*
import platform.Foundation.*
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.darwin.NSObjectProtocol
import platform.darwin.NSObject

/** Retains the weak AVAudioPlayer delegate; stop/replacement never invokes stale callbacks. */
internal class IosScreenAudio : ScreenAudioPlayer {
    private var player: AVAudioPlayer? = null
    private var finished: (() -> Unit)? = null
    private val observers = mutableListOf<NSObjectProtocol>()
    private val delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            if (player == this@IosScreenAudio.player) finish()
        }
        override fun audioPlayerDecodeErrorDidOccur(player: AVAudioPlayer, error: NSError?) {
            if (player == this@IosScreenAudio.player) {
                platformLog("WARN", "Audio", "Bundled audio decode failed")
                finish()
            }
        }
    }
    override fun play(resource: Int, onFinished: () -> Unit) {
        stop()
        finished = onFinished
        try {
            val path = ResourceCatalog.rawPaths[resource] ?: error("Unknown audio resource")
            val url = requireNotNull(NSURL.URLWithString(Res.getUri(path)))
            val audio = AVAudioPlayer(contentsOfURL = url, error = null)
            audio.delegate = delegate
            player = audio
            listOf(AVAudioSessionInterruptionNotification, UIApplicationWillResignActiveNotification).forEach { name ->
                observers += NSNotificationCenter.defaultCenter.addObserverForName(name, null, NSOperationQueue.mainQueue) { finish() }
            }
            if (!audio.prepareToPlay() || !audio.play()) finish()
        } catch (_: Exception) {
            platformLog("WARN", "Audio", "Bundled audio is unavailable")
            finish()
        }
    }
    private fun clearObservers() {
        observers.forEach { NSNotificationCenter.defaultCenter.removeObserver(it) }
        observers.clear()
    }
    private fun finish() {
        clearObservers()
        val callback = finished
        finished = null
        player?.delegate = null
        player?.stop()
        player = null
        callback?.invoke()
    }
    override fun stop() {
        clearObservers()
        finished = null
        player?.delegate = null
        player?.stop()
        player = null
    }
}

internal class IosAudio : AudioPlayerManager {
    private val player = IosScreenAudio()
    override fun playTaskSound(item: HomeUiState.UiTaskItem, onCompletion: () -> Unit) {
        val resource = when (item.category) {
            TaskCategory.PRAYER -> when (item.title) {
                "الفجر" -> R.raw.fajr
                "الظهر" -> R.raw.zuhr
                "العصر" -> R.raw.asr
                "المغرب" -> R.raw.sound_normal
                "العشاء" -> R.raw.ishaa
                else -> null
            }
            TaskCategory.QURAN -> R.raw.werd
            TaskCategory.AZKAR -> R.raw.yala_zekr
            else -> null
        }
        if (resource == null) onCompletion() else player.play(resource, onCompletion)
    }
    override fun playWelcomeSound(onCompletion: () -> Unit) = player.play(R.raw.welcome_kids, onCompletion)
    override fun playCelebrationSound(onCompletion: () -> Unit) = player.play(R.raw.big_star_voice, onCompletion)
    override fun playCorrectAnswerSound(onCompletion: () -> Unit) = player.play(R.raw.good, onCompletion)
    override fun playAlarmSound(soundResId: Int, onCompletion: () -> Unit) = player.play(soundResId, onCompletion)
    override fun playDuaOrZekrSound(soundResId: Int, onCompletion: () -> Unit) = player.play(soundResId, onCompletion)
    override fun stopAnyPlayingSound() = player.stop()
}
