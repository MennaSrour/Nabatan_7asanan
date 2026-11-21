package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.rewards.RewardGold
import com.menna.nabatan_7asena.presentation.screens.rewards.RewardLavender
import com.menna.nabatan_7asena.presentation.screens.rewards.RewardShape
import com.menna.nabatan_7asena.presentation.screens.rewards.RewardText
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun HomeRewardsEntries(onWheel: () -> Unit, onGames: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(onClick = onWheel, modifier = Modifier.weight(1f), shape = RewardShape,
            colors = CardDefaults.cardColors(containerColor = RewardGold)) {
            Column(Modifier.heightIn(min = 88.dp).padding(10.dp)) {
                RewardText("عجلة المكافآت", size = 18, bold = true)
                RewardText("لفة يومية مجانية", size = 15)
            }
        }
        Card(onClick = onGames, modifier = Modifier.weight(1f), shape = RewardShape,
            colors = CardDefaults.cardColors(containerColor = RewardLavender)) {
            Column(Modifier.heightIn(min = 88.dp).padding(10.dp)) {
                RewardText("ألعاب نباتا حسنا", size = 18, bold = true)
                RewardText("العب واجمع النجوم", size = 15)
            }
        }
    }
}
