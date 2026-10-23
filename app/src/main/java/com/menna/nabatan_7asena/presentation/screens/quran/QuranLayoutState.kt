package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.menna.nabatan_7asena.domain.QuranConstants
import kotlinx.coroutines.CoroutineScope

class QuranLayoutState(
    val pagerState: PagerState,
    val drawerState: DrawerState,
    val snackbar: SnackbarHostState,
    val lazyListStates: SnapshotStateMap<Int, LazyListState>,
    val scope: CoroutineScope
)

@Composable
internal fun rememberQuranLayoutState(savedPage: Int): QuranLayoutState {
    val initialSurah = (QuranConstants.getSurahByPage(savedPage).id - 1).coerceIn(0, 113)
    val pager = rememberPagerState(initialPage = initialSurah, pageCount = { 114 })
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val snackbar = remember { SnackbarHostState() }
    val lists = remember { mutableStateMapOf<Int, LazyListState>() }
    val scope = rememberCoroutineScope()
    var restored by remember { mutableStateOf(false) }
    LaunchedEffect(savedPage) {
        if (!restored && savedPage > 1) { pager.scrollToPage(initialSurah); restored = true }
    }
    return remember(pager, drawer, snackbar, lists, scope) { QuranLayoutState(pager, drawer, snackbar, lists, scope) }
}
