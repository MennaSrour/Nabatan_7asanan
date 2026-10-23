package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranIndexDrawer(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val q = SummerTheme.quran
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = q.drawer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = FloatingBarClearance)
        ) {
            QuranIndexHeading()

            var indexTab by remember { mutableStateOf(0) }

            QuranIndexTabs(indexTab, { indexTab = it })

            HorizontalDivider(color = q.accent.copy(alpha = 0.2f))

            Box(modifier = Modifier.weight(1f)) {
                if (indexTab == 0) {
                    QuranSurahIndex(state, actions, layout)
                } else {
                    QuranJuzIndex(state, actions, layout)
                }
            }
            HorizontalDivider(color = q.accent.copy(alpha = 0.2f))
            QuranDownloadPanel(state, actions)
        }
    }
}
