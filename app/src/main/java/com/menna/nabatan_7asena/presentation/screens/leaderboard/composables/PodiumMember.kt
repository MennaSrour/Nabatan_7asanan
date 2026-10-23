package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

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
