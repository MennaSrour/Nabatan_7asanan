package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.screens.home.component.AddTaskBottomSheet
import com.menna.nabatan_7asena.presentation.screens.home.component.BentoStatsGrid
import com.menna.nabatan_7asena.presentation.screens.home.component.CelebrationOverlay
import com.menna.nabatan_7asena.presentation.screens.home.component.ColorfulHeader
import com.menna.nabatan_7asena.presentation.screens.home.component.DailyChallengeDialog
import com.menna.nabatan_7asena.presentation.screens.home.component.DailyWisdomCard
import com.menna.nabatan_7asena.presentation.screens.home.component.NightBackgroundGradient
import com.menna.nabatan_7asena.presentation.components.NoInternetState
import com.menna.nabatan_7asena.presentation.screens.home.component.PrayerTimesRow
import com.menna.nabatan_7asena.presentation.screens.home.component.TaskBubbleCard
import com.menna.nabatan_7asena.presentation.screens.home.component.TreasureChestCard
import com.menna.nabatan_7asena.ui.theme.SummerTheme
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
                    // ملحوظة تصميم: التوست الأصلي كان بأيقونتين (يمين وشمال) حوالين النص —
                    // ده مناسب لعنوان بارز في Dialog، لكن في التوست بيبقى صخب بصري زيادة.
                    // أيقونة واحدة سابقة للنص هي النمط الطبيعي لأي Snackbar احترافي.
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
                            modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)
                        )
                    }

                    item {
                        DailyWisdomCard(
                            wisdom = uiState.dailyWisdom,
                            modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)
                        )
                    }

                    // كان لو activitiesFlow يفشل (الأغلب: مفيش نت)، القسمين دول كانوا
                    // يفضلوا فاضيين تمامًا من غير أي تفسير للمستخدمة — مفيش صلوات
                    // وطالع رسالة "لسه مفيش مهام" وكأنها خلّصت مهامها، مش إن التحميل فشل.
                    if (uiState.hasLoadError && uiState.tasks.isEmpty()) {
                        item {
                            NoInternetState(
                                title = "معلش، مقدرناش نجيب مهام وصلوات النهارده",
                                onRetry = { viewModel.retryLoadingData() }
                            )
                        }
                    } else {
                        item {
                            HomeSectionHeader(
                                icon = R.drawable.time,
                                title = "الصلوات",
                                tint = sectionTitleColor
                            )
                        }

                        if (uiState.hasPrayerLoadError) {
                            // فشل جلب مواقيت الصلاة تحديدًا (بعد إصلاح الـ repository، ده
                            // بقى معزول عن باقي المهام) — نستبدل صف الصلوات بس، مش الشاشة كلها
                            item {
                                NoInternetState(
                                    title = "معلش، مقدرناش نجيب مواقيت الصلاة",
                                    onRetry = { viewModel.retryLoadingData() },
                                    compact = true,
                                    modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal)
                                )
                            }
                        } else {
                            item {
                                // من غير horizontal هنا عمدًا — الصف بياخد حافته من الـ
                                // contentPadding بتاع الپيجر نفسه (مش Modifier.padding من برّه)،
                                // عشان تأثير "بروز الكارت جنب حافة الشاشة" يشتغل صح
                                PrayerTimesRow(
                                    prayers = prayers,
                                    onPrayerClick = { viewModel.onTaskChecked(it) },
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }

                        item {
                            HomeSectionHeader(
                                icon = R.drawable.todo,
                                title = "المهام اليومية",
                                tint = sectionTitleColor
                            )
                        }

                        if (dailyTasks.isEmpty()) {
                            // حالة فارغة صريحة بدل ما تظهر عنوان "المهام اليومية" وبعده
                            // فراغ مفاجئ يوصل إحساس إن فيه حاجة متحملتش صح.
                            item {
                                EmptyDailyTasksHint(tint = sectionTitleColor)
                            }
                        } else {
                            items(items = dailyTasks, key = { it.id }) { item ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)
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
                                                else -> viewModel.onTaskChecked(item)
                                            }
                                        },
                                        onCheckClick = { viewModel.onTaskChecked(item) },
                                        onPlaySound = { viewModel.playTaskSound(item) }
                                    )
                                }
                            }
                        }

                        item {
                            Box(modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 12.dp)) {
                                val allTasksDone =
                                    dailyTasks.isNotEmpty() && dailyTasks.all { it.isCompleted }
                                val allPrayersDone =
                                    prayers.isNotEmpty() && prayers.all { it.isCompleted }
                                // بنبعت العدد الفعلي بدل ما نسيب الكارت من غير أي فكرة عن
                                // "قربتي قد إيه" — ده اللي بيغذّي شريط التقدّم وهي مقفولة
                                TreasureChestCard(
                                    isUnlocked = allTasksDone && allPrayersDone,
                                    completedCount = dailyTasks.count { it.isCompleted } + prayers.count { it.isCompleted },
                                    totalCount = dailyTasks.size + prayers.size,
                                    onClick = { viewModel.openDailyChallenge() }
                                )
                            }
                        }
                    } // نهاية else الخاصة بـ hasLoadError

                    item {
                        Box(modifier = Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)) {
                            Surface(
                                onClick = {
                                    showAddSheet = true
                                    viewModel.loadSuggestions()
                                },
                                shape = RoundedCornerShape(22.dp),
                                color = SummerTheme.colors.cardBackground,
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

/**
 * عنوان قسم موحّد (أيقونة + نص) — كان مكرر حرفيًا مرتين جوه HomeScreen
 * (فوق "الصلوات" وفوق "المهام اليومية"). استخراجه لمكان واحد يضمن إن أي
 * تعديل مستقبلي في التباعد أو الحجم يتطبق تلقائيًا على كل الأقسام،
 * ومينفعش قسم يتنسى تحديثه لوحده.
 */
@Composable
private fun HomeSectionHeader(
    icon: Int,
    title: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = title,
            style = SummerTheme.typography.sectionTitle,
            color = tint
        )
    }
}

@Composable
private fun EmptyDailyTasksHint(tint: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "لسه مفيش مهام إضافية النهارده \"إضافة عمل صالح جديد\" بالأسفل",
            style = SummerTheme.typography.taskSubtitle,
            color = tint.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
    }
}