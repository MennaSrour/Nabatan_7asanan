@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.menna.nabatan_7asena.presentation.screens.shorts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.coroutines.delay
import platform.AVFoundation.*
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIView

@Composable
internal actual fun KidsShortPlayer(
    uri: String,
    playing: Boolean,
    muted: Boolean,
    onState: (VideoState) -> Unit,
    modifier: Modifier
) {
    val url = remember(uri) { NSURL.URLWithString(uri) }
    if (url == null) {
        LaunchedEffect(uri) { onState(VideoState.Failed) }
        return
    }
    val player = remember(url) { AVPlayer.playerWithURL(url) as AVPlayer }
    val viewState by rememberUpdatedState(onState)
    val shouldPlay by rememberUpdatedState(playing)

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, player) {
        val notifications = NSNotificationCenter.defaultCenter
        val finished = notifications.addObserverForName(
            AVPlayerItemDidPlayToEndTimeNotification,
            player.currentItem,
            NSOperationQueue.mainQueue
        ) {
            player.seekToTime(kCMTimeZero)
            if (shouldPlay && lifecycle.currentState == Lifecycle.State.RESUMED) player.play()
        }
        val failed = notifications.addObserverForName(
            AVPlayerItemFailedToPlayToEndTimeNotification,
            player.currentItem,
            NSOperationQueue.mainQueue
        ) { viewState(VideoState.Failed) }
        val pauseObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) player.pause()
        }
        lifecycle.addObserver(pauseObserver)
        onDispose {
            lifecycle.removeObserver(pauseObserver)
            notifications.removeObserver(finished)
            notifications.removeObserver(failed)
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
        }
    }

    LaunchedEffect(player) {
        while (player.currentItem?.status != AVPlayerItemStatusFailed &&
            player.currentItem?.status != AVPlayerItemStatusReadyToPlay
        ) {
            delay(100)
        }
        viewState(
            if (player.currentItem?.status == AVPlayerItemStatusReadyToPlay) VideoState.Ready
            else VideoState.Failed
        )
    }

    SideEffect {
        player.muted = muted
        if (playing && lifecycle.currentState == Lifecycle.State.RESUMED) player.play() else player.pause()
    }

    UIKitView(
        factory = { ShortVideoView(player) },
        modifier = modifier,
        onRelease = { it.videoLayer.player = null }
    )
}

private class ShortVideoView(player: AVPlayer) : UIView() {
    val videoLayer = AVPlayerLayer.playerLayerWithPlayer(player).apply {
        videoGravity = AVLayerVideoGravityResizeAspect
    }

    init {
        userInteractionEnabled = false
        layer.addSublayer(videoLayer)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        videoLayer.frame = bounds
    }
}
