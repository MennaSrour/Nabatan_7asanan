package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun EmptyWinnersHint() {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 5f, durationMillis = 1000)
    val dashColor = SummerTheme.colors.goldDeep.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
            drawRoundRect(
                color = dashColor,
                cornerRadius = CornerRadius(26.dp.toPx()),
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f))
                )
            )
        }
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏆", fontSize = 46.sp, modifier = Modifier.offset(y = bounce.dp))
            Spacer(Modifier.height(8.dp))
            KidsText(
                genderedStringResource(GenderedStrings.quizFirstWinner),
                fontSize = 14.sp,
                color = SummerTheme.colors.textBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}
