package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun ScreenHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = DuaSpacing.l)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                    Brush.linearGradient(
                        listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep)
                    ),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.NightsStay,
                    contentDescription = null,
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(DuaSpacing.m))

            Column {
                Text(
                    genderedStringResource(GenderedStrings.duaTitle),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SummerTheme.colors.textDarkBrown
                )
                Text(
                    genderedStringResource(GenderedStrings.duaDescription),
                    fontSize = 12.sp,
                    color = SummerTheme.colors.textBrown.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(DuaSpacing.m))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.9f),
            thickness = 1.dp,
            color = SummerTheme.colors.textBrown.copy(alpha = 0.12f)
        )
    }
}
