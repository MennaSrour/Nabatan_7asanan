package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.SurahDownloadState
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranSurahIndexItem(surah: com.menna.nabatan_7asena.domain.Surah, isSelected: Boolean, dlState: SurahDownloadState, actions: QuranActions, layout: QuranLayoutState) {
    val q = SummerTheme.quran
    val scope = layout.scope
    val pagerState = layout.pagerState
    val drawerState = layout.drawerState
    NavigationDrawerItem(
        label = {
            Text(
                surah.name,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize   = 15.sp
            )
        },
        badge = {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = !dlState.isDownloading && !dlState.isComplete) {
                    actions.downloadSurah(surah.id)
                },
                contentAlignment = Alignment.Center
            ) {
                when {
                    dlState.isDownloading -> CircularProgressIndicator(
                        modifier    = Modifier.size(15.dp),
                        color       = q.accentStrong,
                        strokeWidth = 2.dp
                    )
                    dlState.isComplete -> Text(
                        "✓",
                        color = q.accentStrong,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    else -> DownloadIcon(tint = q.textMuted, size = 17.dp)
                }
            }
        },
        selected = isSelected,
        onClick  = {
            scope.launch {
                pagerState.scrollToPage(surah.id - 1)
                drawerState.close()
            }
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor   = q.accent.copy(0.15f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor        = q.accentStrong,
            unselectedTextColor      = q.textPrimary
        ),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
