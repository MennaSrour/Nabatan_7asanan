package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun EntryCardContent(
    emoji: String,
    title: String,
    subtitle: String,
    titleColor: Color,
    subtitleColor: Color,
    emojiModifier: Modifier = Modifier,
    useShadow: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 32.sp, modifier = emojiModifier)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            KidsText(
                title,
                fontSize = 18.sp,
                color = titleColor,
                fontWeight = FontWeight.ExtraBold,
                shadow = if (useShadow) ButtonTextShadow else null
            )
            Spacer(Modifier.height(2.dp))
            KidsText(
                subtitle,
                fontSize = 12.sp,
                color = subtitleColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
