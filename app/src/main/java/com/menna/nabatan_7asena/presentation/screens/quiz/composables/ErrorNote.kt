package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun ErrorNote(message: String) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Candy.Coral.copy(alpha = 0.15f))
            .border(2.dp, Candy.Coral.copy(alpha = 0.6f), shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🙈", fontSize = 24.sp)
        Spacer(Modifier.width(10.dp))
        KidsText(message, fontSize = 13.sp, color = SummerTheme.colors.textDarkBrown)
    }
}
