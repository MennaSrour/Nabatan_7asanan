package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.presentation.screens.quran.QuranUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranContent(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState) {
    val drawerState = layout.drawerState
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                QuranIndexDrawer(state, actions, layout)
            }
        ) {
            QuranReaderScaffold(state, actions, layout)
        }
        if (state.isRepeatBottomSheetVisible) QuranRepeatBottomSheet(state, actions)
    }
}
