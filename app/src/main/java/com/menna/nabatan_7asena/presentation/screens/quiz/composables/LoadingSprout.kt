package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun LoadingSprout() {
    val bounce = SummerTheme.rememberFloatingAnimation(range = 10f, durationMillis = 700)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌱", fontSize = 60.sp, modifier = Modifier.offset(y = bounce.dp))
            Spacer(Modifier.height(8.dp))
            KidsText(genderedStringResource(GenderedStrings.quizLoading), fontSize = 15.sp, color = SummerTheme.colors.textBrown)
        }
    }
}
