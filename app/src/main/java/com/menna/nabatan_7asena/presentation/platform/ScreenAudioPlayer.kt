package com.menna.nabatan_7asena.presentation.platform

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ScreenAudioPlayer {
    fun play(@RawRes resource: Int, onFinished: () -> Unit = {})
    fun stop()
}

/** One unscoped player per screen owner; release it when the owner leaves. */
internal class AndroidScreenAudioPlayer @Inject constructor(@ApplicationContext private val context: Context) : ScreenAudioPlayer {
    private var player: MediaPlayer? = null

    override fun play(@RawRes resource: Int, onFinished: () -> Unit) {
        stop()
        try {
            val current = MediaPlayer.create(context, resource)
            if (current == null) { onFinished(); return }
            player = current
            current.setOnCompletionListener { stop(); onFinished() }
            current.setOnErrorListener { _, _, _ -> stop(); onFinished(); true }
            current.start()
        } catch (_: Exception) {
            stop()
            onFinished()
        }
    }

    override fun stop() {
        player?.release()
        player = null
    }
}
