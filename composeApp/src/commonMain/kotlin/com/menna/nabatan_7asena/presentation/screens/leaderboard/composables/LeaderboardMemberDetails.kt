package com.menna.nabatan_7asena.presentation.screens.leaderboard.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.resources.painterResource
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun LeaderboardMemberDetails(
    name: String, gender: User.Gender, avatarIndex: Int, totalStars: Int,
    completedKhatmahs: Int, isCurrentUser: Boolean, modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = CircleShape, color = SummerTheme.colors.backgroundLight,
            border = BorderStroke(1.5.dp, SummerTheme.colors.goldWarm.copy(0.5f)), modifier = Modifier.size(42.dp)) {
            Image(painterResource(AvatarCatalog.resFor(gender, avatarIndex)), null,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            LeaderboardMemberName(name, isCurrentUser)
            LeaderboardMemberScore(totalStars, completedKhatmahs)
        }
    }
}

@Composable
private fun LeaderboardMemberName(name: String, isCurrentUser: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(name, modifier = Modifier.weight(1f, fill = false), maxLines = 1, overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SummerTheme.colors.textPrimary)
        if (isCurrentUser) Surface(color = SummerTheme.colors.highlightAccent.copy(0.15f), shape = SummerTheme.Shapes.SmallRounded) {
            Text("(" + genderedStringResource(GenderedStrings.currentUser) + ")", fontSize = 10.sp,
                fontWeight = FontWeight.Bold, color = SummerTheme.colors.highlightAccent,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
        }
    }
}

@Composable
private fun LeaderboardMemberScore(stars: Int, completedKhatmahs: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stars.toString(), fontSize = 16.sp, fontWeight = FontWeight.Black, color = SummerTheme.colors.goldWarm)
        Icon(Icons.Rounded.Star, stringResource(R.string.leaderboard_stars), tint = SummerTheme.colors.goldWarm, modifier = Modifier.size(16.dp))
        if (completedKhatmahs > 0) {
            Surface(
                color = SummerTheme.quran.accent.copy(alpha = 0.14f),
                shape = SummerTheme.Shapes.SmallRounded,
                border = BorderStroke(1.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.65f))
            ) {
                Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, "الختمات المكتملة",
                        tint = SummerTheme.colors.goldWarm, modifier = Modifier.size(15.dp))
                    Text(completedKhatmahs.toString(), fontSize = 12.sp, color = SummerTheme.colors.textPrimary)
                }
            }
        }
    }
}

@Composable
internal fun LeaderboardRankBadge(label: String, isLoading: Boolean = false) {
    Surface(color = SummerTheme.colors.goldWarm.copy(0.16f), shape = SummerTheme.Shapes.SmallRounded) {
        Box(Modifier.widthIn(min = 44.dp).heightIn(min = 40.dp).padding(6.dp), contentAlignment = Alignment.Center) {
            if (isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = SummerTheme.colors.goldWarm)
            else Text(label, fontSize = 15.sp, fontWeight = FontWeight.Black, color = SummerTheme.colors.textBrown)
        }
    }
}
