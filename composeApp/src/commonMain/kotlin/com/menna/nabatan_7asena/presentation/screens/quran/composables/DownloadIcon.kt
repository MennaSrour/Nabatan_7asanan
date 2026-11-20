package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.menna.nabatan_7asena.resources.painterResource
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource

@Composable
fun DownloadIcon(tint: Color, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Icon(
        painter = painterResource(id = R.drawable.ic_quran_download),
        contentDescription = genderedStringResource(GenderedStrings.quranDownload),
        tint = tint,
        modifier = Modifier.size(size)
    )
}
