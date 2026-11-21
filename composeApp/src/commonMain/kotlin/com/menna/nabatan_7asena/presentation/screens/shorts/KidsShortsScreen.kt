@file:OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)

package com.menna.nabatan_7asena.presentation.screens.shorts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan.shared.generated.resources.*
import com.menna.nabatan_7asena.data.createHttpClient
import com.menna.nabatan_7asena.data.remote.FirebaseDataSource
import com.menna.nabatan_7asena.data.repository.KidsShortsRepository
import com.menna.nabatan_7asena.domain.entity.KidsShort
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.stringResource

internal const val KIDS_SHORTS_ROUTE = "kids_shorts"

@Composable
internal fun KidsShortsScreen(onBack: () -> Unit) {
    val repository = remember { KidsShortsRepository(FirebaseDataSource()) }
    val clips by produceState<List<KidsShort>?>(null, repository) { value = repository.load() }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow
        .collectAsStateWithLifecycle(minActiveState = Lifecycle.State.CREATED)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            val feed = clips
            if (feed == null) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                KidsShortsFeed(feed, resumed = lifecycleState == Lifecycle.State.RESUMED)
            }
            Row(
                modifier = Modifier.align(Alignment.TopStart)
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)))
                    .fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(Res.string.shorts_back), tint = Color.White)
                }
                Text(stringResource(Res.string.kids_shorts), color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun KidsShortsFeed(clips: List<KidsShort>, resumed: Boolean) {
    val pager = rememberPagerState { clips.size }
    var muted by rememberSaveable { mutableStateOf(true) }

    VerticalPager(
        state = pager,
        key = { clips[it].id },
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val active = page == pager.settledPage
        KidsShortPage(
            clip = clips[page],
            active = active,
            canPlay = resumed && !pager.isScrollInProgress,
            muted = muted,
            onToggleMute = { muted = !muted }
        )
    }
}

@Composable
private fun KidsShortPage(
    clip: KidsShort,
    active: Boolean,
    canPlay: Boolean,
    muted: Boolean,
    onToggleMute: () -> Unit
) {
    var paused by rememberSaveable(clip.id) { mutableStateOf(false) }
    var liked by rememberSaveable(clip.id) { mutableStateOf(false) }
    var attempt by remember(clip.id) { mutableIntStateOf(0) }
    var videoState by remember(clip.id, active, attempt) { mutableStateOf(VideoState.Loading) }

    Box(Modifier.fillMaxSize()) {
        if (active) {
            key(clip.videoUrl, attempt) {
                KidsShortPlayer(
                    uri = if (clip.bundled) Res.getUri(clip.videoUrl) else clip.videoUrl,
                    playing = canPlay && !paused,
                    muted = muted,
                    onState = { videoState = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        if (videoState != VideoState.Ready) ShortThumbnail(clip)
        Box(
            Modifier.fillMaxSize().clickable(
                role = Role.Button,
                onClickLabel = stringResource(if (paused) Res.string.shorts_play else Res.string.shorts_pause)
            ) { paused = !paused }
        )
        when (videoState) {
            VideoState.Loading -> if (active && !paused) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color.White)
            }
            VideoState.Ready -> if (paused) {
                Icon(
                    Icons.Rounded.PlayArrow, null,
                    Modifier.align(Alignment.Center).size(72.dp), tint = Color.White
                )
            }
            VideoState.Failed -> Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(Res.string.shorts_playback_error))
                    TextButton(onClick = { paused = false; attempt++ }) {
                        Text(stringResource(Res.string.shorts_retry))
                    }
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))))
                .navigationBarsPadding().padding(start = 20.dp, end = 12.dp, top = 36.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f).padding(end = 8.dp)) {
                Text(clip.category, color = Color(0xFFFFEAB6), style = MaterialTheme.typography.labelLarge)
                Text(clip.title, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }
            IconButton(onClick = onToggleMute) {
                Icon(
                    if (muted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                    stringResource(if (muted) Res.string.shorts_unmute else Res.string.shorts_mute),
                    tint = Color.White
                )
            }
            IconToggleButton(checked = liked, onCheckedChange = { liked = it }) {
                Icon(
                    if (liked) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    stringResource(if (liked) Res.string.shorts_unlike else Res.string.shorts_like),
                    tint = Color(0xFFFFD66D)
                )
            }
        }
    }
}

@Composable
private fun ShortThumbnail(clip: KidsShort) {
    val bitmap by produceState<ImageBitmap?>(null, clip.thumbnailUrl, clip.bundled) {
        value = try {
            val bytes = if (clip.bundled) {
                Res.readBytes(clip.thumbnailUrl)
            } else {
                val client = createHttpClient()
                try {
                    client.get(clip.thumbnailUrl).body<ByteArray>()
                } finally {
                    client.close()
                }
            }
            bytes.decodeToImageBitmap()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            null
        }
    }
    bitmap?.let {
        Image(it, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}
