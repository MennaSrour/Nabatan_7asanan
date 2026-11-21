package com.menna.nabatan_7asena.presentation.screens.badges.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerTreeCard(totalStars: Int) {
    val colors = SummerTheme.colors
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                Brush.linearGradient(
                    listOf(
                        colors.goldWarm,
                        colors.statGreenStart,
                        colors.statGreenEnd
                    )
                )
            )
        ) {
            Canvas(Modifier.fillMaxSize()) {
                listOf(
                    Offset(0.1f, 0.2f) to 2f,
                    Offset(0.3f, 0.1f) to 3f,
                    Offset(0.5f, 0.25f) to 2f,
                ).forEach { (off, r) ->
                    drawCircle(
                        color = colors.white.copy(alpha = 0.5f),
                        radius = r.dp.toPx(),
                        center = Offset(size.width * off.x, size.height * off.y)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(genderedStringResource(GenderedStrings.badgesTreeGrowing), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.white)
                    Spacer(Modifier.height(4.dp))
                    Text(genderedStringResource(GenderedStrings.badgesWaterTree), fontSize = 13.sp, color = colors.white.copy(0.9f))
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = colors.overlayDark.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            genderedStringResource(GenderedStrings.badgesCollectedStars, totalStars),
                            color = colors.overlayDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 13.sp
                        )
                    }
                }
                SummerGrowingTree(totalStars)
            }
        }
    }
}
