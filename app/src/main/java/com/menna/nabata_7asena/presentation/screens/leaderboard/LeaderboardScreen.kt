package com.menna.nabata_7asena.presentation.screens.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
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
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.LeaderboardEntry
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.ui.theme.RamadanTheme
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val starAlphas = RamadanTheme.rememberStarAlphas(count = 8)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LeaderboardBackground(starAlphas)

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
                        CircularProgressIndicator(color = RamadanTheme.Colors.PrimaryGold)
                    }
                } else if (state.list.isEmpty()) {
                    RamadanEmptyState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            val topThree = state.list.take(3)
                            if (topThree.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                RamadanPodiumView(topThree, state.currentUserId)
                                Spacer(Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(Color.White.copy(0.2f))
                                    )
                                    Text(
                                        "  بقية المتسابقين  ",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(0.6f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(Color.White.copy(0.2f))
                                    )
                                }
                            }
                        }

                        val restOfList = state.list.drop(3)
                        itemsIndexed(restOfList) { index, user ->
                            RamadanLeaderboardItem(
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
fun LeaderboardBackground(starAlphas: List<Float>) {
    val starPositions = remember {
        listOf(
            Offset(0.05f, 0.04f) to 3f,
            Offset(0.80f, 0.06f) to 4f,
            Offset(0.40f, 0.02f) to 3f,
            Offset(0.65f, 0.10f) to 5f,
            Offset(0.15f, 0.08f) to 3f,
            Offset(0.90f, 0.15f) to 4f,
            Offset(0.55f, 0.05f) to 3f,
            Offset(0.25f, 0.12f) to 4f,
        )
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A237E),
                    Color(0xFF283593),
                    Color(0xFF3949AB),
                    Color(0xFF5C6BC0).copy(alpha = 0.5f)
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
fun LeaderboardHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("👑", fontSize = 36.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                "أبطال التحدي",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                color = RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    "تنافسوا على الخير 🌙",
                    fontSize = 13.sp,
                    color = RamadanTheme.Colors.PrimaryGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun RamadanPodiumView(users: List<LeaderboardEntry>, currentUserId: String) {
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
            RamadanPodiumMember(
                user = second, rank = 2, height = 130.dp,
                baseColor = Color(0xFF5C6BC0),
                darkerColor = Color(0xFF3949AB),
                crownRes = "🥈", isWinner = false,
                isCurrentUser = second.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
        if (first != null) {
            RamadanPodiumMember(
                user = first, rank = 1, height = 170.dp,
                baseColor = Color(0xFF7E57C2),
                darkerColor = Color(0xFF512DA8),
                crownRes = "👑", isWinner = true,
                isCurrentUser = first.userId == currentUserId,
                modifier = Modifier.weight(1.2f).zIndex(2f).offset(y = (-10).dp)
            )
        }
        if (third != null) {
            RamadanPodiumMember(
                user = third, rank = 3, height = 100.dp,
                baseColor = Color(0xFF42A5F5),
                darkerColor = Color(0xFF1976D2),
                crownRes = "🥉", isWinner = false,
                isCurrentUser = third.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
    }
}

@Composable
fun RamadanPodiumMember(
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
                    if (isCurrentUser) RamadanTheme.Colors.PrimaryPink
                    else RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.8f)
                ),
                shadowElevation = 8.dp,
                modifier = Modifier.size(if (isWinner) 82.dp else 62.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color(0xFF283593))
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
                fontSize = if (isWinner) 38.sp else 28.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = if (isWinner) (-34).dp else (-24).dp)
            )

            if (isCurrentUser) {
                Surface(
                    color = RamadanTheme.Colors.PrimaryPink,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp)
                        .zIndex(3f)
                ) {
                    Text(
                        "أنت",
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
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 3.dp)
            ) {
                Text(
                    "${user.totalStars} ⭐",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = RamadanTheme.Colors.PrimaryGold,
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
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(darkerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(
                        Brush.verticalGradient(listOf(baseColor, darkerColor))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun RamadanLeaderboardItem(user: LeaderboardEntry, rank: Int, isCurrentUser: Boolean) {
    val cardBg = if (isCurrentUser)
        Brush.horizontalGradient(listOf(Color(0xFF283593).copy(0.95f), Color(0xFF3949AB).copy(0.85f)))
    else
        Brush.horizontalGradient(listOf(Color(0xFF1A237E).copy(0.7f), Color(0xFF283593).copy(0.6f)))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .then(
                if (isCurrentUser)
                    Modifier.then(
                        Modifier.padding(0.dp)
                    )
                else Modifier
            )
    ) {
        if (isCurrentUser) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
                    .then(
                        Modifier.padding(0.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isCurrentUser)
                            RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.3f)
                        else
                            Color.White.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontWeight = FontWeight.Black,
                    color = if (isCurrentUser) RamadanTheme.Colors.PrimaryGold else Color.White.copy(0.7f),
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    1.5.dp,
                    if (isCurrentUser) RamadanTheme.Colors.PrimaryGold.copy(0.6f) else Color.White.copy(0.15f)
                ),
                color = Color(0xFF283593),
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
                        color = Color.White
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = RamadanTheme.Colors.PrimaryPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, RamadanTheme.Colors.PrimaryPink.copy(0.5f))
                        ) {
                            Text(
                                "أنت",
                                fontSize = 9.sp,
                                color = RamadanTheme.Colors.PrimaryPink,
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
                        color = Color(0xFF283593).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "${user.quranPartsFinished} 📖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RamadanTheme.Colors.PrimaryTeal,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "${user.totalStars}",
                    fontWeight = FontWeight.Black,
                    color = RamadanTheme.Colors.PrimaryGold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = RamadanTheme.Colors.PrimaryGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RamadanEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌙", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "كن أنت البطل الأول!",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "ابدأ رحلتك الرمضانية الآن",
                fontSize = 14.sp,
                color = Color.White.copy(0.6f)
            )
        }
    }
}