package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun FreeTasbeehEntryCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SummerTheme.Shapes.ExtraRounded)
            .background(SummerTheme.colors.cardBackground.copy(alpha = 0.6f))
            .border(
            width = 1.5.dp,
            color = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.5f),
            shape = SummerTheme.Shapes.ExtraRounded
        )
            .clickable(onClickLabel = genderedStringResource(GenderedStrings.sebhaFree)) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SummerTheme.Colors.PrimaryGold.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = SummerTheme.Colors.PrimaryGold
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                genderedStringResource(GenderedStrings.sebhaFree),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SummerTheme.colors.textPrimary
            )
            Text(
                genderedStringResource(GenderedStrings.sebhaCustomHint),
                fontSize = 12.sp,
                color = SummerTheme.colors.textPrimary.copy(alpha = 0.6f)
            )
        }
    }
}
