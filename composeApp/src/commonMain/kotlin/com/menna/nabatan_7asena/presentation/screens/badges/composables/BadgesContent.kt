package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun BadgesContent(stats: BadgesUiState, onBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
        val colors = SummerTheme.colors

        val achievements = listOf(
            Achievement("وسام الفجر", "سجّل صلاة الفجر في ٣ أيام", "🌅", stats.fajrDays >= 3, colors.goldWarm),
            Achievement("نور القرآن", "أكمل أول جزء من القرآن", "📖", stats.quranPartsFinished >= 1, colors.leafGreen),
            Achievement("نجمة الأذكار", "سجّل ذكر الله في ٣ أيام", "📿", stats.azkarDays >= 3, colors.secondaryAccent),
            Achievement("ساقي الحديقة", "اسقِ شجرتك لأول مرة", "💧", stats.wateredGarden, colors.statGreenStart),
            Achievement("وسام الجمعة", "أكمل تحدّي الجمعة", "🏆", stats.finishedFriday, colors.goldWarm),
            Achievement("أسبوع من الخير", "داوم على عمل طيب ٧ أيام متتالية", "🌙", stats.currentStreak >= 7, colors.secondaryAccent),
            Achievement("حديقة النجوم", "اجمع ٥٠ نجمة", "⭐", stats.totalStars >= 50, colors.goldWarm),
            Achievement("ختمة القرآن", "أكمل ٣٠ جزءًا من القرآن", "👑", stats.quranPartsFinished >= 30, colors.highlightAccent)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            BadgesSummerBackground(particleAlphas)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 16.dp, bottom = 100.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(vertical = 8.dp)
                    ) {
                        TextButton(onClick = onBack) { Text("رجوع") }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "تقدّمي",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    SummerTreeCard(stats.totalStars)
                }

                item(span = { GridItemSpan(2) }) {
                    SummerStreakCard(stats.currentStreak)
                }

                item(span = { GridItemSpan(2) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp).height(20.dp)
                                .background(colors.goldWarm, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "الأوسمة والإنجازات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.textPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

                items(achievements) { badge ->
                    SummerBadgeItem(badge)
                }
            }
        }
    }
}
