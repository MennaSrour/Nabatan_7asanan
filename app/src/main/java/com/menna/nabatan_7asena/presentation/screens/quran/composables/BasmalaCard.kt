package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun BasmalaCard(fontFamily: FontFamily) {
    val q = SummerTheme.quran
    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(containerColor = q.card),
        border   = BorderStroke(1.dp, q.accent.copy(0.25f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier         = Modifier.fillMaxWidth().padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontFamily = fontFamily,
                fontSize   = 22.sp,
                textAlign  = TextAlign.Center,
                color      = q.accentStrong
            )
        }
    }
}
