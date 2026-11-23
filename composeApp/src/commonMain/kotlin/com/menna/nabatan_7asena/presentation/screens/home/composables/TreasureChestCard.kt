package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

internal data class TreasureProgress(val completed: Int, val total: Int) {
    val remaining get() = total - completed
    val fraction get() = if (total > 0) completed.toFloat() / total else 0f
}

internal fun treasureProgress(completed: Int, total: Int): TreasureProgress {
    val safeTotal = total.coerceAtLeast(0)
    return TreasureProgress(completed.coerceIn(0, safeTotal), safeTotal)
}

@Composable
fun TreasureChestCard(isUnlocked: Boolean, completedCount: Int = 0, totalCount: Int = 0, onClick: () -> Unit) {
    val progress = treasureProgress(completedCount, totalCount)
    val shape = RoundedCornerShape(28.dp)
    val colors = SummerTheme.colors
    val openLabel = if (isUnlocked) "لف العجلة الآن" else "لف العجلة"
    Box(Modifier.fillMaxWidth().padding(bottom = 7.dp)) {
        Box(Modifier.matchParentSize().offset(y = 7.dp).clip(shape).background(colors.goldDeep))
        Column(Modifier.fillMaxWidth().clip(shape)
            .background(Brush.verticalGradient(listOf(colors.dialogBackground, colors.goldWarm.copy(alpha = 0.55f))))
            .border(2.dp, colors.goldDeep.copy(alpha = 0.65f), shape)
            .clickable(role = Role.Button, onClickLabel = if (isUnlocked) openLabel else null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("عجلة هدية اليوم", style = SummerTheme.typography.chestSubtitle, color = colors.textBrown)
            DailyWheelIllustration()
            TreasureChestPrompt(isUnlocked, progress)
            if (progress.total > 0) TreasureChestProgress(progress)
            Text(if (isUnlocked) "لف العجلة الآن" else "لف العجلة",
                style = SummerTheme.typography.chestSubtitle, color = colors.textDarkBrown,
                textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun DailyWheelIllustration() {
    val colors = SummerTheme.colors
    Canvas(Modifier.size(72.dp)) {
        val radius = size.minDimension / 2
        val origin = androidx.compose.ui.geometry.Offset(
            (size.width - radius * 2) / 2,
            (size.height - radius * 2) / 2
        )
        val wheelSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        val slices = listOf(
            colors.wisdomAccent, colors.goldWarm, colors.successGreen,
            colors.secondaryAccent, colors.goldDeep, colors.wisdomStart
        )
        slices.forEachIndexed { index, color ->
            drawArc(color, -90f + index * 60f, 60f, true, origin, wheelSize)
        }
        drawCircle(colors.dialogBackground, radius * .2f)
        drawCircle(colors.goldDeep, radius, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
        drawLine(
            colors.textDarkBrown,
            androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2),
            androidx.compose.ui.geometry.Offset(size.width / 2, size.height * .08f),
            strokeWidth = 2.dp.toPx()
        )
        drawCircle(colors.textDarkBrown, radius * .07f)
    }
}

@Composable
private fun TreasureChestPrompt(isUnlocked: Boolean, progress: TreasureProgress) {
    Text(if (isUnlocked) genderedStringResource(GenderedStrings.treasureUnlocked)
        else genderedStringResource(GenderedStrings.treasureCompleteTasks),
        style = SummerTheme.typography.chestTitle, color = SummerTheme.colors.textDarkBrown, textAlign = TextAlign.Center)
    if (!isUnlocked) Text(
        if (progress.remaining > 0) genderedStringResource(GenderedStrings.treasureRemaining, progress.remaining)
        else stringResource(R.string.treasure_waiting_tasks),
        style = SummerTheme.typography.chestSubtitle, color = SummerTheme.colors.textBrown,
        textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun TreasureChestProgress(progress: TreasureProgress) {
    val animatedProgress by animateFloatAsState(progress.fraction, label = "treasureProgress")
    Text(genderedStringResource(GenderedStrings.treasureProgress, progress.completed, progress.total),
        style = SummerTheme.typography.chestSubtitle, color = SummerTheme.colors.textDarkBrown,
        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))
    LinearProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
        color = SummerTheme.colors.goldDeep, trackColor = SummerTheme.colors.textBrown.copy(alpha = 0.12f))
}
