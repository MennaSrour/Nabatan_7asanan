package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun ProphetPathNode(
    prophet: Prophet,
    order: Int,
    isLeft: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLeft) Spacer(modifier = Modifier.width(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (!isLeft) ProphetPlayButton(isPlaying = isPlaying, onClick = onClick)

            Box(modifier = Modifier.size(84.dp), contentAlignment = Alignment.TopEnd) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, clip = false)
                        .border(
                        2.dp,
                        if (isPlaying) SummerTheme.colors.statGreenStart else SummerTheme.colors.goldWarm,
                        CircleShape
                    )
                ) {
                    ProphetMedallionIcon(prophet.icon, sizeDp = 76)
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(SummerTheme.colors.cardBackground, CircleShape)
                        .border(1.5.dp, SummerTheme.colors.goldWarm, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.toString(),
                        color = SummerTheme.colors.textDarkBrown,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SummerTheme.colors.cardBackground)
                    .border(
                    1.5.dp,
                    if (isPlaying) SummerTheme.colors.statGreenStart.copy(alpha = 0.6f)
                    else SummerTheme.colors.textBrown.copy(alpha = 0.25f),
                    RoundedCornerShape(16.dp)
                )
                    .clickable(onClickLabel = prophet.name) { onClick() }
                    .semantics { role = Role.Button }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = prophet.name,
                        fontWeight = FontWeight.Black,
                        color = SummerTheme.colors.textDarkBrown,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = prophet.lesson,
                        fontSize = 11.sp,
                        color = SummerTheme.colors.textBrown.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }

            if (isLeft) ProphetPlayButton(isPlaying = isPlaying, onClick = onClick)
        }

        if (isLeft) Spacer(modifier = Modifier.width(20.dp))
    }
}
