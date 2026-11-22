package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.MemorizationStatus
import com.menna.nabatan_7asena.domain.entity.MemorizationStatus.*
import com.menna.nabatan_7asena.domain.entity.SurahMemorization
import com.menna.nabatan_7asena.presentation.screens.quran.MemorizationSurah
import com.menna.nabatan_7asena.presentation.screens.quran.QuranJourneyUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.roundToInt

private val juzOrder = (30 downTo 1).toList()
private fun juzLabel(juz: Int) = if (juz == 30) "جزء عمّ" else "الجزء $juz"

@Composable
internal fun MemorizationJourney(
    state: QuranJourneyUiState,
    catalog: List<MemorizationSurah>,
    onReview: (Int) -> Unit,
    onPractice: (Int) -> Unit,
    onUpdate: (Int, MemorizationStatus, Int, Int) -> Unit
) {
    var juz by rememberSaveable { mutableIntStateOf(30) }
    val progress = state.progress
    val memorized = catalog.filter { progress.memorization[it.surah.id]?.status == MEMORIZED }
    val juzSurahs = catalog.filter { juz in it.juzs }
    val juzMemorized = memorized.count { juz in it.juzs }
    val revision = if (memorized.isEmpty()) null else {
        memorized[state.today.toEpochDays().mod(memorized.size)].surah
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (revision != null) item {
            JourneyCard(SummerTheme.quran.cardActive) {
                Text("مراجعة اليوم", style = MaterialTheme.typography.titleLarge)
                Text("سورة ${revision.name}")
                JourneyButton("افتح السورة للمراجعة", onClick = { onReview(revision.id) })
            }
        }
        item {
            JourneyCard {
                JourneyProgress("حفظت ${memorized.size} من 114 سورة", memorized.size / 114f)
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(juzOrder) { number ->
                    FilterChip(selected = juz == number, onClick = { juz = number },
                        label = { Text(juzLabel(number)) })
                }
            }
        }
        item {
            JourneyCard {
                val fraction = if (juzSurahs.isEmpty()) 0f else juzMemorized.toFloat() / juzSurahs.size
                Text(juzLabel(juz), style = MaterialTheme.typography.titleMedium)
                JourneyProgress("حفظت $juzMemorized من ${juzSurahs.size} سورة", fraction)
            }
        }
        items(juzSurahs, key = { it.surah.id }) { entry ->
            val id = entry.surah.id
            SurahMemorizationCard(
                entry = entry,
                progress = progress.memorization[id] ?: SurahMemorization(),
                onPractice = { onPractice(id) },
                onUpdate = { status, ayahs -> onUpdate(id, status, ayahs, entry.ayahCount) }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SurahMemorizationCard(
    entry: MemorizationSurah,
    progress: SurahMemorization,
    onPractice: () -> Unit,
    onUpdate: (MemorizationStatus, Int) -> Unit
) {
    val colors = SummerTheme.colors
    val base = SummerTheme.quran.card
    val tint = when (progress.status) {
        NOT_STARTED -> base
        IN_PROGRESS -> lerp(base, colors.goldWarm, 0.19f)
        MEMORIZED -> lerp(base, colors.statGreenStart, 0.28f)
    }
    JourneyCard(tint) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("سورة ${entry.surah.name}", style = MaterialTheme.typography.titleMedium)
            if (progress.status == MEMORIZED) Text("⭐")
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MemorizationStatus.entries.forEach { status ->
                FilterChip(
                    selected = progress.status == status,
                    onClick = { onUpdate(status, progress.ayahs) },
                    label = {
                        Text(when (status) {
                            NOT_STARTED -> "لم أبدأ"
                            IN_PROGRESS -> "جاري الحفظ"
                            MEMORIZED -> "تم الحفظ"
                        })
                    }
                )
            }
        }
        if (progress.status == IN_PROGRESS) {
            var ayahs by remember(progress.ayahs) { mutableIntStateOf(progress.ayahs) }
            Text("حفظت $ayahs من ${entry.ayahCount} آية")
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { onUpdate(IN_PROGRESS, ayahs - 1) }, enabled = ayahs > 0) {
                    Text("−")
                }
                Slider(
                    value = ayahs.toFloat(),
                    onValueChange = { ayahs = it.roundToInt() },
                    onValueChangeFinished = { onUpdate(IN_PROGRESS, ayahs) },
                    valueRange = 0f..entry.ayahCount.toFloat(),
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onUpdate(IN_PROGRESS, ayahs + 1) }, enabled = ayahs < entry.ayahCount) {
                    Text("+")
                }
            }
            JourneyButton("تكرار وحفظ", onClick = onPractice)
        }
    }
}
