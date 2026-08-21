package com.menna.nabata_7asena.presentation.screens.quran

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.QuranConstants
import com.menna.nabata_7asena.domain.entity.AyahModel
import com.menna.nabata_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

enum class AyahAudioState { IDLE, LOADING, PLAYING, PAUSED }

private object QuranTheme {
    val Purple      = Color(0xFF9B87F5)
    val PurpleDeep  = Color(0xFF6C56D9)
    val PurpleSoft  = Color(0xFFEDE7FB)
    val Beige       = Color(0xFFFBF6EE)
    val BeigeDeep   = Color(0xFFF0E4D0)
    val White       = Color(0xFFFFFFFF)
    val TextDark    = Color(0xFF3C3550)
    val TextMuted   = Color(0xFF8F88A6)
    val BorderLight = Color(0xFFE4DEF2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(viewModel: QuranViewModel = hiltViewModel()) {
    val savedPage  by viewModel.lastSavedPage.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
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

    val bookmarkedSurahId by viewModel.bookmarkedSurah.collectAsState()
    val bookmarkedAyahNo  by viewModel.bookmarkedAyah.collectAsState()
    val snackbarState     = remember { SnackbarHostState() }
    var activeJuz         by remember { mutableStateOf(1) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onSurahChanged(pagerState.currentPage + 1)
    }

    LaunchedEffect(Unit) {
        viewModel.scrollToSurahEvent.collect { page ->
            pagerState.animateScrollToPage(page)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = QuranTheme.Beige
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "فهرس السور",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = QuranTheme.TextDark,
                            fontWeight = FontWeight.Black
                        )
                    }

                    HorizontalDivider(color = QuranTheme.Purple.copy(alpha = 0.2f))

                    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)) {
                        items(QuranConstants.surahs) { surah ->
                            val isSelected = pagerState.currentPage == surah.id - 1
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        surah.name,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        fontSize   = 15.sp
                                    )
                                },
                                selected = isSelected,
                                onClick  = {
                                    scope.launch {
                                        pagerState.scrollToPage(surah.id - 1)
                                        drawerState.close()
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor   = QuranTheme.Purple.copy(0.15f),
                                    unselectedContainerColor = Color.Transparent,
                                    selectedTextColor        = QuranTheme.PurpleDeep,
                                    unselectedTextColor      = QuranTheme.TextDark
                                ),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
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
                                    color      = QuranTheme.PurpleDeep,
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
                                            color    = QuranTheme.Purple
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
                                    tint = QuranTheme.TextDark
                                )
                            }
                        },
                        actions = {
                            if (audioState !is QuranAudioState.Idle) {
                                IconButton(onClick = { viewModel.stopAudio() }) {
                                    Icon(
                                        Icons.Rounded.Stop,
                                        contentDescription = null,
                                        tint     = QuranTheme.PurpleDeep,
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
                                    color    = QuranTheme.PurpleSoft,
                                    shape    = RoundedCornerShape(12.dp),
                                    border   = BorderStroke(1.dp, QuranTheme.Purple.copy(0.4f)),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.Bookmark,
                                            null,
                                            tint     = QuranTheme.PurpleDeep,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "$bookmarkedSurahName ($bookmarkedAyahNo)",
                                            fontSize   = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = QuranTheme.PurpleDeep
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = QuranTheme.Beige
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

                        val currentJuz = remember(ayahs) { ayahs.firstOrNull()?.jozz ?: 1 }
                        LaunchedEffect(pagerState.currentPage, currentJuz) {
                            if (pagerState.currentPage == pageIndex) activeJuz = currentJuz
                        }

                        val lazyListState = rememberLazyListState()

                        LaunchedEffect(pagerState.currentPage) {
                            if (pagerState.currentPage == pageIndex) {
                                viewModel.scrollToAyahEvent.collect { ayahNo ->
                                    val hasBasmala  = currentSurahId != 1 && currentSurahId != 9
                                    val targetIndex = if (hasBasmala) ayahNo else ayahNo - 1
                                    lazyListState.animateScrollToItem(targetIndex.coerceAtLeast(0))
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            SurahHeaderCard(surah = surahInfo, ayahsCount = ayahs.size)

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
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    QuranTheme.White,
                    QuranTheme.Beige,
                    QuranTheme.PurpleSoft.copy(alpha = 0.5f)
                )
            )
        )
    }
}

@Composable
fun SurahHeaderCard(surah: com.menna.nabata_7asena.domain.Surah, ayahsCount: Int) {
    Card(
        shape    = SummerTheme.Shapes.ExtraRounded,
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent),
        border   = BorderStroke(2.dp, QuranTheme.Purple.copy(0.35f)),
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
                            QuranTheme.PurpleSoft,
                            QuranTheme.Beige
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
                        color  = QuranTheme.Purple.copy(0.15f),
                        shape  = RoundedCornerShape(50)
                    ) {
                        Text(
                            text       = type,
                            color      = QuranTheme.PurpleDeep,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text  = "بداية من صفحة ${surah.startPage}",
                        color = QuranTheme.TextMuted,
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
                        color      = QuranTheme.PurpleDeep,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text       = "$ayahsCount آية",
                        color      = QuranTheme.Purple,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun BasmalaCard(fontFamily: FontFamily) {
    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(containerColor = QuranTheme.White.copy(alpha = 0.9f)),
        border   = BorderStroke(1.dp, QuranTheme.Purple.copy(0.25f)),
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
                color      = QuranTheme.PurpleDeep
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
    val isActive = audioState == AyahAudioState.PLAYING || audioState == AyahAudioState.LOADING

    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(
            containerColor = if (isActive) QuranTheme.PurpleSoft else QuranTheme.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 3.dp
        ),
        border = BorderStroke(
            width = if (isBookmarked || isActive) 1.5.dp else 1.dp,
            color = when {
                isActive     -> QuranTheme.Purple
                isBookmarked -> QuranTheme.Purple
                else         -> QuranTheme.BorderLight
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
                    color    = QuranTheme.Purple.copy(0.12f),
                    shape    = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${ayah.ayaNo}",
                            color      = QuranTheme.PurpleDeep,
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
                                        QuranTheme.Purple.copy(0.2f)
                                    AyahAudioState.PAUSED ->
                                        QuranTheme.PurpleSoft
                                    else -> Color.Transparent
                                },
                                CircleShape
                            )
                            .border(
                                1.dp,
                                when (audioState) {
                                    AyahAudioState.IDLE -> Color.Transparent
                                    else -> QuranTheme.Purple.copy(0.4f)
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
                                    color       = QuranTheme.PurpleDeep,
                                    strokeWidth = 2.dp
                                )
                            }
                            AyahAudioState.PLAYING -> {
                                Icon(
                                    Icons.Rounded.Pause,
                                    null,
                                    tint     = QuranTheme.PurpleDeep,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            AyahAudioState.PAUSED,
                            AyahAudioState.IDLE -> {
                                Icon(
                                    Icons.Rounded.VolumeUp,
                                    null,
                                    tint     = QuranTheme.Purple,
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
                            tint     = if (isBookmarked) QuranTheme.PurpleDeep else QuranTheme.TextMuted.copy(0.6f),
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
                color      = QuranTheme.TextDark,
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
                    .background(QuranTheme.Purple.copy(0.8f), RoundedCornerShape(2.dp))
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