package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun QuranPlaylistPlayer(queue: PlaylistPlaybackState, audio: QuranAudioState, actions: QuranActions) {
    val track = queue.tracks[queue.currentIndex]
    Surface(color = SummerTheme.quran.drawer, shadowElevation = 6.dp, modifier = Modifier.padding(bottom = FloatingBarClearance)) {
        Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${queue.name} · ${QuranConstants.surahs[track.surahId - 1].name}", color = SummerTheme.quran.accentStrong)
            Text(stringResource(R.string.quran_playlist_progress, arabicIndic(queue.currentIndex + 1), arabicIndic(queue.tracks.size)))
            Row(horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = actions::previousPlaylistSurah) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.quran_previous_surah)) }
                IconButton(onClick = actions::togglePlaylistPlayback) {
                    if (audio is QuranAudioState.Loading) CircularProgressIndicator(Modifier.size(22.dp))
                    else Icon(if (audio is QuranAudioState.Paused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, stringResource(R.string.quran_play_pause))
                }
                IconButton(onClick = actions::nextPlaylistSurah) { Icon(Icons.AutoMirrored.Rounded.ArrowForward, stringResource(R.string.quran_next_surah)) }
                IconToggleButton(checked = queue.repeat, onCheckedChange = { actions.togglePlaylistRepeat() }) {
                    Icon(Icons.Rounded.Repeat, stringResource(R.string.quran_repeat_playlist), tint = if (queue.repeat) SummerTheme.quran.accentStrong else SummerTheme.quran.textMuted)
                }
                IconButton(onClick = actions::stopAudio) { Icon(Icons.Rounded.Stop, genderedStringResource(GenderedStrings.quranStopPlayback)) }
            }
        }
    }
}
