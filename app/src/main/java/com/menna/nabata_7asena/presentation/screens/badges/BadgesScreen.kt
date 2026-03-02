package com.menna.nabata_7asena.presentation.screens.badges

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
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.ui.theme.RamadanTheme

data class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val color: Color
)

@Composable
fun BadgesScreen(viewModel: BadgesViewModel = hiltViewModel()) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        val stats by viewModel.userStats.collectAsState()
        val starAlphas = RamadanTheme.rememberStarAlphas(count = 6)

        val achievements = listOf(
            Achievement("بداية بطل",     "التزام 3 أيام",        "🥉", stats.currentStreak >= 3,   Color(0xFFCD7F32)),
            Achievement("الأسبوع الذهبي","التزام 7 أيام",        "🥈", stats.currentStreak >= 7,   Color(0xFF90A4AE)),
            Achievement("العشرة الأوائل","التزام 10 أيام",       "🥇", stats.currentStreak >= 10,  Color(0xFFFFD700)),
            Achievement("الأسطورة",      "التزام 30 يوم",        "👑", stats.currentStreak >= 30,  RamadanTheme.Colors.PrimaryGold),
            Achievement("فاتحة الخير",   "ختم الجزء 30",         "📖", stats.quranPartsFinished >= 1,  Color(0xFF66BB6A)),
            Achievement("نصف الطريق",   "ختم 15 جزء",           "🌙", stats.quranPartsFinished >= 15, RamadanTheme.Colors.PrimaryTeal),
            Achievement("حافظ القرآن",   "ختم المصحف كاملاً",    "🕋", stats.quranPartsFinished >= 30, RamadanTheme.Colors.PrimaryPink),
            Achievement("نبتة صغيرة",    "جمع 50 نجمة",          "🌱", stats.totalStars >= 50,     Color(0xFF8BC34A)),
            Achievement("شجرة مثمرة",    "جمع 150 نجمة",         "🌳", stats.totalStars >= 150,    Color(0xFF388E3C)),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            BadgesRamadanBackground(starAlphas)

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
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("🌟", fontSize = 28.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "إنجازاتي",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    RamadanTreeCard(stats.totalStars)
                }

                item(span = { GridItemSpan(2) }) {
                    RamadanStreakCard(stats.currentStreak)
                }

                item(span = { GridItemSpan(2) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp).height(20.dp)
                                .background(RamadanTheme.Colors.PrimaryGold, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "مكتبة الأوسمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                items(achievements) { badge ->
                    RamadanBadgeItem(badge)
                }
            }
        }
    }
}

@Composable
fun BadgesRamadanBackground(starAlphas: List<Float>) {
    val starPositions = remember {
        listOf(
            Offset(0.08f, 0.05f) to 3f,
            Offset(0.85f, 0.08f) to 4f,
            Offset(0.45f, 0.03f) to 3f,
            Offset(0.60f, 0.12f) to 5f,
            Offset(0.20f, 0.10f) to 3f,
            Offset(0.75f, 0.20f) to 4f,
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A237E),
                    Color(0xFF283593),
                    Color(0xFF3949AB),
                    Color(0xFF5C6BC0).copy(alpha = 0.6f)
                )
            )
        )
        starPositions.forEachIndexed { i, (off, radius) ->
            drawCircle(
                color = Color(0xFFFFF176),
                radius = radius.dp.toPx(),
                center = Offset(size.width * off.x, size.height * off.y),
                alpha = starAlphas.getOrElse(i) { 0.7f }
            )
        }
    }
}

@Composable
fun RamadanTreeCard(totalStars: Int) {
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
                            Color(0xFF283593),
                            Color(0xFF3949AB),
                            Color(0xFF1A237E)
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
                        color = Color(0xFFFFF176).copy(alpha = 0.3f),
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
                    Text("شجرتك بتكبر! 🌱", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text("كل نجمة بتسقي شجرتك", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "$totalStars ⭐ مجمعة",
                            color = RamadanTheme.Colors.PrimaryGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 13.sp
                        )
                    }
                }
                RamadanGrowingTree(totalStars)
            }
        }
    }
}

@Composable
fun RamadanGrowingTree(totalStars: Int) {
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
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .padding(8.dp)
    ) {
        LottieAnimation(composition = composition, progress = { animatedProgress }, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.width(70.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = RamadanTheme.Colors.PrimaryGold,
            trackColor = Color.White.copy(0.2f)
        )
    }
}
@Composable
fun RamadanStreakCard(streak: Int) {
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
                            Color(0xFF7E57C2),
                            Color(0xFF9575CD),
                            Color(0xFFB39DDB)
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
                            .background(Color.White.copy(0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("⚡", fontSize = 28.sp) }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text("حماس متواصل!", fontWeight = FontWeight.Bold, color = Color.White.copy(0.9f), fontSize = 13.sp)
                        Text("$streak أيام", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "خير الأعمال أدومها!",
                        fontSize = 12.sp,
                        color = Color.White,
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
fun RamadanBadgeItem(badge: Achievement) {
    val bgColor = if (badge.isUnlocked)
        Color(0xFF283593).copy(alpha = 0.85f)
    else
        Color(0xFF1A237E).copy(alpha = 0.6f)

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
                                badge.color.copy(alpha = 0.3f),
                                Color(0xFF1A237E).copy(alpha = 0.9f)
                            )
                        )
                    else
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E2B6B), Color(0xFF1A237E))
                        )
                )
                .border(
                    width = if (badge.isUnlocked) 1.5.dp else 1.dp,
                    color = if (badge.isUnlocked) badge.color.copy(0.6f) else Color.White.copy(0.1f),
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
                                badge.color.copy(alpha = 0.25f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        )
                        .border(
                            2.dp,
                            if (badge.isUnlocked) badge.color.copy(0.8f) else Color.White.copy(0.15f),
                            CircleShape
                        )
                ) {
                    if (badge.isUnlocked)
                        Text(badge.icon, fontSize = 30.sp)
                    else
                        Text("🔒", fontSize = 26.sp, modifier = Modifier.alpha(0.4f))
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    badge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (badge.isUnlocked) Color.White else Color.White.copy(0.4f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    badge.description,
                    fontSize = 10.sp,
                    color = if (badge.isUnlocked) badge.color.copy(0.9f) else Color.White.copy(0.3f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}