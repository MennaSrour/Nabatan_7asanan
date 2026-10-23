package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun DuaCard(zekr: Zekr, order: Int, isPlaying: Boolean, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DuaRadius.card),
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        border = if (isDark) BorderStroke(
            1.dp,
            SummerTheme.colors.white.copy(alpha = 0.08f)
        ) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SummerTheme.colors.goldWarm.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(zekr.icon),
                        contentDescription = null,
                        tint = SummerTheme.colors.goldWarm,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(DuaSpacing.s))

                Text(
                    text = order.toArabicIndic(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.textBrown.copy(alpha = 0.5f)
                )

                Spacer(Modifier.width(DuaSpacing.s))

                Text(
                    text = zekr.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.textDarkBrown,
                    modifier = Modifier.weight(1f)
                )

                val buttonBg by animateColorAsState(
                    targetValue = if (isPlaying) SummerTheme.colors.goldWarm
                    else SummerTheme.colors.goldWarm.copy(alpha = 0.14f),
                    animationSpec = tween(200),
                    label = "dua_play_bg"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isPlaying) SummerTheme.colors.white else SummerTheme.colors.goldWarm,
                    animationSpec = tween(200),
                    label = "dua_play_icon"
                )

                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(buttonBg, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                        contentDescription = if (isPlaying) genderedStringResource(GenderedStrings.duaStopAudio, zekr.title) else genderedStringResource(GenderedStrings.duaListenAudio, zekr.title),
                        tint = iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(Modifier.height(DuaSpacing.m))

            HorizontalDivider(
                thickness = 1.dp,
                color = SummerTheme.colors.textBrown.copy(alpha = 0.1f)
            )

            Spacer(Modifier.height(DuaSpacing.m))

            Text(
                text = zekr.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SummerTheme.colors.textDarkBrown,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
    }
}
