package com.menna.nabatan_7asena.presentation.screens.seasonal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan.shared.generated.resources.*
import com.menna.nabatan_7asena.core.util.RamadanSeason
import com.menna.nabatan_7asena.domain.entity.RAMADAN_CHALLENGE_STARS
import com.menna.nabatan_7asena.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal const val RAMADAN_CALENDAR_ROUTE = "ramadan-calendar"

@Composable
internal fun RamadanCalendarScreen(season: RamadanSeason?, viewModel: HomeViewModel, onBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(RamadanLavender, Color(0xFF20162F))))
                .safeDrawingPadding()
        ) {
            Row(Modifier.padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(Res.string.ramadan_back), tint = RamadanGold)
                }
                Text(stringResource(Res.string.ramadan_calendar_title),
                    color = RamadanGold, style = MaterialTheme.typography.headlineSmall)
            }
            if (season == null) {
                Text(stringResource(Res.string.ramadan_outside_season), Modifier.padding(20.dp), color = Color.White)
            } else {
                val profileId by viewModel.ramadanProfileIds.collectAsStateWithLifecycle(initialValue = "")
                key(season.year, season.isPreview, profileId) {
                    val progress = remember(season.year, season.isPreview) { viewModel.ramadanCalendar.observe(profileId, season) }
                    val completed by progress.collectAsStateWithLifecycle(initialValue = emptySet())
                    CalendarContent(season, completed, profileReady = profileId.isNotBlank() && profileId != "local") { day ->
                        viewModel.ramadanCalendar.complete(profileId, season, day)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarContent(
    season: RamadanSeason,
    completed: Set<Int>,
    profileReady: Boolean,
    onComplete: suspend (Int) -> Unit
) {
    var selectedDay by rememberSaveable { mutableStateOf<Int?>(null) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Text(
        stringResource(Res.string.ramadan_progress, completed.size.arabicDigits(),
            (completed.size * RAMADAN_CHALLENGE_STARS).arabicDigits()),
        Modifier.padding(horizontal = 20.dp, vertical = 6.dp), color = Color.White
    )
    if (season.isPreview) {
        Text(stringResource(Res.string.ramadan_preview), Modifier.padding(horizontal = 20.dp), color = RamadanGold)
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(55.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(30, key = { it + 1 }) { index ->
            val day = index + 1
            LanternDoor(day, day in completed, day <= season.unlockedThrough) {
                error = false
                selectedDay = day
            }
        }
    }
    selectedDay?.let { day ->
        val done = day in completed
        val unlocked = day <= season.unlockedThrough
        AlertDialog(
            onDismissRequest = { if (!saving) selectedDay = null },
            title = { Text(stringResource(Res.string.ramadan_day, day.arabicDigits())) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(if (unlocked) RamadanDeeds[day - 1] else Res.string.ramadan_locked))
                    if (done) Text(stringResource(Res.string.ramadan_completed))
                    if (error) Text(stringResource(Res.string.ramadan_save_error), color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                if (unlocked && !done) {
                    Button(
                        enabled = profileReady && !saving,
                        onClick = {
                            saving = true
                            error = false
                            scope.launch {
                                try {
                                    onComplete(day)
                                    selectedDay = null
                                } catch (failure: CancellationException) {
                                    throw failure
                                } catch (failure: Exception) {
                                    error = true
                                } finally {
                                    saving = false
                                }
                            }
                        }
                    ) { Text(stringResource(Res.string.ramadan_complete)) }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDay = null }, enabled = !saving) {
                    Text(stringResource(Res.string.ramadan_close))
                }
            }
        )
    }
}

@Composable
private fun LanternDoor(day: Int, completed: Boolean, unlocked: Boolean, onClick: () -> Unit) {
    val glow by animateFloatAsState(if (completed) 1f else 0f, tween(650), label = "lantern glow")
    val background by animateColorAsState(
        if (completed) Color(0xFF765123) else if (unlocked) Color(0xFF5A3F78) else Color(0xFF352A49),
        tween(650), label = "lantern door"
    )
    val label = stringResource(
        when {
            completed -> Res.string.ramadan_completed
            unlocked -> Res.string.ramadan_available_label
            else -> Res.string.ramadan_locked_label
        }
    )
    Surface(
        onClick = onClick,
        modifier = Modifier.height(82.dp).semantics { stateDescription = label },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 10.dp, bottomEnd = 10.dp),
        color = background,
        contentColor = if (unlocked) RamadanGold else Color(0xFFD0BEDA)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.matchParentSize()) {
                if (glow > 0f) {
                    drawCircle(Brush.radialGradient(listOf(RamadanGold.copy(alpha = 0.7f * glow), Color.Transparent),
                        center = Offset(size.width / 2, size.height * 0.4f), radius = size.height * 0.56f),
                        radius = size.height * 0.56f, center = Offset(size.width / 2, size.height * 0.4f))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (unlocked) {
                    Canvas(Modifier.size(25.dp)) {
                        val color = if (completed) RamadanGold else Color(0xFFE3B67A)
                        val w = size.width
                        val h = size.height
                        drawCircle(color, radius = w * 0.11f, center = Offset(w * 0.5f, h * 0.13f))
                        drawRoundRect(color, Offset(w * 0.23f, h * 0.24f), Size(w * 0.54f, h * 0.59f), CornerRadius(w * 0.14f))
                        drawLine(color, Offset(w * 0.18f, h * 0.88f), Offset(w * 0.82f, h * 0.88f), 2.dp.toPx())
                        drawLine(background, Offset(w * 0.5f, h * 0.37f), Offset(w * 0.5f, h * 0.71f), 1.5.dp.toPx())
                    }
                } else {
                    Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                Text(day.arabicDigits(), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

private fun Int.arabicDigits(): String = toString().map { digit -> '٠' + digit.digitToInt() }.joinToString("")

private val RamadanDeeds = listOf(
    Res.string.ramadan_deed_01, Res.string.ramadan_deed_02, Res.string.ramadan_deed_03,
    Res.string.ramadan_deed_04, Res.string.ramadan_deed_05, Res.string.ramadan_deed_06,
    Res.string.ramadan_deed_07, Res.string.ramadan_deed_08, Res.string.ramadan_deed_09,
    Res.string.ramadan_deed_10, Res.string.ramadan_deed_11, Res.string.ramadan_deed_12,
    Res.string.ramadan_deed_13, Res.string.ramadan_deed_14, Res.string.ramadan_deed_15,
    Res.string.ramadan_deed_16, Res.string.ramadan_deed_17, Res.string.ramadan_deed_18,
    Res.string.ramadan_deed_19, Res.string.ramadan_deed_20, Res.string.ramadan_deed_21,
    Res.string.ramadan_deed_22, Res.string.ramadan_deed_23, Res.string.ramadan_deed_24,
    Res.string.ramadan_deed_25, Res.string.ramadan_deed_26, Res.string.ramadan_deed_27,
    Res.string.ramadan_deed_28, Res.string.ramadan_deed_29, Res.string.ramadan_deed_30
)
