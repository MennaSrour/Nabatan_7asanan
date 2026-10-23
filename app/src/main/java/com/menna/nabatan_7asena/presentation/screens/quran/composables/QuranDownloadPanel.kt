package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranDownloadPanel(state: QuranUiState, actions: QuranActions) {
    val q = SummerTheme.quran
    val fullDownloadState = state.fullDownloadState
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        when {
            fullDownloadState.isDownloading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(16.dp),
                        color       = q.accentStrong,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        genderedStringResource(GenderedStrings.quranDownloadProgress, fullDownloadState.currentSurahName, fullDownloadState.completedSurahs, fullDownloadState.totalSurahs),
                        color = q.textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            fullDownloadState.isComplete -> {
                Text(
                    genderedStringResource(GenderedStrings.quranOfflineReady),
                    color = q.accentStrong,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            else -> {
                Surface(
                    onClick  = { actions.downloadFullQuran() },
                    color    = q.accent.copy(alpha = 0.16f),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DownloadIcon(tint = q.accentStrong)
                        Text(
                            genderedStringResource(GenderedStrings.quranDownloadAll),
                            color      = q.accentStrong,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = DownloadLabelFont
                        )
                    }
                }
                fullDownloadState.error?.let { err ->
                    Spacer(Modifier.height(4.dp))
                    Text(err, color = q.textMuted, fontSize = 10.sp)
                }
            }
        }
    }
}
