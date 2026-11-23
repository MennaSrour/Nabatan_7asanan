package com.menna.nabatan_7asena.presentation.screens.shorts

import android.graphics.Matrix
import android.view.TextureView
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer

@Composable
internal actual fun KidsShortPlayer(
    uri: String,
    playing: Boolean,
    muted: Boolean,
    onState: (VideoState) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val reportState by rememberUpdatedState(onState)
    val player = remember(context, uri) { ExoPlayer.Builder(context).build() }
    val texture = remember(context, player) { TextureView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, player, texture) {
        fun fitVideo(size: VideoSize) {
            if (size.width == 0 || size.height == 0 || texture.width == 0 || texture.height == 0) return
            val videoRatio = size.width * size.pixelWidthHeightRatio / size.height
            val viewRatio = texture.width.toFloat() / texture.height
            texture.setTransform(Matrix().apply {
                setScale(
                    (videoRatio / viewRatio).coerceAtMost(1f),
                    (viewRatio / videoRatio).coerceAtMost(1f),
                    texture.width / 2f,
                    texture.height / 2f
                )
            })
        }

        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) = fitVideo(videoSize)
            override fun onRenderedFirstFrame() = reportState(VideoState.Ready)
            override fun onPlayerError(error: PlaybackException) = reportState(VideoState.Failed)
        }
        val layoutListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            fitVideo(player.videoSize)
        }
        texture.addOnLayoutChangeListener(layoutListener)
        player.addListener(listener)
        player.setVideoTextureView(texture)
        player.setAudioAttributes(AudioAttributes.DEFAULT, true)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        val pauseObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) player.pause()
        }
        lifecycle.addObserver(pauseObserver)
        onDispose {
            lifecycle.removeObserver(pauseObserver)
            texture.removeOnLayoutChangeListener(layoutListener)
            player.removeListener(listener)
            player.release()
        }
    }

    SideEffect {
        player.volume = if (muted) 0f else 1f
        player.playWhenReady = playing && lifecycle.currentState == Lifecycle.State.RESUMED
    }

    AndroidView(factory = { texture }, modifier = modifier)
}
