package com.menna.nabatan_7asena.presentation.screens.prayerguide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.resources.AppResources as R
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.domain.entity.PRAYER_GUIDE_BONUS_STARS
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val Cream = Color(0xFFFFF9EC)
private val Lavender = Color(0xFFEEE6FA)
private val Purple = Color(0xFF614581)
private val Ink = Color(0xFF37264C)
private val SoftGold = Color(0xFFFFE8AE)

@Composable
fun PrayerGuideScreen(onBack: () -> Unit, awardBonus: suspend () -> Boolean) {
    var prayerTab by rememberSaveable { mutableStateOf(false) }
    var wuduIndex by rememberSaveable { mutableIntStateOf(0) }
    var prayerIndex by rememberSaveable { mutableIntStateOf(0) }
    var wuduReviewed by rememberSaveable { mutableIntStateOf(0) }
    var prayerReviewed by rememberSaveable { mutableIntStateOf(0) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }
    var saved by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val steps = if (prayerTab) prayerSteps else wuduSteps
    val index = if (prayerTab) prayerIndex else wuduIndex
    val reviewed = if (prayerTab) prayerReviewed else wuduReviewed
    val complete = wuduReviewed == wuduSteps.size && prayerReviewed == prayerSteps.size
    fun showStep(step: Int) {
        if (prayerTab) prayerIndex = step else wuduIndex = step
    }
    fun claimBonus() {
        if (saving || saved) return
        saving = true
        scope.launch {
            try {
                val granted = awardBonus()
                feedback = if (granted) "أضفت إلى حصّالتك ${arabicIndic(PRAYER_GUIDE_BONUS_STARS)} نجوم"
                    else "نجوم الدليل لهذا اليوم في حصّالتك بالفعل!"
                saved = true
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                feedback = "لم تُحفظ النجوم بعد. جرّب مرة أخرى."
            } finally {
                saving = false
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (complete) {
            PrayerGuideCelebration(feedback, saved, saving, onRetry = ::claimBonus, onFinish = onBack)
            return@CompositionLocalProvider
        }
        val scroll = rememberScrollState()
        LaunchedEffect(prayerTab, index) { scroll.scrollTo(0) }
        Column(
            Modifier.fillMaxSize().background(Cream).systemBarsPadding().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("رجوع", color = Purple) }
                Text("دليل الوضوء والصلاة", color = Ink, style = MaterialTheme.typography.headlineSmall)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GuideTab("الوضوء", !prayerTab, Modifier.weight(1f)) { prayerTab = false }
                GuideTab("الصلاة", prayerTab, Modifier.weight(1f)) { prayerTab = true }
            }
            StepChecklist(steps, index, reviewed, onSelect = ::showStep)
            Column(
                Modifier.weight(1f).verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StepCard(steps[index], index, steps.size)
                if (prayerTab) PrayerSummary()
            }
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { showStep(index - 1) }, enabled = index > 0) { Text("السابقة") }
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White),
                    onClick = {
                        val nextReviewed = maxOf(reviewed, index + 1)
                        if (prayerTab) prayerReviewed = nextReviewed else wuduReviewed = nextReviewed
                        if (index < steps.lastIndex) {
                            showStep(index + 1)
                        } else if (wuduReviewed == wuduSteps.size && prayerReviewed == prayerSteps.size) {
                            claimBonus()
                        } else {
                            prayerTab = !prayerTab
                        }
                    }
                ) { Text(if (index < steps.lastIndex) "الخطوة التالية" else "أكملت الخطوات") }
            }
        }
    }
}

@Composable
private fun PrayerGuideCelebration(
    feedback: String?,
    saved: Boolean,
    saving: Boolean,
    onRetry: () -> Unit,
    onFinish: () -> Unit
) {
    val confetti by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
    BoxWithConstraints(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Lavender, Cream))).systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        val artworkHeight = (maxHeight * 0.32f).coerceAtMost(190.dp)
        Column(
            Modifier.widthIn(max = 480.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .background(Cream, RoundedCornerShape(30.dp))
                .border(2.dp, SoftGold, RoundedCornerShape(30.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LottieAnimation(confetti, Modifier.fillMaxWidth().height(artworkHeight), iterations = 1)
            Text("أحسنت", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Purple)
            Text("أكملت خطوات الوضوء والصلاة", style = MaterialTheme.typography.titleLarge, color = Ink, textAlign = TextAlign.Center)
            if (saving) Text("جارٍ حفظ النجوم", color = Ink)
            feedback?.let { Text(it, color = Ink, textAlign = TextAlign.Center) }
            if (!saved && !saving) TextButton(onClick = onRetry) { Text("حفظ النجوم", color = Purple) }
            ChunkyButton(onClick = onFinish, enabled = !saving, color = SoftGold, edgeColor = Color(0xFFDDBA67)) {
                Text("إنهاء", color = Purple, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepChecklist(steps: List<GuideStep>, index: Int, reviewed: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        steps.forEachIndexed { stepIndex, step ->
            val complete = stepIndex < reviewed
            FilterChip(
                selected = stepIndex == index,
                enabled = stepIndex <= reviewed,
                onClick = { onSelect(stepIndex) },
                label = { Text(if (complete) "✓" else arabicIndic(stepIndex + 1)) },
                modifier = Modifier.semantics {
                    val status = if (complete) "، تمت المراجعة" else ""
                    contentDescription = "الخطوة ${arabicIndic(stepIndex + 1)}، ${step.title}$status"
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (complete) SoftGold else Color.White,
                    labelColor = Ink, selectedContainerColor = Purple, selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun GuideTab(title: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text(title) } },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White, labelColor = Ink,
            selectedContainerColor = Purple, selectedLabelColor = Color.White
        ),
        modifier = modifier.heightIn(min = 48.dp)
    )
}

@Composable
private fun StepCard(step: GuideStep, index: Int, total: Int) {
    Column(
        Modifier.fillMaxWidth().background(Lavender, RoundedCornerShape(24.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val frame = RoundedCornerShape(20.dp)
        val back = if (step.scene.isWudu) listOf(Color(0xFFE4F8FF), Color(0xFFCBF0E6))
            else listOf(Color(0xFFF7EFFF), Color(0xFFFFEBC6))
        GuideArtwork(
            step.scene,
            Modifier.fillMaxWidth().height(160.dp)
                .background(Brush.verticalGradient(back), frame)
                .border(2.dp, Color.White, frame)
                .padding(8.dp)
        )
        Text("الخطوة ${arabicIndic(index + 1)} من ${arabicIndic(total)}", color = Purple)
        Text(step.title, color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(step.instruction, color = Ink, style = MaterialTheme.typography.bodyLarge)
        step.recitation?.let {
            Text(
                it,
                modifier = Modifier.fillMaxWidth().background(SoftGold, RoundedCornerShape(16.dp)).padding(12.dp),
                color = Ink, style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PrayerSummary() {
    Column(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("ركعات الصلوات المفروضة", color = Purple, fontWeight = FontWeight.Bold)
        listOf("الفجر" to "٢", "الظهر" to "٤", "العصر" to "٤", "المغرب" to "٣", "العشاء" to "٤").forEach { (prayer, count) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(prayer, color = Ink)
                Text(count, color = Ink)
            }
        }
    }
}
