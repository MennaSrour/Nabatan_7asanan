package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.ui.theme.GalaxyTheme
import kotlin.random.Random
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val twinkleAlphas = GalaxyTheme.rememberTwinkleAlphas(count = 14)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GalaxyBackground(twinkleAlphas)

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                GalaxyHeader()

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GalaxyTheme.Colors.StarGold)
                    }
                } else if (state.list.isEmpty()) {
                    GalaxyEmptyState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            val topThree = state.list.take(3)
                            if (topThree.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                GalaxyPodiumView(topThree, state.currentUserId)
                                Spacer(Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(GalaxyTheme.Colors.TextMuted.copy(0.25f))
                                    )
                                    Text(
                                        "وما زال الغرس يزهر",
                                        fontSize = 12.sp,
                                        color = GalaxyTheme.Colors.TextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(GalaxyTheme.Colors.TextMuted.copy(0.25f))
                                    )
                                }
                            }
                        }

                        val restOfList = state.list.drop(3)
                        itemsIndexed(restOfList) { index, user ->
                            GalaxyLeaderboardItem(
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

/**
 * خلفية فضاء متدرجة الظلام + نجوم متلألئة + كواكب عائمة زخرفية + مذنّب متحرك.
 * كل شيء مرسوم بالكود، من غير أي صور خارجية.
 */
@Composable
fun GalaxyBackground(twinkleAlphas: List<Float>) {
    val stars = remember {
        List(46) {
            Triple(
                Offset(Random.nextFloat(), Random.nextFloat()),
                (1.2f + Random.nextFloat() * 1.8f),
                Random.nextInt(twinkleAlphas.size.coerceAtLeast(1))
            )
        }
    }
    val ringAngle = GalaxyTheme.rememberSlowRotation(durationMillis = 18000)

    val comet = rememberInfiniteTransitionOffset()

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // تدرّج سماء الفضاء
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GalaxyTheme.Colors.DeepSpace,
                        GalaxyTheme.Colors.SpaceIndigo,
                        GalaxyTheme.Colors.SpaceNavy
                    )
                )
            )

            // سحابة "نيبولا" خفيفة في الخلفية
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GalaxyTheme.Colors.NebulaPurple.copy(alpha = 0.22f), Color.Transparent),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.15f, size.height * 0.1f)
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.15f, size.height * 0.1f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GalaxyTheme.Colors.NebulaPink.copy(alpha = 0.18f), Color.Transparent),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.9f, size.height * 0.18f)
                ),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.9f, size.height * 0.18f)
            )

            // كوكب زخرفي بحلقة (زاوية علوية يمين)
            val planetCenter = Offset(size.width * 0.86f, size.height * 0.09f)
            val planetRadius = size.width * 0.10f
            rotate(ringAngle, pivot = planetCenter) {
                drawOval(
                    color = GalaxyTheme.Colors.CometCyan.copy(alpha = 0.35f),
                    topLeft = Offset(planetCenter.x - planetRadius * 1.7f, planetCenter.y - planetRadius * 0.35f),
                    size = Size(planetRadius * 3.4f, planetRadius * 0.7f)
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GalaxyTheme.Colors.MarsOrange, GalaxyTheme.Colors.NebulaPurple)
                ),
                radius = planetRadius,
                center = planetCenter
            )

            // كوكب صغير تاني (يسار تحت الهيدر)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GalaxyTheme.Colors.CometCyan, GalaxyTheme.Colors.SpaceIndigo)
                ),
                radius = size.width * 0.045f,
                center = Offset(size.width * 0.08f, size.height * 0.22f)
            )

            // نجوم متلألئة
            stars.forEach { (offsetFraction, radiusDp, alphaIndex) ->
                drawCircle(
                    color = GalaxyTheme.Colors.MoonSilver,
                    radius = radiusDp.dp.toPx(),
                    center = Offset(size.width * offsetFraction.x, size.height * offsetFraction.y),
                    alpha = twinkleAlphas.getOrElse(alphaIndex) { 0.8f }
                )
            }

            // مذنّب بذيل خفيف يعبر أعلى الشاشة
            val cometStart = Offset(size.width * (comet - 0.15f), size.height * 0.06f)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, GalaxyTheme.Colors.MoonSilver.copy(alpha = 0.9f))
                ),
                start = Offset(cometStart.x - 60f, cometStart.y - 30f),
                end = cometStart,
                strokeWidth = 3f
            )
        }
    }
}

