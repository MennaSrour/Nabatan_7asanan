package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R

@Composable
fun TreasureChestCard(isUnlocked: Boolean, onClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.treasure))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isUnlocked,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    if (isUnlocked) listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFFF8F00)
                    ) else listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))
                )
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            
            if (isUnlocked) {
                
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Text(text = "🔒", fontSize = 40.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = if (isUnlocked) "هديتك جاهزة!" else "الكنز مقفول",
                    color = if (isUnlocked) Color.White else Color.Gray,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = if (isUnlocked) "اضغط عشان تفتح" else "خلص مهامك عشان تكسب",
                    color = if (isUnlocked) Color.White.copy(0.9f) else Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}