package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.ui.theme.SummerTheme

data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val color: Color
)

private val MedalBronze = Color(0xFFCD7F32)
private val MedalSilver = Color(0xFF90A4AE)

@Composable
fun BadgesScreen(viewModel: BadgesViewModel = hiltViewModel()) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        val stats by viewModel.userStats.collectAsState()
        val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
        val colors = SummerTheme.colors

        val achievements = listOf(
            Achievement("بداية بطل",     "التزام 3 أيام",        "🥉", stats.currentStreak >= 3,   MedalBronze),
            Achievement("الأسبوع الذهبي","التزام 7 أيام",        "🥈", stats.currentStreak >= 7,   MedalSilver),
            Achievement("العشرة الأوائل","التزام 10 أيام",       "🥇", stats.currentStreak >= 10,  colors.goldWarm),
            Achievement("الأسطورة",      "التزام 30 يوم",        "👑", stats.currentStreak >= 30,  colors.goldWarm),
            Achievement("فاتحة الخير",   "ختم الجزء 30",         "📖", stats.quranPartsFinished >= 1,  colors.statGreenStart),
            Achievement("نصف الطريق",   "ختم 15 جزء",           "🌱", stats.quranPartsFinished >= 15, colors.secondaryAccent),
            Achievement("حافظ القرآن",   "ختم المصحف كاملاً",    "🌳", stats.quranPartsFinished >= 30, colors.highlightAccent),
            Achievement("نبتة صغيرة",    "جمع 50 نجمة",          "🌱", stats.totalStars >= 50,     colors.leafGreen),
            Achievement("شجرة مثمرة",    "جمع 150 نجمة",         "🌳", stats.totalStars >= 150,    colors.statGreenEnd),
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
                            "إنجازاتي",
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
                            "مكتبة الأوسمة",
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

@Composable
fun BadgesSummerBackground(particleAlphas: List<Float>) {
    val positions = remember {
        listOf(
            Offset(0.08f, 0.05f) to 3f,
            Offset(0.85f, 0.08f) to 4f,
            Offset(0.45f, 0.03f) to 3f,
            Offset(0.60f, 0.12f) to 5f,
            Offset(0.20f, 0.10f) to 3f,
            Offset(0.75f, 0.20f) to 4f,
        )
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))

    val bgColors = SummerTheme.colors
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        bgColors.wisdomStart,
                        bgColors.dialogBackground,
                        bgColors.dayBackground
                    )
                )
            )
            positions.forEachIndexed { i, (off, radius) ->
                drawCircle(
                    color = bgColors.goldWarm.copy(0.6f),
                    radius = radius.dp.toPx(),
                    center = Offset(size.width * off.x, size.height * off.y),
                    alpha = particleAlphas.getOrElse(i) { 0.7f }
                )
            }
        }

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .alpha(0.5f)
        )
    }
}

@Composable
fun SummerTreeCard(totalStars: Int) {
    val colors = SummerTheme.colors
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            colors.goldWarm,
                            colors.statGreenStart,
                            colors.statGreenEnd
                        )
                    )
                )
        ) {
            Canvas(Modifier.fillMaxSize()) {
                listOf(
                    Offset(0.1f, 0.2f) to 2f,
                    Offset(0.3f, 0.1f) to 3f,
                    Offset(0.5f, 0.25f) to 2f,
                ).forEach { (off, r) ->
                    drawCircle(
                        color = colors.white.copy(alpha = 0.5f),
                        radius = r.dp.toPx(),
                        center = Offset(size.width * off.x, size.height * off.y)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("شجرتك بتكبر! 🌱", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.white)
                    Spacer(Modifier.height(4.dp))
                    Text("كل نجمة بتسقي شجرتك", fontSize = 13.sp, color = colors.white.copy(0.9f))
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = colors.overlayDark.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "$totalStars ⭐ مجمعة",
                            color = colors.overlayDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 13.sp
                        )
                    }
                }
                SummerGrowingTree(totalStars)
            }
        }
    }
}

@Composable
fun SummerGrowingTree(totalStars: Int) {
    val colors = SummerTheme.colors
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.growing_tree))
    val targetProgress = when {
        totalStars < 50  -> (totalStars / 50f) * 0.33f
        totalStars < 150 -> 0.33f + ((totalStars - 50) / 100f) * 0.34f
        else             -> 0.67f + ((totalStars - 150) / 150f) * 0.33f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(targetProgress, tween(1000, easing = LinearEasing), label = "tree")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(colors.white.copy(alpha = 0.2f), CircleShape)
            .padding(8.dp)
    ) {
        LottieAnimation(composition = composition, progress = { animatedProgress }, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.width(70.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = colors.goldWarm,
            trackColor = colors.white.copy(0.3f)
        )
    }
}

@Composable
fun SummerStreakCard(streak: Int) {
    val colors = SummerTheme.colors
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            colors.goldDeep,
                            colors.goldWarm,
                            colors.goldWarm
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(colors.white.copy(0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("⚡", fontSize = 28.sp) }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text("حماس متواصل!", fontWeight = FontWeight.Bold, color = colors.textBrown, fontSize = 13.sp)
                        Text("$streak أيام", fontSize = 28.sp, fontWeight = FontWeight.Black, color = colors.textDarkBrown)
                    }
                }

                Surface(
                    color = colors.white.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "خير الأعمال أدومها!",
                        fontSize = 12.sp,
                        color = colors.textDarkBrown,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SummerBadgeItem(badge: Achievement) {
    val colors = SummerTheme.colors
    Card(
        modifier = Modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(if (badge.isUnlocked) 6.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (badge.isUnlocked)
                        Brush.verticalGradient(
                            listOf(
                                badge.color.copy(alpha = 0.2f),
                                colors.white.copy(alpha = 0.95f)
                            )
                        )
                    else
                        Brush.verticalGradient(
                            listOf(colors.chipUnselected, colors.dayBackground)
                        )
                )
                .border(
                    width = if (badge.isUnlocked) 2.dp else 1.dp,
                    color = if (badge.isUnlocked) badge.color.copy(0.8f) else colors.textBrown.copy(0.2f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (badge.isUnlocked)
                                badge.color.copy(alpha = 0.2f)
                            else
                                colors.white.copy(alpha = 0.4f)
                        )
                        .border(
                            2.dp,
                            if (badge.isUnlocked) badge.color.copy(0.7f) else colors.textBrown.copy(0.2f),
                            CircleShape
                        )
                ) {
                    if (badge.isUnlocked)
                        Text(badge.icon, fontSize = 30.sp)
                    else
                        Text("🔒", fontSize = 26.sp, modifier = Modifier.alpha(0.3f))
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    badge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (badge.isUnlocked) colors.textPrimary else colors.textBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    badge.description,
                    fontSize = 10.sp,
                    color = if (badge.isUnlocked) colors.textBrown else colors.textBrown.copy(0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}