package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun MascotHeader(isCompetitionDay: Boolean) {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 5f, durationMillis = 900)
    val bubbleShape = RoundedCornerShape(topStart = 6.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 24.dp)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = bounce.dp)
                .clip(CircleShape)
                .background(SummerTheme.colors.goldWarm.copy(alpha = 0.35f))
                .border(4.dp, SummerTheme.colors.cardBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🌻", fontSize = 44.sp)
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(bubbleShape)
                .background(SummerTheme.colors.cardBackground)
                .border(2.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.6f), bubbleShape)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            KidsText(genderedStringResource(GenderedStrings.quizFridayChallenge), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            KidsText(
                if (isCompetitionDay) genderedStringResource(GenderedStrings.quizCompetitionDay)
                else genderedStringResource(GenderedStrings.quizStudyToday),
                fontSize = 13.sp,
                color = SummerTheme.colors.textBrown,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
