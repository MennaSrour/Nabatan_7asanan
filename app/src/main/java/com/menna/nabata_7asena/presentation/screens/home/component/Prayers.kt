package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabata_7asena.presentation.screens.home.HomeUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PRAYER_ORDER = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")

private data class PrayerSky(val gradient: List<Color>, val labelAccent: Color)

private val PRAYER_SKIES = mapOf(
    "الفجر" to PrayerSky(
        gradient = listOf(Color(0xFF1E2A5C), Color(0xFF4B4F92), Color(0xFFB088A8)),
        labelAccent = Color(0xFF4B3F86)
    ),
    "الظهر" to PrayerSky(
        gradient = listOf(Color(0xFF1E90FF), Color(0xFF63C7FF)),
        labelAccent = Color(0xFF1565C0)
    ),
    "العصر" to PrayerSky(
        gradient = listOf(Color(0xFF5AA9D6), Color(0xFFF0C77A)),
        labelAccent = Color(0xFFC98A2E)
    ),
    "المغرب" to PrayerSky(
        gradient = listOf(Color(0xFFFFB25E), Color(0xFFFF7A6B), Color(0xFF7A4E9E)),
        labelAccent = Color(0xFFD1476B)
    ),
    "العشاء" to PrayerSky(
        gradient = listOf(Color(0xFF1E1140), Color(0xFF4B2E7E)),
        labelAccent = Color(0xFF3A2266)
    )
)

private fun defaultSky(name: String) = PRAYER_SKIES[name] ?: PrayerSky(
    gradient = listOf(Color(0xFF64748B), Color(0xFF94A3B8)),
    labelAccent = Color(0xFF475569)
)

private fun prayerIconRes(name: String) = when (name) {
    "الفجر" -> "fajr"
    "الظهر" -> "zuhr_sun"
    "العصر" -> "asr_sun"
    "المغرب" -> "maghrib"
    "العشاء" -> "ishaa"
    else -> ""
}

private fun parseTimeToMinutes(timeStr: String?): Int? {
    if (timeStr.isNullOrBlank()) return null
    val trimmed = timeStr.trim()
    val cal = try {
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        sdf.isLenient = false
        Calendar.getInstance().apply { time = sdf.parse(trimmed) ?: return@apply }
    } catch (e: Exception) {
        try {
            val sdf = SimpleDateFormat("h:mm a", Locale.US)
            Calendar.getInstance().apply { time = sdf.parse(trimmed.uppercase()) ?: return null }
        } catch (e2: Exception) {
            return null
        }
    }
    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
}

private fun currentMinutesOfDay(): Int {
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
}

@Composable
fun PrayerTimesRow(
    prayers: List<HomeUiState.UiTaskItem>,
    onPrayerClick: (HomeUiState.UiTaskItem) -> Unit,
    modifier: Modifier = Modifier
) {

    val ordered = remember(prayers) {
        prayers.sortedBy { p ->
            PRAYER_ORDER.indexOf(p.title).let { if (it == -1) Int.MAX_VALUE else it }
        }
    }

    val nowMinutes = currentMinutesOfDay()

    val nextIndex = remember(ordered, nowMinutes) {
        val idx = ordered.indexOfFirst { p -> (parseTimeToMinutes(p.subtitle) ?: -1) > nowMinutes }
        if (idx == -1) 0 else idx
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        items(items = ordered, key = { it.id }) { prayer ->
            val isNext = ordered.indexOf(prayer) == nextIndex
            PrayerCard(
                prayer = prayer,
                isNext = isNext,
                onClick = { onPrayerClick(prayer) }
            )
        }
    }
}

@Composable
private fun PrayerCard(
    prayer: HomeUiState.UiTaskItem,
    isNext: Boolean,
    onClick: () -> Unit
) {
    val sky = remember(prayer.title) { defaultSky(prayer.title) }

    val cardWidth by animateDpAsState(if (isNext) 92.dp else 76.dp, tween(450), label = "cardWidth")
    val cardHeight by animateDpAsState(
        if (isNext) 158.dp else 138.dp,
        tween(450),
        label = "cardHeight"
    )

    val baseGradient = if (prayer.isCompleted) {
        sky.gradient.map { c -> lerp(c, Color(0xFFE9E7F0), 0.55f) }
    } else {
        sky.gradient
    }

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
    ) {
        if (isNext) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .blur(10.dp)
                    .background(Color.White.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(baseGradient))
                .then(
                    if (isNext) {
                        Modifier.background(Color.White.copy(alpha = 0.12f))
                    } else Modifier
                )
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(if (isNext) 34.dp else 28.dp),
                contentAlignment = Alignment.Center
            ) {
                ClayAssetImage(
                    resName = prayerIconRes(prayer.title),
                    fallbackIcon = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = prayer.subtitle ?: "",
                color = Color.White,
                fontSize = if (isNext) 14.sp else 13.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = prayer.title,
                color = Color.White,
                fontSize = if (isNext) 13.5.sp else 12.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(6.dp))
            if (isNext && !prayer.isCompleted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "القادمة",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = sky.labelAccent
                    )
                }
            } else {
                Spacer(Modifier.height(16.dp))
            }
        }

        if (prayer.isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.28f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isNext) 46.dp else 38.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "تمت",
                        tint = sky.labelAccent,
                        modifier = Modifier.size(if (isNext) 26.dp else 20.dp)
                    )
                }
            }
        }
    }
}