package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

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
