package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState
import com.menna.nabatan_7asena.ui.theme.QuranColors

@Composable
internal fun SurahDownloadRow(state: SurahDownloadState, onClick: () -> Unit, q: QuranColors) {
    val clickable = !state.isDownloading && !state.isComplete
    Surface(
        onClick  = onClick,
        enabled  = clickable,
        color    = q.accent.copy(alpha = if (state.isComplete) 0.10f else 0.16f),
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
            when {
                state.isDownloading -> CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    color       = q.accentStrong,
                    strokeWidth = 2.dp
                )
                state.isComplete -> Text("✓", color = q.accentStrong, fontWeight = FontWeight.Black, fontSize = 16.sp)
                state.error != null -> Text("⚠", fontSize = 15.sp)
                else -> DownloadIcon(tint = q.accentStrong)
            }

            Text(
                text = when {
                    state.isDownloading -> genderedStringResource(GenderedStrings.quranAyahDownloadProgress, state.downloadedAyahs, state.totalAyahs)
                    state.isComplete    -> genderedStringResource(GenderedStrings.quranSurahOfflineReady)
                    state.error != null -> genderedStringResource(GenderedStrings.quranDownloadRetry, state.error.orEmpty())
                    else                -> genderedStringResource(GenderedStrings.quranDownloadSurah)
                },
                color      = q.accentStrong,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = DownloadLabelFont,
                modifier   = Modifier.weight(1f)
            )
        }
    }
}
