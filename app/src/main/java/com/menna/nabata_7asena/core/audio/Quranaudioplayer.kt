package com.menna.nabata_7asena.core.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مشغّل صوت القرآن — Wrapper حول ExoPlayer لتدفق الآيات من EveryAyah API
 *
 * ليه interface؟
 *   → سهّل الـ testing وعدم ربط الـ ViewModel بـ ExoPlayer مباشرة
 */
interface QuranAudioPlayer {
    fun play(url: String, onReady: () -> Unit, onBuffering: () -> Unit, onEnded: () -> Unit)
    fun pause()
    fun resume()
    fun stop()
    fun release()
}

@Singleton
class QuranAudioPlayerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : QuranAudioPlayer {

    private var player: ExoPlayer? = null

    override fun play(
        url: String,
        onReady: () -> Unit,
        onBuffering: () -> Unit,
        onEnded: () -> Unit
    ) {
        release()

        val exoPlayer = ExoPlayer.Builder(context).build()
        player = exoPlayer

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> onBuffering()
                    Player.STATE_READY     -> onReady()
                    Player.STATE_ENDED     -> onEnded()
                    else -> Unit
                }
            }
        })

        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.play()
    }

    override fun stop() {
        player?.stop()
    }

    override fun release() {
        try {
            player?.stop()
            player?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            player = null
        }
    }
}

object EveryAyahUrlBuilder {
    private const val BASE_URL = "https://everyayah.com/data/Husary_128kbps"

    fun buildUrl(suraNo: Int, ayaNo: Int): String {
        val sura = suraNo.toString().padStart(3, '0')
        val aya  = ayaNo.toString().padStart(3, '0')
        return "$BASE_URL/$sura$aya.mp3"
    }
}