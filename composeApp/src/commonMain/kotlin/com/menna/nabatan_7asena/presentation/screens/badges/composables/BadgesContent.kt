package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.components.TrophyArtwork
import com.menna.nabatan_7asena.presentation.screens.badges.BADGES_TITLE
import com.menna.nabatan_7asena.presentation.screens.badges.Badge
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesUiState
import com.menna.nabatan_7asena.presentation.screens.badges.badgeCountLabel
import com.menna.nabatan_7asena.presentation.screens.badges.unlockedCount

@Composable
fun BadgesContent(stats: BadgesUiState, onBack: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val (earned, waiting) = Badge.entries.partition { it.isUnlocked(stats) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF1E7FF), Color(0xFFFFF7E1))))
                .systemBarsPadding()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 22.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onBack) { Text("رجوع", color = TrophyInk) }
                        Text(BADGES_TITLE, color = TrophyInk, style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold)
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { TrophyBanner(stats) }
                if (earned.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { ShowcaseHeading("أوسمتي المكتسبة") }
                    items(earned, key = { it.name }) { badge ->
                        SummerBadgeItem(badge, stats) { selected = badge.name }
                    }
                }
                if (waiting.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { ShowcaseHeading("أوسمة تنتظرك") }
                    items(waiting, key = { it.name }) { badge ->
                        SummerBadgeItem(badge, stats) { selected = badge.name }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { SummerTreeCard(stats.totalStars) }
                item(span = { GridItemSpan(maxLineSpan) }) { SummerStreakCard(stats.currentStreak) }
            }
        }
        Badge.entries.firstOrNull { it.name == selected }?.let { badge ->
            BadgeDialog(badge, stats) { selected = null }
        }
    }
}

@Composable
private fun ShowcaseHeading(title: String) {
    Text(title, color = TrophyInk, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun TrophyBanner(stats: BadgesUiState) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(Color(0xFFFFDB76), Color(0xFFFFECCD), Color(0xFFDCC6FA))), shape)
            .border(2.dp, Color(0xFFFFF6D7), shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TrophyArtwork(Modifier.size(88.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("خزانة الأوسمة", color = TrophyInk, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold)
            Text(stats.badgeCountLabel(), color = TrophyInk, style = MaterialTheme.typography.bodyLarge)
            MedalProgress(stats.unlockedCount().toFloat() / Badge.entries.size, Color(0xFF8753BC), Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BadgeDialog(badge: Badge, stats: BadgesUiState, onDismiss: () -> Unit) {
    val earned = badge.isUnlocked(stats)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFF8E9),
        icon = { MedalArtwork(badge, earned, Modifier.size(124.dp)) },
        title = { Text(badge.title, color = TrophyInk, textAlign = TextAlign.Center) },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(if (earned) badge.earnedMessage else badge.goal, color = TrophyInk, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold)
                if (!earned) MedalProgress(badge.fraction(stats), badge.tint, Modifier.height(10.dp))
                Text(
                    if (earned) "ما شاء الله! أضفت وسامًا لامعًا إلى خزانة جوائزك!"
                    else "كل خطوة صغيرة تقرّبك من هذا الوسام!",
                    color = TrophyInk, textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("رائع!", color = TrophyInk) } }
    )
}
