package com.menna.nabata_7asena.presentation.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabata_7asena.domain.entity.TaskCategory
import com.menna.nabata_7asena.presentation.screens.home.component.AddTaskBottomSheet
import com.menna.nabata_7asena.presentation.screens.home.component.BentoStatsGrid
import com.menna.nabata_7asena.presentation.screens.home.component.CelebrationOverlay
import com.menna.nabata_7asena.presentation.screens.home.component.ColorfulHeader
import com.menna.nabata_7asena.presentation.screens.home.component.DailyChallengeDialog
import com.menna.nabata_7asena.presentation.screens.home.component.DailyWisdomCard
import com.menna.nabata_7asena.presentation.screens.home.component.NightBackgroundGradient
import com.menna.nabata_7asena.presentation.screens.home.component.PrayerTimesRow
import com.menna.nabata_7asena.presentation.screens.home.component.TaskBubbleCard
import com.menna.nabata_7asena.presentation.screens.home.component.TreasureChestCard
import com.menna.nabata_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToSebha: () -> Unit,
    completedExternalTaskId: Int? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestions by viewModel.suggestedTasks.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(completedExternalTaskId) {
        completedExternalTaskId?.let { viewModel.markTaskCompletedExternally(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    val backgroundGradient = if (uiState.isNightTheme) {
        NightBackgroundGradient
    } else {
        Brush.verticalGradient(listOf(SummerTheme.colors.dayBackground, SummerTheme.colors.dayBackground))
    }

    Scaffold(
        containerColor = SummerTheme.colors.transparent,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp)
            ) { data ->
                Card(
                    shape = SummerTheme.shapes.taskCard,
                    colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.backgroundLight),
                    border = BorderStroke(2.dp, SummerTheme.colors.goldWarm),
                    modifier = Modifier
                        .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = SummerTheme.colors.goldWarm,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = data.visuals.message,
                            color = SummerTheme.colors.textDarkBrown,
                            style = SummerTheme.typography.snackbarText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = SummerTheme.colors.goldWarm,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            val prayers = uiState.tasks.filter { it.category == TaskCategory.PRAYER }
            val dailyTasks = uiState.tasks.filter { it.category != TaskCategory.PRAYER }
            val sectionTitleColor =
                if (uiState.isNightTheme) SummerTheme.colors.white else SummerTheme.colors.sectionTitleDark

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 90.dp)
            ) {
                item {
                    ColorfulHeader(
                        user = uiState.user,
                        hijriDate = uiState.hijriDate,
                        onSettingsClick = onNavigateToSettings,
                        onAvatarClick = { viewModel.playWelcomeSound() },
                        isNight = uiState.isNightTheme
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SummerTheme.colors.goldWarm)
                        }
                    }
                } else {
                    item {
                        BentoStatsGrid(
                            user = uiState.user,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    item {
                        DailyWisdomCard(
                            wisdom = uiState.dailyWisdom,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                tint = sectionTitleColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "الصلوات",
                                style = SummerTheme.typography.sectionTitle,
                                color = sectionTitleColor
                            )
                        }
                    }

                    item {
                        PrayerTimesRow(
                            prayers = prayers,
                            onPrayerClick = { viewModel.onTaskChecked(it) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = sectionTitleColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "المهام اليومية",
                                style = SummerTheme.typography.sectionTitle,
                                color = sectionTitleColor
                            )
                        }
                    }

                    items(items = dailyTasks, key = { it.id }) { item ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .animateItem(
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                        ) {
                            TaskBubbleCard(
                                item = item,
                                hasBookmark = uiState.hasBookmark,
                                onClick = {
                                    when (item.category) {
                                        TaskCategory.QURAN -> onNavigateToQuran()
                                        TaskCategory.AZKAR -> onNavigateToSebha()
                                        TaskCategory.CHALLENGE -> viewModel.openDailyChallenge()
                                        else -> viewModel.onTaskChecked(item)
                                    }
                                },
                                onCheckClick = { viewModel.onTaskChecked(item) },
                                onPlaySound = { viewModel.playTaskSound(item) }
                            )
                        }
                    }

                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            val allTasksDone =
                                dailyTasks.isNotEmpty() && dailyTasks.all { it.isCompleted }
                            val allPrayersDone =
                                prayers.isNotEmpty() && prayers.all { it.isCompleted }
                            TreasureChestCard(
                                isUnlocked = allTasksDone && allPrayersDone,
                                onClick = { viewModel.openDailyChallenge() }
                            )
                        }
                    }

                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Surface(
                                onClick = {
                                    showAddSheet = true
                                    viewModel.loadSuggestions()
                                },
                                shape = RoundedCornerShape(22.dp),
                                color = SummerTheme.colors.white,
                                border = BorderStroke(2.dp, SummerTheme.colors.addBtnBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(
                                        4.dp,
                                        RoundedCornerShape(22.dp),
                                        spotColor = SummerTheme.colors.addBtnShadow
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = SummerTheme.colors.successGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "إضافة عمل صالح جديد",
                                        color = SummerTheme.colors.addBtnText,
                                        style = SummerTheme.typography.addButtonText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.showCelebration) {
                CelebrationOverlay(onDismiss = { viewModel.dismissCelebration() })
            }

            if (showAddSheet) {
                AddTaskBottomSheet(
                    onDismiss = { showAddSheet = false },
                    suggestions = suggestions,
                    onTaskSelected = { taskTitle ->
                        viewModel.onAddExtraTask(taskTitle)
                        showAddSheet = false
                    }
                )
            }

            if (uiState.showChallengeDialog && uiState.currentRiddle != null) {
                DailyChallengeDialog(
                    riddle = uiState.currentRiddle!!,
                    onDismiss = { viewModel.closeChallenge() },
                    onAnswerSelected = { isCorrect ->
                        if (isCorrect) viewModel.onCorrectAnswer()
                    }
                )
            }
        }
    }
}