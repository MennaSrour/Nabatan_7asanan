package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource

@Composable
internal fun QuranPlaylistRepeatControl(queue: PlaylistPlaybackState, actions: QuranActions) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text(if (queue.repeat) "∞" else "${queue.repeatCount}×") }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            listOf(1, 2, 3, 5, INFINITE_REPEATS).forEach { count ->
                DropdownMenuItem(text = { Text(if (count == INFINITE_REPEATS) "∞" else "$count×") },
                    onClick = { actions.setPlaylistRepeatCount(count); expanded = false })
            }
            Row(Modifier.padding(horizontal = 8.dp)) {
                TextButton(onClick = { actions.setPlaylistRepeatCount((queue.repeatCount - 1).coerceAtLeast(1)) }, enabled = queue.repeatCount > 1) {
                    Text(stringResource(R.string.playlist_repeat_less))
                }
                TextButton(onClick = { actions.setPlaylistRepeatCount((queue.repeatCount.coerceAtLeast(0) + 1).coerceAtMost(99)) }, enabled = queue.repeatCount < 99) {
                    Text(stringResource(R.string.playlist_repeat_more))
                }
            }
        }
    }
}
