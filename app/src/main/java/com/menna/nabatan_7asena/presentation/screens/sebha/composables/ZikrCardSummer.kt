package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.screens.sebha.ZikrItem
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun ZikrCardSummer(zikr: ZikrItem, onClick: (ZikrItem) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    Box(
        modifier = Modifier
            .height(160.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(SummerTheme.Shapes.ExtraRounded)
            .background(
            Brush.verticalGradient(
                listOf(Color(zikr.color).copy(0.12f), SummerTheme.colors.cardBackground.copy(0.95f))
            )
        )
            .border(1.5.dp, Color(zikr.color).copy(0.4f), SummerTheme.Shapes.ExtraRounded)
            .clickable { isPressed = true; onClick(zikr) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(Color(zikr.color).copy(0.2f)),
                contentAlignment = Alignment.Center
            ) { Text("📿", fontSize = 26.sp) }
            Spacer(Modifier.height(10.dp))
            Text(zikr.text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SummerTheme.colors.textPrimary, textAlign = TextAlign.Center, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Surface(color = Color(zikr.color).copy(0.85f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${zikr.target}", fontSize = 14.sp, color = SummerTheme.colors.white, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text("☀️", fontSize = 11.sp)
                }
            }
        }
    }
}
