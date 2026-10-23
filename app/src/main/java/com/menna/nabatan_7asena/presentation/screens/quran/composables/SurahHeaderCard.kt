package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SurahHeaderCard(
    surah: com.menna.nabatan_7asena.domain.Surah,
    ayahsCount: Int,
    currentPage: Int,
    downloadState: SurahDownloadState,
    onDownloadClick: () -> Unit
) {
    val q = SummerTheme.quran
    Card(
        shape    = SummerTheme.Shapes.ExtraRounded,
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent),
        border   = BorderStroke(2.dp, q.accent.copy(0.35f)),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        q.headerStart,
                        q.headerEnd
                    )
                )
            )
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val type = if (surah.id in listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)) genderedStringResource(GenderedStrings.quranMedinan) else genderedStringResource(GenderedStrings.quranMeccan)
                    Surface(
                        color  = q.accent.copy(0.15f),
                        shape  = RoundedCornerShape(50)
                    ) {
                        Text(
                            text       = type,
                            color      = q.accentStrong,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text  = genderedStringResource(GenderedStrings.quranPage, currentPage),
                        color = q.textMuted,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text       = genderedStringResource(GenderedStrings.quranSurahName, surah.name),
                        color      = q.accentStrong,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text       = genderedStringResource(GenderedStrings.quranAyahCount, ayahsCount),
                        color      = q.accent,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                SurahDownloadRow(state = downloadState, onClick = onDownloadClick, q = q)
            }
        }
    }
}
