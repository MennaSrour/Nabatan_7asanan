package com.menna.nabata_7asena.presentation.screens.leaderboard

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.LeaderboardEntry
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val particleAlphas = SummerTheme.rememberParticleAlphas(count = 8)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LeaderboardBackground(particleAlphas)

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LeaderboardHeader()

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SummerTheme.Colors.PrimaryGold)
                    }
                } else if (state.list.isEmpty()) {
                    SummerEmptyState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            val topThree = state.list.take(3)
                            if (topThree.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                SummerPodiumView(topThree, state.currentUserId)
                                Spacer(Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(Color(0xFF37474F).copy(0.15f))
                                    )
                                    Text(
                                        "  بقية المتسابقين  ",
                                        fontSize = 12.sp,
                                        color = Color(0xFF37474F).copy(0.6f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(Color(0xFF37474F).copy(0.15f))
                                    )
                                }
                            }
                        }

                        val restOfList = state.list.drop(3)
                        itemsIndexed(restOfList) { index, user ->
                            SummerLeaderboardItem(
                                user = user,
                                rank = index + 4,
                                isCurrentUser = user.userId == state.currentUserId
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardBackground(particleAlphas: List<Float>) {
    val positions = remember {
        listOf(
            Offset(0.05f, 0.04f) to (3f to SparkleKind.STAR),
            Offset(0.80f, 0.06f) to (4f to SparkleKind.GOLD),
            Offset(0.40f, 0.02f) to (3f to SparkleKind.STAR),
            Offset(0.65f, 0.10f) to (5f to SparkleKind.PINK),
            Offset(0.15f, 0.08f) to (3f to SparkleKind.STAR),
            Offset(0.90f, 0.15f) to (4f to SparkleKind.GOLD),
            Offset(0.55f, 0.05f) to (3f to SparkleKind.STAR),
            Offset(0.25f, 0.12f) to (4f to SparkleKind.PINK),
        )
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SummerTheme.Colors.PrimarySummerBlue,
                        Color(0xFF81D4FA),
                        SummerTheme.Colors.BackgroundSunny
                    )
                )
            )
            positions.forEachIndexed { i, (off, radiusAndKind) ->
                val (radius, kind) = radiusAndKind
                val sparkleColor = when (kind) {
                    SparkleKind.STAR -> Color.White
                    SparkleKind.GOLD -> SummerTheme.Colors.PrimaryGold
                    SparkleKind.PINK -> SummerTheme.Colors.PrimaryPink
                }
                drawCircle(
                    color = sparkleColor.copy(alpha = 0.75f),
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

private enum class SparkleKind { STAR, GOLD, PINK }

@Composable
fun LeaderboardHeader() {
    val floatOffset = SummerTheme.rememberFloatingAnimation()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🏆",
                fontSize = 40.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "أبطال التحدي",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF37474F)
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                color = SummerTheme.Colors.PrimaryPink.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, SummerTheme.Colors.PrimaryGold.copy(0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("🌟", fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "يلا نكسب سوا!",
                        fontSize = 13.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SummerPodiumView(users: List<LeaderboardEntry>, currentUserId: String) {
    val first  = users.firstOrNull()
    val second = users.getOrNull(1)
    val third  = users.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        if (second != null) {
            SummerPodiumMember(
                user = second, rank = 2, height = 130.dp,
                baseColor = SummerTheme.Colors.PrimaryTeal,
                darkerColor = Color(0xFF00897B),
                crownRes = "🥈", isWinner = false,
                isCurrentUser = second.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
        if (first != null) {
            SummerPodiumMember(
                user = first, rank = 1, height = 175.dp,
                baseColor = SummerTheme.Colors.PrimaryGold,
                darkerColor = Color(0xFFF57C00),
                crownRes = "👑", isWinner = true,
                isCurrentUser = first.userId == currentUserId,
                modifier = Modifier.weight(1.2f).zIndex(2f).offset(y = (-10).dp)
            )
        }
        if (third != null) {
            SummerPodiumMember(
                user = third, rank = 3, height = 100.dp,
                baseColor = SummerTheme.Colors.PrimarySummerBlue,
                darkerColor = Color(0xFF0288D1),
                crownRes = "🥉", isWinner = false,
                isCurrentUser = third.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
    }
}

@Composable
fun SummerPodiumMember(
    user: LeaderboardEntry,
    rank: Int,
    height: Dp,
    baseColor: Color,
    darkerColor: Color,
    crownRes: String,
    isWinner: Boolean = false,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    val crownScale = if (isWinner) {
        val infinite = rememberInfiniteTransition(label = "crownPulse")
        val scale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "crownScale"
        )
        scale
    } else 1f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    3.dp,
                    if (isCurrentUser) SummerTheme.Colors.PrimaryPink
                    else baseColor.copy(alpha = 0.9f)
                ),
                shadowElevation = 8.dp,
                modifier = Modifier.size(if (isWinner) 86.dp else 64.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color(0xFFFFFDE7))
                ) {
                    Image(
                        painter = painterResource(
                            id = if (user.gender == User.Gender.BOY) R.drawable.boy_avatar else R.drawable.girl_avatar
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isWinner) 10.dp else 6.dp)
                    )
                }
            }

            Text(
                crownRes,
                fontSize = if (isWinner) 40.sp else 28.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = if (isWinner) (-36).dp else (-24).dp)
                    .scale(crownScale)
            )

            if (isCurrentUser) {
                Surface(
                    color = SummerTheme.Colors.PrimaryPink,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp)
                        .zIndex(3f)
                ) {
                    Text(
                        "أنت ⭐",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF37474F),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = Color.White.copy(alpha = 0.7f),
                shape = SummerTheme.Shapes.SmallRounded,
                modifier = Modifier.padding(top = 3.dp),
                border = BorderStroke(1.dp, SummerTheme.Colors.PrimaryGold.copy(0.4f))
            ) {
                Text(
                    "${user.totalStars} ⭐",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE65100),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .padding(horizontal = 4.dp).offset(y = 4.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(darkerColor.copy(0.2f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(
                        Brush.verticalGradient(listOf(baseColor, darkerColor))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun SummerLeaderboardItem(user: LeaderboardEntry, rank: Int, isCurrentUser: Boolean) {
    val cardBg = if (isCurrentUser)
        Brush.horizontalGradient(listOf(SummerTheme.Colors.PrimaryPink.copy(0.15f), SummerTheme.Colors.PrimaryGold.copy(0.15f)))
    else
        Brush.horizontalGradient(listOf(Color.White.copy(0.9f), Color(0xFFFAFAFA).copy(0.9f)))

    val contentColor = if (isCurrentUser) SummerTheme.Colors.PrimaryPink else Color(0xFF37474F)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(SummerTheme.Shapes.MediumRounded)
            .background(cardBg)
            .border(
                width = if (isCurrentUser) 1.5.dp else 1.dp,
                color = if (isCurrentUser) SummerTheme.Colors.PrimaryPink.copy(0.6f) else Color.Gray.copy(0.15f),
                shape = SummerTheme.Shapes.MediumRounded
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(SummerTheme.Shapes.SmallRounded)
                    .background(
                        if (isCurrentUser)
                            SummerTheme.Colors.PrimaryGold.copy(alpha = 0.25f)
                        else
                            Color.Gray.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontWeight = FontWeight.Black,
                    color = if (isCurrentUser) Color(0xFFE65100) else Color.Gray,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    1.5.dp,
                    if (isCurrentUser) SummerTheme.Colors.PrimaryGold.copy(0.6f) else Color.Gray.copy(0.15f)
                ),
                color = Color.White,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(
                            if (user.gender == User.Gender.BOY) R.drawable.boy_avatar else R.drawable.girl_avatar
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(4.dp)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = contentColor
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = SummerTheme.Colors.PrimaryPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, SummerTheme.Colors.PrimaryPink.copy(0.5f))
                        ) {
                            Text(
                                "أنت",
                                fontSize = 9.sp,
                                color = SummerTheme.Colors.PrimaryPink,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (user.quranPartsFinished > 0) {
                    Surface(
                        color = SummerTheme.Colors.PrimaryTeal.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "${user.quranPartsFinished} 📖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00796B),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "${user.totalStars}",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE65100),
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = SummerTheme.Colors.PrimaryGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SummerEmptyState() {
    val floatOffset = SummerTheme.rememberFloatingAnimation()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🏆",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "كن أنت البطل الأول!",
                fontSize = 22.sp,
                color = Color(0xFF37474F),
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "ابدأ في جمع النجوم وخد مكانك في القمة 🌟",
                fontSize = 14.sp,
                color = Color(0xFF37474F).copy(0.6f)
            )
        }
    }
}