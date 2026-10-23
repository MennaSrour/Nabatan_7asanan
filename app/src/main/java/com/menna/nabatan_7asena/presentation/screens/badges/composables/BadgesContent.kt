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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.badges.BadgesUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun BadgesContent(stats: BadgesUiState) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
        val colors = SummerTheme.colors

        val achievements = listOf(
            Achievement(genderedStringResource(GenderedStrings.badgeHeroBeginning),     genderedStringResource(GenderedStrings.badgeThreeDays),        "🥉", stats.currentStreak >= 3,   MedalBronze),
            Achievement(genderedStringResource(GenderedStrings.badgeGoldenWeek),genderedStringResource(GenderedStrings.badgeSevenDays),        "🥈", stats.currentStreak >= 7,   MedalSilver),
            Achievement(genderedStringResource(GenderedStrings.badgeFirstTen),genderedStringResource(GenderedStrings.badgeTenDays),       "🥇", stats.currentStreak >= 10,  colors.goldWarm),
            Achievement(genderedStringResource(GenderedStrings.badgeLegend),      genderedStringResource(GenderedStrings.badgeThirtyDays),        "👑", stats.currentStreak >= 30,  colors.goldWarm),
            Achievement(genderedStringResource(GenderedStrings.badgeOpeningGood),   genderedStringResource(GenderedStrings.badgeFirstPart),         "📖", stats.quranPartsFinished >= 1,  colors.statGreenStart),
            Achievement(genderedStringResource(GenderedStrings.badgeHalfWay),   genderedStringResource(GenderedStrings.badgeFifteenParts),           "🌱", stats.quranPartsFinished >= 15, colors.secondaryAccent),
            Achievement(genderedStringResource(GenderedStrings.badgeQuranKeeper),   genderedStringResource(GenderedStrings.badgeFullQuran),    "🌳", stats.quranPartsFinished >= 30, colors.highlightAccent),
            Achievement(genderedStringResource(GenderedStrings.badgeSmallPlant),    genderedStringResource(GenderedStrings.badgeFiftyStars),          "🌱", stats.totalStars >= 50,     colors.leafGreen),
            Achievement(genderedStringResource(GenderedStrings.badgeFruitfulTree),    genderedStringResource(GenderedStrings.badgeHundredFiftyStars),         "🌳", stats.totalStars >= 150,    colors.statGreenEnd),
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
                        Text("🌟", fontSize = 28.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            genderedStringResource(GenderedStrings.badgesTitle),
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
                            genderedStringResource(GenderedStrings.badgesLibrary),
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
