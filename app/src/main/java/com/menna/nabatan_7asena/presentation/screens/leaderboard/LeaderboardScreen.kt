package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

private val SilverBase = Color(0xFFB0BEC5)
private val SilverDark = Color(0xFF78909C)
private val BronzeBase = Color(0xFFD7A86E)
private val BronzeDark = Color(0xFFA9744F)

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LeaderboardBackground()

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
                        CircularProgressIndicator(color = SummerTheme.colors.goldWarm)
                    }
                } else if (state.list.isEmpty()) {
                    LeaderboardEmptyState()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            val topThree = state.list.take(3)
                            if (topThree.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                PodiumView(topThree, state.currentUserId)
                                Spacer(Modifier.height(20.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(SummerTheme.colors.textBrown.copy(0.25f))
                                    )
                                    Text(
                                        genderedStringResource(GenderedStrings.leaderboardGrowing),
                                        fontSize = 12.sp,
                                        color = SummerTheme.colors.textBrown,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        Modifier
                                            .weight(1f).height(1.dp)
                                            .background(SummerTheme.colors.textBrown.copy(0.25f))
                                    )
                                }
                            }
                        }

                        val restOfList = state.list.drop(3)
                        itemsIndexed(restOfList) { index, user ->
                            LeaderboardItem(
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
fun LeaderboardBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SummerTheme.colors.dialogBackground,
                        SummerTheme.colors.backgroundLight
                    )
                )
            )
    )
}

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
                genderedStringResource(GenderedStrings.leaderboardTitle),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = SummerTheme.colors.textPrimary
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                color = SummerTheme.colors.goldWarm.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        genderedStringResource(GenderedStrings.leaderboardSubtitle),
                        fontSize = 13.sp,
                        color = SummerTheme.colors.goldDeep,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumView(users: List<LeaderboardEntry>, currentUserId: String) {
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
            PodiumMember(
                user = second, rank = 2, height = 130.dp,
                baseColor = SilverBase,
                darkerColor = SilverDark,
                badgeEmoji = "🥈", isWinner = false,
                isCurrentUser = second.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
        if (first != null) {
            PodiumMember(
                user = first, rank = 1, height = 175.dp,
                baseColor = SummerTheme.colors.goldWarm,
                darkerColor = SummerTheme.colors.goldDeep,
                badgeEmoji = "👑", isWinner = true,
                isCurrentUser = first.userId == currentUserId,
                modifier = Modifier.weight(1.2f).zIndex(2f).offset(y = (-10).dp)
            )
        }
        if (third != null) {
            PodiumMember(
                user = third, rank = 3, height = 100.dp,
                baseColor = BronzeBase,
                darkerColor = BronzeDark,
                badgeEmoji = "🥉", isWinner = false,
                isCurrentUser = third.userId == currentUserId,
                modifier = Modifier.weight(1f).zIndex(1f)
            )
        }
    }
}

@Composable
fun PodiumMember(
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
    val infinite = rememberInfiniteTransition(label = "badgePulse")
    val winningScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeScale"
    )
    val badgeScale = if (isWinner) winningScale else 1f

    val ringGlowColor = if (isCurrentUser) SummerTheme.colors.highlightAccent else baseColor

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
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
                    modifier = Modifier.background(SummerTheme.colors.backgroundLight)
                ) {
                    Image(
                        painter = painterResource(
                            id = AvatarCatalog.resFor(user.gender, user.avatarIndex)
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
                    color = SummerTheme.colors.highlightAccent,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp)
                        .zIndex(3f)
                ) {
                    Text(
                        genderedStringResource(GenderedStrings.currentUserHighlight),
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
                color = SummerTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = SummerTheme.colors.dialogBackground.copy(alpha = 0.8f),
                shape = SummerTheme.Shapes.SmallRounded,
                modifier = Modifier.padding(top = 3.dp),
                border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(0.5f))
            ) {
                Text(
                    "${user.totalStars} ⭐",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = SummerTheme.colors.goldWarm,
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
                    .background(darkerColor.copy(0.25f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(height)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Brush.verticalGradient(listOf(baseColor, darkerColor))),
                contentAlignment = Alignment.Center
            ) {
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
fun LeaderboardItem(user: LeaderboardEntry, rank: Int, isCurrentUser: Boolean) {
    val cardBg = if (isCurrentUser)
        Brush.horizontalGradient(
            listOf(SummerTheme.colors.highlightAccent.copy(0.18f), SummerTheme.colors.goldWarm.copy(0.14f))
        )
    else
        Brush.horizontalGradient(
            listOf(SummerTheme.colors.backgroundLight.copy(0.75f), SummerTheme.colors.dialogBackground.copy(0.75f))
        )

    val contentColor = if (isCurrentUser) SummerTheme.colors.highlightAccent else SummerTheme.colors.textPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(SummerTheme.Shapes.MediumRounded)
            .background(cardBg)
            .border(
                width = if (isCurrentUser) 1.5.dp else 1.dp,
                color = if (isCurrentUser) SummerTheme.colors.highlightAccent.copy(0.7f) else SummerTheme.colors.textBrown.copy(0.15f),
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
                            SummerTheme.colors.goldWarm.copy(alpha = 0.25f)
                        else
                            SummerTheme.colors.textBrown.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontWeight = FontWeight.Black,
                    color = if (isCurrentUser) SummerTheme.colors.goldWarm else SummerTheme.colors.textBrown,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                shape = CircleShape,
                border = BorderStroke(
                    1.5.dp,
                    if (isCurrentUser) SummerTheme.colors.goldWarm.copy(0.7f) else SummerTheme.colors.textBrown.copy(0.2f)
                ),
                color = SummerTheme.colors.backgroundLight,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(
                            AvatarCatalog.resFor(user.gender, user.avatarIndex)
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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
                            color = SummerTheme.colors.highlightAccent.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, SummerTheme.colors.highlightAccent.copy(0.5f))
                        ) {
                            Text(
                                genderedStringResource(GenderedStrings.currentUser),
                                fontSize = 9.sp,
                                color = SummerTheme.colors.highlightAccent,
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
                        color = SummerTheme.colors.primaryTeal.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "${user.quranPartsFinished} 📖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SummerTheme.colors.primaryTeal,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "${user.totalStars}",
                    fontWeight = FontWeight.Black,
                    color = SummerTheme.colors.goldWarm,
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = null,
                    tint = SummerTheme.colors.goldWarm,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardEmptyState() {
    val floatOffset = SummerTheme.rememberFloatingAnimation()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🌟",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
              genderedStringResource(GenderedStrings.leaderboardFirstStar),
                fontSize = 22.sp,
                color = SummerTheme.colors.textPrimary,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                genderedStringResource(GenderedStrings.leaderboardEarnStars),
                fontSize = 14.sp,
                color = SummerTheme.colors.textBrown
            )
        }
    }
}