package com.menna.nabatan_7asena.presentation.screens.quran

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.ui.theme.QuranColors
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AyahAudioState { IDLE, LOADING, PLAYING, PAUSED }
private val FloatingBarClearance = 92.dp

private val DownloadLabelFont = FontFamily(Font(R.font.cairo_semibold))

@Composable
private fun DownloadIcon(tint: Color, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Icon(
        painter = painterResource(id = R.drawable.ic_quran_download),
        contentDescription = "تنزيل",
        tint = tint,
        modifier = Modifier.size(size)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(viewModel: QuranViewModel = hiltViewModel()) {
    val q = SummerTheme.quran
    val savedPage  by viewModel.lastSavedPage.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val downloadStates by viewModel.surahDownloadStates.collectAsState()
    val fullDownloadState by viewModel.fullDownloadState.collectAsState()
    val quranFont  = FontFamily(Font(R.font.amiri_regular))

    val initialSurah = remember(savedPage) {
        val surah = QuranConstants.getSurahByPage(savedPage)
        (surah.id - 1).coerceIn(0, 113)
    }

    val pagerState = rememberPagerState(initialPage = initialSurah, pageCount = { 114 })

    var hasScrolledToInitial by remember { mutableStateOf(false) }
    LaunchedEffect(savedPage) {
        if (!hasScrolledToInitial && savedPage > 1) {
            val surah = QuranConstants.getSurahByPage(savedPage)
            pagerState.scrollToPage(surah.id - 1)
            hasScrolledToInitial = true
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

  val lazyListStates = remember { mutableStateMapOf<Int, LazyListState>() }

    val bookmarkedSurahId by viewModel.bookmarkedSurah.collectAsState()
    val bookmarkedAyahNo  by viewModel.bookmarkedAyah.collectAsState()
    val snackbarState     = remember { SnackbarHostState() }
    var activeJuz         by remember { mutableStateOf(1) }
    var currentPageNumber by remember { mutableStateOf(1) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onSurahChanged(pagerState.currentPage + 1)
    }

    LaunchedEffect(Unit) {
        viewModel.scrollTarget.collect { target ->
            pagerState.scrollToPage(target.surahIndex)

            val listState = snapshotFlow { lazyListStates[target.surahIndex] }
                .filterNotNull()
                .first()

            val hasBasmala = (target.surahIndex + 1) != 1 && (target.surahIndex + 1) != 9
            val targetIndex = if (hasBasmala) target.ayaNo else target.ayaNo - 1
            listState.scrollToItem(targetIndex.coerceAtLeast(0))
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage to lazyListStates[pagerState.currentPage]?.firstVisibleItemIndex }
            .collect { (page, visibleIndex) ->
                if (visibleIndex == null) return@collect
                val surahId = page + 1
                val ayahs = viewModel.getSurahAyahs(surahId)
                if (ayahs.isEmpty()) return@collect
                val hasBasmala = surahId != 1 && surahId != 9
                val ayahIdx = (visibleIndex - if (hasBasmala) 1 else 0)
                    .coerceIn(0, ayahs.size - 1)
                val ayah = ayahs[ayahIdx]
                currentPageNumber = ayah.page
                activeJuz = ayah.jozz
            }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = q.drawer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = FloatingBarClearance)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
                                .statusBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "الفهرس",
                                style      = MaterialTheme.typography.titleMedium,
                                color      = q.textPrimary,
                                fontWeight = FontWeight.Black
                            )
                        }

                        var indexTab by remember { mutableStateOf(0) }

                        TabRow(
                            selectedTabIndex = indexTab,
                            containerColor   = Color.Transparent,
                            contentColor     = q.accentStrong,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Tab(
                                selected = indexTab == 0,
                                onClick  = { indexTab = 0 },
                                text     = {
                                    Text(
                                        "السور",
                                        fontWeight = if (indexTab == 0) FontWeight.Black else FontWeight.Medium,
                                        fontSize   = 14.sp
                                    )
                                }
                            )
                            Tab(
                                selected = indexTab == 1,
                                onClick  = { indexTab = 1 },
                                text     = {
                                    Text(
                                        "الأجزاء",
                                        fontWeight = if (indexTab == 1) FontWeight.Black else FontWeight.Medium,
                                        fontSize   = 14.sp
                                    )
                                }
                            )
                        }

                        HorizontalDivider(color = q.accent.copy(alpha = 0.2f))

                        Box(modifier = Modifier.weight(1f)) {
                            if (indexTab == 0) {
                                LazyColumn(contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)) {
                                    items(QuranConstants.surahs) { surah ->
                                        val isSelected = pagerState.currentPage == surah.id - 1
                                        val dlState = downloadStates[surah.id] ?: SurahDownloadState()
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
                                                            viewModel.downloadSurah(surah.id)
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
                                }
                            } else {
                                LazyColumn(contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)) {
                                    items(30) { i ->
                                        val juz = i + 1
                                        val isSelected = activeJuz == juz
                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    "الجزء ${formatJuzNumber(juz)}",
                                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                    fontSize   = 15.sp
                                                )
                                            },
                                            selected = isSelected,
                                            onClick  = {
                                                viewModel.goToJuz(juz)
                                                scope.launch { drawerState.close() }
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
                                }
                            }
                        }
                        HorizontalDivider(color = q.accent.copy(alpha = 0.2f))
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
                                            "بينزّل ${fullDownloadState.currentSurahName} (${fullDownloadState.completedSurahs}/${fullDownloadState.totalSurahs})",
                                            color = q.textMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                fullDownloadState.isComplete -> {
                                    Text(
                                        "✓ المصحف كامل متاح أوفلاين",
                                        color = q.accentStrong,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                else -> {
                                    Surface(
                                        onClick  = { viewModel.downloadFullQuran() },
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
                                                "تنزيل صوت المصحف كامل",
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
                }
            }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost   = { SnackbarHost(hostState = snackbarState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "الجزء ${formatJuzNumber(activeJuz)}",
                                    fontWeight = FontWeight.Black,
                                    color      = q.accentStrong,
                                    fontSize   = 20.sp
                                )
                                if (audioState !is QuranAudioState.Idle) {
                                    val label = when (audioState) {
                                        is QuranAudioState.Loading -> "جاري التحميل..."
                                        is QuranAudioState.Playing -> " يعمل الأن..."
                                        is QuranAudioState.Paused  -> "⏸ متوقف"
                                        else -> ""
                                    }
                                    if (label.isNotEmpty()) {
                                        Text(
                                            label,
                                            fontSize = 11.sp,
                                            color    = q.accent
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = q.textPrimary
                                )
                            }
                        },
                        actions = {
                            if (audioState !is QuranAudioState.Idle) {
                                IconButton(onClick = { viewModel.stopAudio() }) {
                                    Icon(
                                        Icons.Rounded.Stop,
                                        contentDescription = null,
                                        tint     = q.accentStrong,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            if (bookmarkedSurahId > 0 && bookmarkedAyahNo > 0) {
                                val bookmarkedSurahName = remember(bookmarkedSurahId) {
                                    viewModel.getSurahInfo(bookmarkedSurahId).name
                                }
                                Surface(
                                    onClick  = { viewModel.goToBookmark() },
                                    color    = q.accentSoft,
                                    shape    = RoundedCornerShape(12.dp),
                                    border   = BorderStroke(1.dp, q.accent.copy(0.4f)),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.Bookmark,
                                            null,
                                            tint     = q.accentStrong,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "$bookmarkedSurahName ($bookmarkedAyahNo)",
                                            fontSize   = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = q.accentStrong
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = q.topBar
                        )
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    QuranBackground()

                    HorizontalPager(
                        state    = pagerState,
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        beyondViewportPageCount = 1
                    ) { pageIndex ->
                        val currentSurahId = pageIndex + 1
                        val surahInfo = remember(currentSurahId) { viewModel.getSurahInfo(currentSurahId) }
                        val ayahs     = remember(currentSurahId) { viewModel.getSurahAyahs(currentSurahId) }

                        LaunchedEffect(currentSurahId) {
                            viewModel.refreshSurahDownloadState(currentSurahId)
                        }
                        val surahDownloadState = downloadStates[currentSurahId]
                            ?: SurahDownloadState(totalAyahs = ayahs.size)

                        val lazyListState = rememberLazyListState()
                        SideEffect { lazyListStates[pageIndex] = lazyListState }

                        Column(modifier = Modifier.fillMaxSize()) {
                            SurahHeaderCard(
                                surah = surahInfo,
                                ayahsCount = ayahs.size,
                                currentPage = currentPageNumber,
                                downloadState = surahDownloadState,
                                onDownloadClick = { viewModel.downloadSurah(currentSurahId) }
                            )

                            LazyColumn(
                                state       = lazyListState,
                                contentPadding = PaddingValues(
                                    start = 16.dp, end = 16.dp,
                                    top   = 8.dp,  bottom = 120.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            ) {
                                val hasBasmala = currentSurahId != 1 && currentSurahId != 9
                                if (hasBasmala) {
                                    item { BasmalaCard(quranFont) }
                                }

                                itemsIndexed(ayahs) { _, ayah ->
                                    val isBookmarked = bookmarkedSurahId == ayah.suraNo &&
                                            bookmarkedAyahNo  == ayah.ayaNo

                                    val ayahAudioState: AyahAudioState = when (val s = audioState) {
                                        is QuranAudioState.Loading ->
                                            if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                                                AyahAudioState.LOADING else AyahAudioState.IDLE
                                        is QuranAudioState.Playing ->
                                            if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                                                AyahAudioState.PLAYING else AyahAudioState.IDLE
                                        is QuranAudioState.Paused ->
                                            if (s.suraNo == ayah.suraNo && s.ayaNo == ayah.ayaNo)
                                                AyahAudioState.PAUSED else AyahAudioState.IDLE
                                        else -> AyahAudioState.IDLE
                                    }

                                    AyahCard(
                                        ayah            = ayah,
                                        isBookmarked    = isBookmarked,
                                        quranFont       = quranFont,
                                        audioState      = ayahAudioState,
                                        onPlayAudio     = { viewModel.playAyahAudio(ayah.suraNo, ayah.ayaNo) },
                                        onBookmarkClick = {
                                            viewModel.saveBookmark(ayah.suraNo, ayah.ayaNo)
                                            scope.launch {
                                                snackbarState.showSnackbar("تم حفظ العلامة بنجاح 🔖")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun QuranBackground() {
    val q = SummerTheme.quran
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    q.backgroundTop,
                    q.backgroundMid,
                    q.backgroundEnd
                )
            )
        )
    }
}

@Composable
private fun SurahDownloadRow(state: SurahDownloadState, onClick: () -> Unit, q: QuranColors) {
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
                    state.isDownloading -> "يتم تنزيل صوت السورة... (${state.downloadedAyahs}/${state.totalAyahs})"
                    state.isComplete    -> "صوت السورة متاح بدون الحاجة للإنترنت"
                    state.error != null -> "${state.error} — الضغط للمحاولة تاني"
                    else                -> "تنزيل صوت السورة"
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
                    val type = if (surah.id in listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)) "سورة مدنية" else "سورة مكية"
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
                        text  = "صفحة $currentPage",
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
                        text       = "سُورَةُ ${surah.name}",
                        color      = q.accentStrong,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text       = "$ayahsCount آية",
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

@Composable
fun BasmalaCard(fontFamily: FontFamily) {
    val q = SummerTheme.quran
    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(containerColor = q.card),
        border   = BorderStroke(1.dp, q.accent.copy(0.25f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier         = Modifier.fillMaxWidth().padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontFamily = fontFamily,
                fontSize   = 22.sp,
                textAlign  = TextAlign.Center,
                color      = q.accentStrong
            )
        }
    }
}

@Composable
fun AyahCard(
    ayah: AyahModel,
    isBookmarked: Boolean,
    quranFont: FontFamily,
    audioState: AyahAudioState,
    onPlayAudio: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val q = SummerTheme.quran
    val isActive = audioState == AyahAudioState.PLAYING || audioState == AyahAudioState.LOADING

    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(
            containerColor = if (isActive) q.cardActive else q.card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 3.dp
        ),
        border = BorderStroke(
            width = if (isBookmarked || isActive) 1.5.dp else 1.dp,
            color = when {
                isActive     -> q.accent
                isBookmarked -> q.accent
                else         -> q.border
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Surface(
                    color    = q.accent.copy(0.12f),
                    shape    = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${ayah.ayaNo}",
                            color      = q.accentStrong,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                when (audioState) {
                                    AyahAudioState.PLAYING ->
                                        q.accent.copy(0.2f)
                                    AyahAudioState.PAUSED ->
                                        q.accentSoft
                                    else -> Color.Transparent
                                },
                                CircleShape
                            )
                            .border(
                                1.dp,
                                when (audioState) {
                                    AyahAudioState.IDLE -> Color.Transparent
                                    else -> q.accent.copy(0.4f)
                                },
                                CircleShape
                            )
                            .clickable { onPlayAudio() },
                        contentAlignment = Alignment.Center
                    ) {
                        when (audioState) {
                            AyahAudioState.LOADING -> {
                                val rotation by rememberInfiniteTransition(label = "load")
                                    .animateFloat(
                                        initialValue = 0f,
                                        targetValue  = 360f,
                                        animationSpec = infiniteRepeatable(
                                            tween(900, easing = LinearEasing),
                                            RepeatMode.Restart
                                        ),
                                        label = "rotate"
                                    )
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(18.dp).rotate(rotation),
                                    color       = q.accentStrong,
                                    strokeWidth = 2.dp
                                )
                            }
                            AyahAudioState.PLAYING -> {
                                Icon(
                                    Icons.Rounded.Pause,
                                    null,
                                    tint     = q.accentStrong,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            AyahAudioState.PAUSED,
                            AyahAudioState.IDLE -> {
                                Icon(
                                    Icons.Rounded.VolumeUp,
                                    null,
                                    tint     = q.accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = onBookmarkClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint     = if (isBookmarked) q.accentStrong else q.textMuted.copy(0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val displayText = remember(ayah.text) {
                ayah.text.replace(Regex("[^\u0600-\u06FF\u0020\u06DD]"), "").trim()
            }

            Text(
                text       = displayText,
                fontFamily = quranFont,
                fontSize   = 24.sp,
                textAlign  = TextAlign.Right,
                lineHeight = 44.sp,
                color      = q.textPrimary,
                modifier   = Modifier.fillMaxWidth()
            )

            if (audioState == AyahAudioState.PLAYING) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AudioWaveIndicator()
                }
            }
        }
    }
}

@Composable
fun AudioWaveIndicator() {
    val q = SummerTheme.quran
    val transition = rememberInfiniteTransition(label = "wave")
    val bars = List(5) { i ->
        transition.animateFloat(
            initialValue  = 4f,
            targetValue   = 16f,
            animationSpec = infiniteRepeatable(
                animation  = tween(380 + i * 70, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$i"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        modifier              = Modifier.height(20.dp)
    ) {
        bars.forEach { bar ->
            val h by bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .background(q.accent.copy(0.8f), RoundedCornerShape(2.dp))
            )
        }
    }
}

fun formatJuzNumber(juz: Int): String {
    val names = listOf(
        "الأول","الثاني","الثالث","الرابع","الخامس",
        "السادس","السابع","الثامن","التاسع","العاشر",
        "الحادي عشر","الثاني عشر","الثالث عشر","الرابع عشر","الخامس عشر",
        "السادس عشر","السابع عشر","الثامن عشر","التاسع عشر","العشرون",
        "الحادي والعشرون","الثاني والعشرون","الثالث والعشرون",
        "الرابع والعشرون","الخامس والعشرون","السادس والعشرون",
        "السابع والعشرون","الثامن والعشرون","التاسع والعشرون","الثلاثون"
    )
    return names.getOrNull(juz - 1) ?: juz.toString()
}