package com.menna.nabata_7asena.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabata_7asena.presentation.screens.home.component.*
import com.menna.nabata_7asena.ui.theme.RamadanTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val showChallenge by viewModel.showChallengeDialog.collectAsState()
    val currentRiddle by viewModel.currentRiddle.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    val suggestions by viewModel.suggestedTasks.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp)
            ) { data ->
                Card(
                    shape = RamadanTheme.Shapes.MediumRounded,
                    colors = CardDefaults.cardColors(
                        containerColor = RamadanTheme.Colors.BackgroundMoon
                    ),
                    border = BorderStroke(2.dp, RamadanTheme.Colors.PrimaryGold),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(RamadanTheme.Emojis.SPARKLES, fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = data.visuals.message,
                            color = RamadanTheme.Colors.PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(RamadanTheme.Emojis.SPARKLES, fontSize = 20.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            RamadanSkyBackgroundAnimated()

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ColorfulHeaderRamadan(
                    user = uiState.user,
                    hijriDate = uiState.hijriDate,
                    onSettingsClick = onNavigateToSettings
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RamadanTheme.Colors.PrimaryGold)
                    }
                } else {
                    TasksListRamadan(
                        tasks = uiState.tasks,
                        onTaskClick = { viewModel.onTaskChecked(it) },
                        onTreasureClick = { viewModel.openDailyChallenge() },
                        onPlaySound = { task ->
                            viewModel.playTaskSound(context, task)
                        },
                        wisdom = uiState.dailyWisdom
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    showAddSheet = true
                    viewModel.loadSuggestions()
                },
                containerColor = RamadanTheme.Colors.PrimaryGold,
                contentColor = Color.White,
                shape = RamadanTheme.Shapes.SmallRounded,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 24.dp, end = 24.dp, start = 24.dp, bottom = 100.dp)
                    .size(70.dp)
                    .shadow(
                        12.dp,
                        RamadanTheme.Shapes.SmallRounded,
                        spotColor = RamadanTheme.Colors.StarGold
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                    Text("🌙", fontSize = 12.sp)
                }
            }

            if (uiState.showCelebration) {
                CelebrationOverlayRamadan(onDismiss = { viewModel.dismissCelebration() })
            }

            if (showAddSheet) {
                AddTaskBottomSheetRamadan(
                    onDismiss = { showAddSheet = false },
                    suggestions = suggestions,
                    onTaskSelected = { taskTitle ->
                        viewModel.onAddExtraTask(taskTitle)
                        showAddSheet = false
                    }
                )
            }

            if (showChallenge && currentRiddle != null) {
                DailyChallengeDialogRamadan(
                    riddle = currentRiddle!!,
                    onDismiss = { viewModel.closeChallenge() },
                    onAnswerSelected = { isCorrect ->
                        if (isCorrect) viewModel.onCorrectAnswer()
                    }
                )
            }
        }
    }
}

@Composable
fun RamadanSkyBackgroundAnimated() {
    val starAlphas = RamadanTheme.rememberStarAlphas(count = 9)

    val moonScale = RamadanTheme.rememberMoonGlowAnimation()

    val starData = remember {
        listOf(
            Offset(0.10f, 0.10f) to 3f,
            Offset(0.30f, 0.15f) to 4f,
            Offset(0.70f, 0.08f) to 5f,
            Offset(0.85f, 0.25f) to 3f,
            Offset(0.50f, 0.05f) to 4f,
            Offset(0.20f, 0.30f) to 3f,
            Offset(0.90f, 0.12f) to 4f,
            Offset(0.15f, 0.22f) to 3f,
            Offset(0.60f, 0.18f) to 5f,
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A237E),
                    Color(0xFF283593),
                    Color(0xFF3949AB),
                    Color(0xFF5C6BC0).copy(alpha = 0.5f)
                )
            )
        )

        starData.forEachIndexed { index, (normalizedOffset, radius) ->
            drawCircle(
                color = Color(0xFFFFF176),
                radius = radius.dp.toPx(),
                center = Offset(size.width * normalizedOffset.x, size.height * normalizedOffset.y),
                alpha = starAlphas.getOrElse(index) { 0.8f }
            )
        }

        val moonCenter = Offset(size.width * 0.85f, size.height * 0.15f)
        val moonRadius = 40.dp.toPx()

        drawCircle(
            color = Color(0xFFFFF9C4).copy(alpha = 0.15f),
            radius = moonRadius * moonScale * 1.6f,
            center = moonCenter
        )
        drawCircle(
            color = Color(0xFFFFFDE7).copy(alpha = 0.35f),
            radius = moonRadius * moonScale * 1.3f,
            center = moonCenter
        )

        drawCircle(
            color = Color(0xFFFFFDE7),
            radius = moonRadius * moonScale,
            center = moonCenter
        )
    }
}

@Composable
fun RamadanDecorationTop() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(6) {
            Text(
                text = if (it % 2 == 0) "🏮" else "⭐",
                fontSize = 24.sp,
                modifier = Modifier.offset(y = (it * 3).dp)
            )
        }
    }
}