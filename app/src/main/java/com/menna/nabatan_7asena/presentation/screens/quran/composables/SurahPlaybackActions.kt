package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun SurahPlaybackActions(download: SurahDownloadState, repeatEnabled: Boolean,
    onDownload: () -> Unit, onRepeat: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.weight(1f)) { SurahDownloadRow(download, onDownload, SummerTheme.quran) }
        Box(Modifier.weight(1f)) { RepeatMemorizeButton(repeatEnabled, onRepeat) }
    }
}

@Composable
private fun RepeatMemorizeButton(enabled: Boolean, onClick: () -> Unit) {
    val colors = SummerTheme.quran
    Surface(onClick = onClick, enabled = enabled, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = colors.accentSoft, contentColor = colors.accentStrong) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painterResource(R.drawable.ic_repeat_play), contentDescription = null,
                tint = colors.accentStrong, modifier = Modifier.size(22.dp))
            Text(genderedStringResource(GenderedStrings.quranRepeatMemorize))
        }
    }
}
