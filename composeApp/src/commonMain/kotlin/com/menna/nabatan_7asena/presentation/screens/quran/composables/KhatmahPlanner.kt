package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.menna.nabatan_7asena.domain.entity.KHATMAH_BONUS_STARS
import com.menna.nabatan_7asena.domain.entity.MUSHAF_PAGE_COUNT
import com.menna.nabatan_7asena.domain.entity.Wird
import com.menna.nabatan_7asena.domain.entity.WirdGoal
import com.menna.nabatan_7asena.presentation.screens.quran.QuranJourneyUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun KhatmahPlanner(
    state: QuranJourneyUiState,
    onStart: (WirdGoal) -> Unit,
    onRead: (Int) -> Unit,
    onComplete: (Wird) -> Unit
) {
    val journey = state.progress
    val current = journey.activeKhatmah
    val completedToday = journey.lastWirdDate == state.today
    val wird = journey.wirdFor(state.today)
    var goal by rememberSaveable { mutableStateOf(WirdGoal.ONE) }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (journey.completedKhatmahs > 0) item {
            JourneyCard { Text("الختمات المكتملة: ${journey.completedKhatmahs}") }
        }
        if (current == null) {
            item {
                JourneyCard {
                    Text("ختمة جديدة", style = MaterialTheme.typography.titleLarge)
                    Text("اختر وردك اليومي لقراءة 604 صفحات.")
                }
            }
            items(WirdGoal.entries) { option ->
                FilterChip(
                    selected = goal == option,
                    onClick = { goal = option },
                    label = { Text(option.label) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SummerTheme.quran.card,
                        selectedContainerColor = SummerTheme.quran.cardActive
                    )
                )
            }
            item {
                JourneyCard {
                    Text("المدة المتوقعة: ${goal.estimatedDays} يوم")
                    JourneyButton("ابدأ ختمة جديدة", onClick = { onStart(goal) })
                }
            }
        } else {
            item {
                JourneyCard {
                    val fraction = current.pagesRead.toFloat() / MUSHAF_PAGE_COUNT
                    Text("تقدّم الختمة", style = MaterialTheme.typography.titleLarge)
                    JourneyProgress("قرأت ${current.pagesRead} من $MUSHAF_PAGE_COUNT صفحة · ${(fraction * 100).toInt()}٪", fraction)
                    Text(current.goal.label, color = SummerTheme.quran.textMuted)
                }
            }
        }
        if (wird != null) item {
            JourneyCard {
                Text("ورد اليوم: من صفحة ${wird.firstPage} إلى ${wird.lastPage}",
                    style = MaterialTheme.typography.titleMedium)
                JourneyButton("اقرأ ورد اليوم", onClick = { onRead(wird.firstPage) })
                if (completedToday) {
                    Text("أتممت ورد اليوم ⭐")
                    if (current?.pagesRead == 0) Text("أول ورد في الختمة الجديدة غداً.")
                } else {
                    OutlinedButton(onClick = { onComplete(wird) }, modifier = Modifier.fillMaxWidth()) {
                        Text("أتممت ورد اليوم")
                    }
                }
            }
        }
    }
}

@Composable
internal fun KhatmahCelebrationDialog(onDismiss: () -> Unit, onStartNew: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = SummerTheme.quran.drawer,
            icon = { Text("✨📖✨") },
            title = { Text("مبارك ختم القرآن!") },
            text = { Text("أتممت قراءة 604 صفحات من القرآن الكريم، وحصلت على $KHATMAH_BONUS_STARS نجمة إضافية!") },
            confirmButton = { TextButton(onClick = onStartNew) { Text("ختمة جديدة") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("لاحقاً") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranJourneyDialog(
    title: String,
    onClose: () -> Unit,
    snackbar: SnackbarHostState,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = SummerTheme.quran.backgroundMid,
                topBar = {
                    TopAppBar(
                        title = { Text(title, color = SummerTheme.quran.textPrimary) },
                        navigationIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "العودة إلى المصحف")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = SummerTheme.quran.topBar)
                    )
                },
                snackbarHost = { SnackbarHost(snackbar) }
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    QuranBackground()
                    content()
                }
            }
        }
    }
}

@Composable
internal fun JourneyCard(
    color: androidx.compose.ui.graphics.Color = SummerTheme.quran.card,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SummerTheme.Shapes.MediumRounded,
        color = color,
        contentColor = SummerTheme.quran.textPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, SummerTheme.quran.border)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
internal fun JourneyButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = SummerTheme.quran.accentStrong,
            contentColor = SummerTheme.quran.backgroundTop
        )
    ) { Text(label) }
}

@Composable
internal fun JourneyProgress(label: String, progress: Float) {
    Text(label)
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
        color = SummerTheme.quran.accentStrong,
        trackColor = SummerTheme.quran.accent.copy(alpha = 0.22f)
    )
}
