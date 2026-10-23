package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranAudioState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranReaderTopBar(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val q = SummerTheme.quran
    val audioState = state.audioState
    val bookmarkedSurahId = state.bookmarkedSurahId
    val bookmarkedAyahNo = state.bookmarkedAyahNo
    val activeJuz = state.activeJuz
    val drawerState = layout.drawerState
    val scope = layout.scope
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    genderedStringResource(GenderedStrings.quranActiveJuz, formatJuzNumber(activeJuz)),
                    fontWeight = FontWeight.Black,
                    color      = q.accentStrong,
                    fontSize   = 20.sp
                )
                if (audioState !is QuranAudioState.Idle) {
                    val label = when (audioState) {
                        is QuranAudioState.Loading -> genderedStringResource(GenderedStrings.loading)
                        is QuranAudioState.Playing -> genderedStringResource(GenderedStrings.quranPlaying)
                        is QuranAudioState.Paused  -> genderedStringResource(GenderedStrings.quranPaused)
                        else -> ""
                    }
                    if (label.isNotEmpty()) {
                        Text(
                            label,
                            fontSize = 11.sp,
                            color    = q.accent
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = q.textPrimary
                )
            }
        },
        actions = {
            if (audioState !is QuranAudioState.Idle) {
                IconButton(onClick = { actions.stopAudio() }) {
                    Icon(
                        Icons.Rounded.Stop,
                        contentDescription = null,
                        tint     = q.accentStrong,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (bookmarkedSurahId > 0 && bookmarkedAyahNo > 0) {
                val bookmarkedSurahName = remember(bookmarkedSurahId) {
                    QuranConstants.surahs[bookmarkedSurahId - 1].name
                }
                Surface(
                    onClick  = { actions.goToBookmark() },
                    color    = q.accentSoft,
                    shape    = RoundedCornerShape(12.dp),
                    border   = BorderStroke(1.dp, q.accent.copy(0.4f)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Bookmark,
                            null,
                            tint     = q.accentStrong,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$bookmarkedSurahName ($bookmarkedAyahNo)",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = q.accentStrong
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = q.topBar
        )
    )
}
