package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedString
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranRepeatBottomSheet(state: QuranUiState, actions: QuranActions) {
    ModalBottomSheet(onDismissRequest = actions::onDismissRepeatBottomSheet,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SummerTheme.quran.card, contentColor = SummerTheme.quran.textPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            .navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(genderedStringResource(GenderedStrings.quranRepeatMemorize),
                style = MaterialTheme.typography.titleLarge, color = SummerTheme.quran.accentStrong)
            RepeatScopeSelector(state.playbackScope, actions::onPlaybackScopeChanged)
            if (state.playbackScope == PlaybackScope.CUSTOM_RANGE) RepeatAyahRange(state, actions)
            RepeatCountSelector(state.repeatCount, actions::onRepeatCountChanged)
            StartRepeatButton(state.repeatAyahCount > 0, actions::onStartRepeatClicked)
        }
    }
}

@Composable
private fun RepeatScopeSelector(selected: PlaybackScope, onSelected: (PlaybackScope) -> Unit) {
    Column {
        RepeatScopeOption(PlaybackScope.FULL_SURAH, GenderedStrings.quranFullSurah, selected, onSelected)
        RepeatScopeOption(PlaybackScope.CUSTOM_RANGE, GenderedStrings.quranCustomRange, selected, onSelected)
    }
}

@Composable
private fun RepeatScopeOption(scope: PlaybackScope, label: GenderedString,
    selected: PlaybackScope, onSelected: (PlaybackScope) -> Unit) {
    Row(Modifier.fillMaxWidth().selectable(selected == scope, role = Role.RadioButton,
        onClick = { onSelected(scope) }).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected == scope, onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = SummerTheme.quran.accentStrong))
        Spacer(Modifier.width(8.dp))
        Text(genderedStringResource(label))
    }
}

@Composable
private fun RepeatAyahRange(state: QuranUiState, actions: QuranActions) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AyahDropdown(GenderedStrings.quranStartAyah, state.startAyah, 1..state.repeatAyahCount,
            actions::onStartAyahChanged, Modifier.weight(1f))
        AyahDropdown(GenderedStrings.quranEndAyah, state.endAyah, state.startAyah..state.repeatAyahCount,
            actions::onEndAyahChanged, Modifier.weight(1f))
    }
}

@Composable
private fun AyahDropdown(label: GenderedString, selected: Int, choices: IntRange,
    onSelected: (Int) -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SummerTheme.quran.accentStrong)) {
            Text(genderedStringResource(label, selected))
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 240.dp), containerColor = SummerTheme.quran.card) {
            choices.forEach { ayah ->
                DropdownMenuItem(text = { Text(genderedStringResource(label, ayah)) },
                    onClick = { onSelected(ayah); expanded = false })
            }
        }
    }
}

@Composable
private fun RepeatCountSelector(selected: Int, onSelected: (Int) -> Unit) {
    Column {
        Text(genderedStringResource(GenderedStrings.quranRepeatCount))
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(repeatCountOptions.size) { index ->
                val count = repeatCountOptions[index]
                FilterChip(selected = count == selected, onClick = { onSelected(count) },
                    label = { Text(repeatCountLabel(count)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SummerTheme.quran.accentSoft,
                        selectedLabelColor = SummerTheme.quran.accentStrong))
            }
        }
    }
}

@Composable
private fun repeatCountLabel(count: Int): String = if (count == INFINITE_REPEATS)
    genderedStringResource(GenderedStrings.quranInfiniteRepeat)
else genderedStringResource(GenderedStrings.quranRepeatTimes, count)

@Composable
private fun StartRepeatButton(enabled: Boolean, onStart: () -> Unit) {
    Button(onClick = onStart, enabled = enabled, modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = SummerTheme.quran.accentStrong,
            contentColor = SummerTheme.quran.card)) {
        Text(genderedStringResource(GenderedStrings.quranStartRepeat))
    }
}