/** حركة أفقية بطيئة متكررة من 0 إلى 1، تُستخدم لتحريك المذنّب */
@Composable
private fun rememberInfiniteTransitionOffset(): Float {
    val infinite = rememberInfiniteTransition(label = "cometTravel")
    val value by infinite.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cometX"
    )
    return value
}

@Composable
fun GalaxyHeader() {
    val floatOffset = GalaxyTheme.rememberFloatingAnimation()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🚀",
                fontSize = 40.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "أطيب ثمار غرس أمتنا",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = GalaxyTheme.Colors.TextLight
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                color = GalaxyTheme.Colors.NebulaPurple.copy(alpha = 0.35f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, GalaxyTheme.Colors.CometCyan.copy(0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) { Text(
                        "في سباق الخير... تزهر خطانا",
                        fontSize = 13.sp,
                        color = GalaxyTheme.Colors.StarGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GalaxyPodiumView(users: List<LeaderboardEntry>, currentUserId: String) {
    val first = users.firstOrNull()
    val second = users.getOrNull(1)
    val third = users.getOrNull(2)

    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        if (second != null) {
            GalaxyPodiumMember(
                user = second, rank = 2, height = 130.dp,
                baseColor = GalaxyTheme.Colors.CometCyan,
                darkerColor = Color(0xFF0091EA),
                badgeEmoji = "🛰️", isWinner = false,
                isCurrentUser = second.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
        if (first != null) {
            GalaxyPodiumMember(
                user = first, rank = 1, height = 175.dp,
                baseColor = GalaxyTheme.Colors.StarGold,
                darkerColor = GalaxyTheme.Colors.MarsOrange,
                badgeEmoji = "👑", isWinner = true,
                isCurrentUser = first.userId == currentUserId,
                modifier = Modifier.weight(1.2f).zIndex(2f).offset(y = (-10).dp)
            )
        }
        if (third != null) {
            GalaxyPodiumMember(
                user = third, rank = 3, height = 100.dp,
                baseColor = GalaxyTheme.Colors.NebulaPurple,
                darkerColor = Color(0xFF5B21B6),
                badgeEmoji = "🪐", isWinner = false,
                isCurrentUser = third.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
    }
}

@Composable
fun GalaxyPodiumMember(
    user: LeaderboardEntry,
    rank: Int,
    height: Dp,
    baseColor: Color,
    darkerColor: Color,
    badgeEmoji: String,
    isWinner: Boolean = false,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    val badgeScale = if (isWinner) {
        val infinite = rememberInfiniteTransition(label = "badgePulse")
        val scale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "badgeScale"
        )
        scale
    } else 1f

    val ringGlowColor = if (isCurrentUser) GalaxyTheme.Colors.NebulaPink else baseColor

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            // هالة توهج خلف الخوذة
            Box(
                modifier = Modifier
                    .size(if (isWinner) 104.dp else 82.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(ringGlowColor.copy(alpha = 0.45f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Surface(
                shape = CircleShape,
                border = BorderStroke(3.dp, ringGlowColor),
                shadowElevation = 10.dp,
                modifier = Modifier.size(if (isWinner) 86.dp else 64.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(GalaxyTheme.Colors.SpaceIndigo)
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
                badgeEmoji,
                fontSize = if (isWinner) 38.sp else 26.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = if (isWinner) (-36).dp else (-24).dp)
                    .scale(badgeScale)
            )

            if (isCurrentUser) {
                Surface(
                    color = GalaxyTheme.Colors.NebulaPink,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp)
                        .zIndex(3f)
                ) {
                    Text(
                        "أنت ✨",
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
                color = GalaxyTheme.Colors.TextLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = GalaxyTheme.Colors.SpaceNavy.copy(alpha = 0.8f),
                shape = GalaxyTheme.Shapes.SmallRounded,
                modifier = Modifier.padding(top = 3.dp),
                border = BorderStroke(1.dp, GalaxyTheme.Colors.StarGold.copy(0.5f))
            ) {
                Text(
                    "${user.totalStars} ⭐",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = GalaxyTheme.Colors.StarGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // القاعدة (منصة التتويج) بشكل "قطاع كوكب" مع نقاط تُحاكي الفوهات
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .padding(horizontal = 4.dp).offset(y = 4.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(darkerColor.copy(0.25f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Brush.verticalGradient(listOf(baseColor, darkerColor))),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val craterSpots = listOf(
                        Offset(0.25f, 0.3f) to 5f,
                        Offset(0.7f, 0.55f) to 4f,
                        Offset(0.45f, 0.75f) to 3f,
                        Offset(0.15f, 0.65f) to 3f
                    )
                    craterSpots.forEach { (frac, radius) ->
                        drawCircle(
                            color = Color.White.copy(alpha = 0.10f),
                            radius = radius.dp.toPx(),
                            center = Offset(size.width * frac.x, size.height * frac.y)
                        )
                    }
                }
                Text(
                    "$rank",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }
    }
}

@Composable
fun GalaxyLeaderboardItem(user: LeaderboardEntry, rank: Int, isCurrentUser: Boolean) {
    val cardBg = if (isCurrentUser)
        Brush.horizontalGradient(
            listOf(GalaxyTheme.Colors.NebulaPink.copy(0.22f), GalaxyTheme.Colors.NebulaPurple.copy(0.22f))
        )
    else
        Brush.horizontalGradient(
            listOf(GalaxyTheme.Colors.SpaceIndigo.copy(0.75f), GalaxyTheme.Colors.SpaceNavy.copy(0.75f))
        )

    val contentColor = if (isCurrentUser) GalaxyTheme.Colors.NebulaPink else GalaxyTheme.Colors.TextLight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(GalaxyTheme.Shapes.MediumRounded)
            .background(cardBg)
            .border(
                width = if (isCurrentUser) 1.5.dp else 1.dp,
                color = if (isCurrentUser) GalaxyTheme.Colors.NebulaPink.copy(0.7f) else GalaxyTheme.Colors.CometCyan.copy(0.18f),
                shape = GalaxyTheme.Shapes.MediumRounded
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
                    .clip(GalaxyTheme.Shapes.SmallRounded)
                    .background(
                        if (isCurrentUser)
                            GalaxyTheme.Colors.StarGold.copy(alpha = 0.25f)
                        else
                            GalaxyTheme.Colors.CometCyan.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontWeight = FontWeight.Black,
                    color = if (isCurrentUser) GalaxyTheme.Colors.StarGold else GalaxyTheme.Colors.TextMuted,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    1.5.dp,
                    if (isCurrentUser) GalaxyTheme.Colors.StarGold.copy(0.7f) else GalaxyTheme.Colors.CometCyan.copy(0.25f)
                ),
                color = GalaxyTheme.Colors.SpaceIndigo,
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
                            color = GalaxyTheme.Colors.NebulaPink.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, GalaxyTheme.Colors.NebulaPink.copy(0.5f))
                        ) {
                            Text(
                                "أنت",
                                fontSize = 9.sp,
                                color = GalaxyTheme.Colors.NebulaPink,
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
                        color = GalaxyTheme.Colors.CometCyan.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "${user.quranPartsFinished} 📖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GalaxyTheme.Colors.CometCyan,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "${user.totalStars}",
                    fontWeight = FontWeight.Black,
                    color = GalaxyTheme.Colors.StarGold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = null,
                    tint = GalaxyTheme.Colors.StarGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun GalaxyEmptyState() {
    val floatOffset = GalaxyTheme.rememberFloatingAnimation()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🧑‍🚀",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "كن أنت أول نجم في المجرة!",
                fontSize = 22.sp,
                color = GalaxyTheme.Colors.TextLight,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "ابدأ رحلتك واجمع النجوم من كل الكواكب 🪐✨",
                fontSize = 14.sp,
                color = GalaxyTheme.Colors.TextMuted
            )
        }
    }
}