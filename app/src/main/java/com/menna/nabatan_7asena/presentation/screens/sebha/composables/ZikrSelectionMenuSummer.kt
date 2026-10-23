package com.menna.nabatan_7asena.presentation.screens.sebha.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.sebha.ZikrItem
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun ZikrSelectionMenuSummer(
    azkarList: List<ZikrItem>,
    onSelect: (ZikrItem) -> Unit,
    onFreeTasbeehClick: () -> Unit
) {
    val particleAlphas = SummerTheme.rememberParticleAlphas(count = 6)
    Box(modifier = Modifier.fillMaxSize()) {
        SebhaSummerBackground(particleAlphas)
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
            Card(
                shape = SummerTheme.Shapes.ExtraRounded,
                colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.backgroundLight.copy(0.9f)),
                border = BorderStroke(2.dp, SummerTheme.Colors.PrimaryGold.copy(0.5f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📿", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(genderedStringResource(GenderedStrings.sebhaChoose), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SummerTheme.colors.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(genderedStringResource(GenderedStrings.sebhaEarnStars), fontSize = 13.sp, color = SummerTheme.colors.goldDeep.copy(0.9f))
                }
            }

            FreeTasbeehEntryCard(onClick = onFreeTasbeehClick)

            Spacer(Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(azkarList) { zikr -> ZikrCardSummer(zikr, onSelect) }
            }
        }
    }
}
