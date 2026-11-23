package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PeopleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.components.TrophyArtwork
import com.menna.nabatan_7asena.presentation.screens.badges.BADGES_TITLE
import com.menna.nabatan_7asena.presentation.screens.prayerguide.GuideArtwork
import com.menna.nabatan_7asena.presentation.screens.prayerguide.GuideScene
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.presentation.screens.friends.FriendsCream
import com.menna.nabatan_7asena.presentation.screens.friends.FriendsGold
import com.menna.nabatan_7asena.presentation.screens.friends.FriendsLavender
import com.menna.nabatan_7asena.presentation.screens.friends.FriendsPurple
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun HomeShortcutCards(badgeCount: String, onPrayerGuide: () -> Unit, onBadges: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShortcutCard(
            "تعلّم الوضوء والصلاة", "خطوة بخطوة",
            Color(0xFFD9F8FF), Color(0xFFAEF0D5), Color(0xFF6BB9B3),
            onPrayerGuide, Modifier.weight(1f)
        ) { GuideArtwork(GuideScene.WashHands, Modifier.size(width = 90.dp, height = 64.dp)) }
        ShortcutCard(
            BADGES_TITLE, badgeCount,
            Color(0xFFFFECB3), Color(0xFFE7D5FF), Color(0xFFAF8BD5),
            onBadges, Modifier.weight(1f)
        ) { TrophyArtwork(Modifier.size(64.dp)) }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    subtitle: String,
    top: Color,
    bottom: Color,
    edge: Color,
    onClick: () -> Unit,
    modifier: Modifier,
    artwork: @Composable () -> Unit
) {
    ChunkyButton(
        onClick = onClick, color = top, edgeColor = edge, modifier = modifier,
        depth = 8.dp, faceBrush = Brush.verticalGradient(listOf(top, bottom)),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            artwork()
            Box(Modifier.heightIn(min = 50.dp), contentAlignment = Alignment.Center) {
                Text(title, color = Color(0xFF382752), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            }
            Text(subtitle, color = Color(0xFF503A6A), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun FriendsHomeCard(onClick: () -> Unit, showBadge: Boolean) {
    ChunkyButton(
        onClick = onClick,
        color = FriendsCream,
        edgeColor = Color(0xFFAF8BD5),
        shape = RoundedCornerShape(26.dp),
        faceBrush = Brush.horizontalGradient(listOf(FriendsLavender, FriendsCream)),
        faceBorder = FriendsGold,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 8.dp)
            .semantics { if (showBadge) stateDescription = "لديك تحديث جديد" }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.PeopleOutline, null, Modifier.size(32.dp), tint = FriendsPurple)
            Text(
                text = "أصحابي",
                modifier = Modifier.weight(1f),
                color = FriendsPurple,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            if (showBadge) Badge(containerColor = FriendsPurple)
        }
    }
}
