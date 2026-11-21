package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

import com.menna.nabatan_7asena.domain.entity.QuranPlaylist
import kotlinx.coroutines.launch

@Composable
fun QuranPlaylistsIndex(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    var naming by remember { mutableStateOf(false) }
    val selected = state.playlists.firstOrNull { it.id == state.editingPlaylistId }
    if (selected != null) PlaylistEditor(selected, actions) {
        actions.startPlaylist(selected.id)
        layout.scope.launch { layout.drawerState.close() }
    } else Column(Modifier.fillMaxSize().padding(10.dp)) {
        Button(onClick = { naming = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Add, null); Text(stringResource(R.string.quran_new_playlist))
        }
        if (state.playlists.isEmpty()) Text(stringResource(R.string.quran_playlists_empty), Modifier.padding(12.dp))
        LazyColumn {
            items(state.playlists, key = { it.id }) { playlist ->
                PlaylistLibraryRow(playlist, { actions.editPlaylist(playlist.id) }) {
                    actions.startPlaylist(playlist.id)
                    layout.scope.launch { layout.drawerState.close() }
                }
            }
        }
    }
    if (naming) PlaylistNameDialog("", { naming = false }) { actions.createPlaylist(it); naming = false }
}

@Composable
private fun PlaylistLibraryRow(playlist: QuranPlaylist, onEdit: () -> Unit, onPlay: () -> Unit) {
    Surface(onClick = onEdit, color = SummerTheme.quran.card, shape = RoundedCornerShape(14.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(playlist.name, Modifier.weight(1f), color = SummerTheme.quran.accentStrong)
            IconButton(onClick = onPlay, enabled = playlist.surahIds.isNotEmpty()) {
                Icon(Icons.Rounded.PlayArrow, stringResource(R.string.quran_play_playlist))
            }
        }
    }
}

@Composable
private fun PlaylistEditor(playlist: QuranPlaylist, actions: QuranActions, onPlay: () -> Unit) {
    var rename by remember(playlist.id) { mutableStateOf(false) }
    var adding by remember(playlist.id) { mutableStateOf(false) }
    var deleting by remember(playlist.id) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        TextButton(onClick = { actions.editPlaylist(null) }) { Text(stringResource(R.string.quran_back_playlists)) }
        Text(playlist.name, color = SummerTheme.quran.accentStrong, fontWeight = FontWeight.Bold)
        Row {
            IconButton(onClick = { rename = true }) { Icon(Icons.Rounded.Edit, stringResource(R.string.quran_rename)) }
            IconButton(onClick = { deleting = true }) { Icon(Icons.Rounded.Delete, stringResource(R.string.quran_delete)) }
            IconButton(onClick = { adding = true }) { Icon(Icons.Rounded.Add, stringResource(R.string.quran_add_surah)) }
            IconButton(onClick = onPlay, enabled = playlist.surahIds.isNotEmpty()) { Icon(Icons.Rounded.PlayArrow, stringResource(R.string.quran_play_playlist)) }
        }
        if (playlist.surahIds.isEmpty()) Text(stringResource(R.string.quran_playlist_empty), Modifier.padding(8.dp))
        PlaylistSurahList(playlist, actions, Modifier.weight(1f))
    }
    if (rename) PlaylistNameDialog(playlist.name, { rename = false }) { actions.renamePlaylist(playlist.id, it); rename = false }
    if (adding) PlaylistAddSurahs(playlist, actions) { adding = false }
    if (deleting) PlaylistDeleteDialog({ deleting = false }) { actions.deletePlaylist(playlist.id); deleting = false }
}

@Composable
private fun PlaylistSurahList(playlist: QuranPlaylist, actions: QuranActions, modifier: Modifier) {
    LazyColumn(modifier) {
        itemsIndexed(playlist.surahIds, key = { _, id -> id }) { index, surahId ->
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text("${arabicIndic(index + 1)} · ${QuranConstants.surahs[surahId - 1].name}")
                Row {
                    IconButton(onClick = { actions.movePlaylistSurah(playlist.id, index, index - 1) }, enabled = index > 0) {
                        Icon(Icons.Rounded.KeyboardArrowUp, stringResource(R.string.quran_move_up))
                    }
                    IconButton(onClick = { actions.movePlaylistSurah(playlist.id, index, index + 1) }, enabled = index < playlist.surahIds.lastIndex) {
                        Icon(Icons.Rounded.KeyboardArrowDown, stringResource(R.string.quran_move_down))
                    }
                    IconButton(onClick = { actions.removePlaylistSurah(playlist.id, surahId) }) {
                        Icon(Icons.Rounded.RemoveCircleOutline, stringResource(R.string.quran_remove_surah))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistAddSurahs(playlist: QuranPlaylist, actions: QuranActions, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.quran_add_surah)) },
        text = {
            LazyColumn(Modifier.heightIn(max = 400.dp)) {
                items(QuranConstants.surahs, key = { it.id }) { surah ->
                    Row(Modifier.fillMaxWidth().clickable {
                        if (surah.id in playlist.surahIds) actions.removePlaylistSurah(playlist.id, surah.id)
                        else actions.addPlaylistSurah(playlist.id, surah.id)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = surah.id in playlist.surahIds, onCheckedChange = null)
                        Text(surah.name)
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.quran_save)) } })
}

@Composable
private fun PlaylistNameDialog(initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember(initial) { mutableStateOf(initial) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.quran_playlist_name)) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true,
            label = { Text(stringResource(R.string.quran_playlist_name)) }) },
        confirmButton = { TextButton(onClick = { onSave(name.trim()) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.quran_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.quran_cancel)) } })
}

@Composable
private fun PlaylistDeleteDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, text = { Text(stringResource(R.string.quran_delete_confirm)) },
        confirmButton = { TextButton(onClick = onDelete) { Text(stringResource(R.string.quran_delete)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.quran_cancel)) } })
}
