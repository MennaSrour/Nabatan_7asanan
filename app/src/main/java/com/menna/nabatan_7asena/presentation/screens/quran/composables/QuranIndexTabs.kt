package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranIndexTabs(indexTab: Int, onTabSelected: (Int) -> Unit) {
    val q = SummerTheme.quran
    TabRow(
        selectedTabIndex = indexTab,
        containerColor   = Color.Transparent,
        contentColor     = q.accentStrong,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Tab(
            selected = indexTab == 0,
            onClick  = { onTabSelected(0) },
            text     = {
                Text(
                    genderedStringResource(GenderedStrings.quranSurahs),
                    fontWeight = if (indexTab == 0) FontWeight.Black else FontWeight.Medium,
                    fontSize   = 14.sp
                )
            }
        )
        Tab(
            selected = indexTab == 1,
            onClick  = { onTabSelected(1) },
            text     = {
                Text(
                    genderedStringResource(GenderedStrings.quranParts),
                    fontWeight = if (indexTab == 1) FontWeight.Black else FontWeight.Medium,
                    fontSize   = 14.sp
                )
            }
        )
    }
}
