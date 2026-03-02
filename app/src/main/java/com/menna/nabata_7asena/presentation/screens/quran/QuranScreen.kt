package com.menna.nabata_7asena.presentation.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
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
import com.menna.nabata_7asena.ui.theme.RamadanTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuranScreen(viewModel: QuranViewModel = hiltViewModel()) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val savedPage by viewModel.lastSavedPage.collectAsState()
        val quranFontFamily = FontFamily(Font(R.font.quran_font))

        val pagerState = rememberPagerState(
            initialPage = (savedPage - 1).coerceAtLeast(0),
            pageCount = { 604 }
        )
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var drawerMode by remember { mutableStateOf("SURA") }

        LaunchedEffect(pagerState.currentPage) {
            viewModel.onPageChanged(pagerState.currentPage + 1)
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = RamadanTheme.Colors.BackgroundMoon
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📖", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "الفهرس",
                            style = MaterialTheme.typography.headlineSmall,
                            color = RamadanTheme.Colors.PrimaryPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        FilterChip(
                            selected = drawerMode == "SURA",
                            onClick = { drawerMode = "SURA" },
                            label = { Text("السور 📚") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RamadanTheme.Colors.PrimaryGold,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = drawerMode == "JUZ",
                            onClick = { drawerMode = "JUZ" },
                            label = { Text("الأجزاء 📖") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RamadanTheme.Colors.PrimaryGold,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    HorizontalDivider(color = RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.3f))

                    LazyColumn {
                        if (drawerMode == "SURA") {
                            items(QuranConstants.surahs) { surah ->
                                NavigationDrawerItem(
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(surah.name)
                                            Spacer(Modifier.width(8.dp))
                                            Text("🌙", fontSize = 12.sp)
                                        }
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            pagerState.scrollToPage(surah.startPage - 1)
                                            drawerState.close()
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent
                                    )
                                )
                            }
                        } else {
                            items(30) { i ->
                                val juzNum = i + 1
                                NavigationDrawerItem(
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("الجزء $juzNum")
                                            Spacer(Modifier.width(8.dp))
                                            Text("⭐", fontSize = 12.sp)
                                        }
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            pagerState.scrollToPage(
                                                viewModel.getStartPageForJuz(juzNum) - 1
                                            )
                                            drawerState.close()
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    val info = viewModel.getSurahInfo(pagerState.currentPage + 1)
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📖", fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        info.name,
                                        fontWeight = FontWeight.Bold,
                                        color = RamadanTheme.Colors.PrimaryPurple
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "الجزء ${info.juz} - صفحة ${pagerState.currentPage + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = RamadanTheme.Colors.PrimaryPurple
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = RamadanTheme.Colors.BackgroundMoon
                        )
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    QuranRamadanBackground()

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        reverseLayout = true,
                        beyondViewportPageCount = 1
                    ) { pageIndex ->
                        val pageText = remember(pageIndex) {
                            viewModel.getPageText(pageIndex + 1)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp)
                                .padding(top = 24.dp, bottom = 100.dp)
                        ) {
                            Card(
                                shape = RamadanTheme.Shapes.ExtraRounded,
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.95f)
                                ),
                                elevation = CardDefaults.cardElevation(8.dp),
                                border = BorderStroke(
                                    2.dp,
                                    RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("✨", fontSize = 16.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("🌙", fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("✨", fontSize = 16.sp)
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Text(
                                        text = pageText,
                                        fontFamily = quranFontFamily,
                                        fontSize = 24.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 48.sp,
                                        color = Color(0xFF1A1A1A),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("✨", fontSize = 16.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("⭐", fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("✨", fontSize = 16.sp)
                                    }
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
fun QuranRamadanBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFDF7),
                    Color(0xFFFFF9E6),
                    Color(0xFFFFECB3).copy(alpha = 0.3f)
                )
            )
        )
    }
}