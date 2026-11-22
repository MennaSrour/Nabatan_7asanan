@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.menna.nabatan_7asena.core.util.PrayerTimeline
import com.menna.nabatan_7asena.domain.entity.TaskCategory
import com.menna.nabatan_7asena.presentation.screens.home.*
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

@Composable
internal fun NextPrayerCountdownCard(state: HomeUiState) {
    val date = state.prayerDate ?: return
    val prayers = remember(state.tasks) { state.tasks.filter { it.category == TaskCategory.PRAYER }.mapNotNull {
        PrayerTimeline.parseMinutes(it.subtitle)?.let { minutes -> CountdownPrayer(it.title, minutes) }
    } }
    val fajr = stringResource(R.string.countdown_fajr)
    val now = rememberPrayerClock()
    val countdown = nextPrayerCountdown(prayers, date, state.previousIshaMinutes, state.tomorrowFajrMinutes, fajr, now, TimeZone.currentSystemDefault())
    Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), shape = RoundedCornerShape(24.dp),
        color = SummerTheme.colors.dialogBackground, border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.6f)), shadowElevation = 3.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CountdownHeading(countdown)
            if (countdown != null) {
                PrayerCountdownTimeline(countdown)
                if (countdown.elapsedFraction == null) Text(stringResource(R.string.countdown_interval_missing),
                    style = MaterialTheme.typography.bodySmall, color = SummerTheme.colors.textBrown)
            }
        }
    }
}

@Composable
private fun rememberPrayerClock(): kotlin.time.Instant {
    var now by remember { mutableStateOf(Clock.System.now()) }
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                now = Clock.System.now()
                delay(1000L - now.toEpochMilliseconds().mod(1000L))
            }
        }
    }
    return now
}

@Composable
private fun CountdownHeading(countdown: PrayerCountdown?) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OriginalPrayerIcon(countdown?.name.orEmpty(), Modifier.size(48.dp))
        Column(Modifier.weight(1f)) {
            Text(if (countdown == null) stringResource(R.string.countdown_waiting)
                else stringResource(R.string.countdown_until, countdown.name),
                style = SummerTheme.typography.chestSubtitle, color = SummerTheme.colors.textBrown)
            Text(countdown?.let { formatPrayerCountdown(it.secondsRemaining) } ?: stringResource(R.string.countdown_missing_times),
                style = MaterialTheme.typography.titleLarge.copy(textDirection = if (countdown == null) TextDirection.Rtl else TextDirection.Ltr),
                fontWeight = FontWeight.ExtraBold, fontSize = if (countdown == null) 13.sp else 26.sp,
                color = SummerTheme.colors.highlightAccent)
        }
    }
}

@Composable
private fun PrayerCountdownTimeline(countdown: PrayerCountdown) {
    val fraction = countdown.elapsedFraction ?: 0f
    val progressDescription = if (countdown.elapsedFraction == null) stringResource(R.string.countdown_interval_missing)
        else stringResource(R.string.countdown_progress, (fraction * 100).toInt())
    BoxWithConstraints(Modifier.fillMaxWidth().height(32.dp).semantics {
        contentDescription = progressDescription
        if (countdown.elapsedFraction != null) progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
    }) {
        Box(Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 16.dp).height(12.dp)
            .clip(RoundedCornerShape(50)).background(SummerTheme.colors.highlightAccent.copy(0.12f))) {
            Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(SummerTheme.colors.goldWarm))
        }
        OriginalPrayerIcon(countdown.name, Modifier.align(Alignment.CenterStart)
            .offset(x = (maxWidth - 32.dp).coerceAtLeast(0.dp) * fraction).size(32.dp))
    }
}
