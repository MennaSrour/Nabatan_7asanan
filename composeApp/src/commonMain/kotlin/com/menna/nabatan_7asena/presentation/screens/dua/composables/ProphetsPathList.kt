package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.random.Random

@Composable
fun ProphetsPathList(playingResId: Int?, onProphetClick: (Prophet) -> Unit) {
    val stoneColor = SummerTheme.colors.textBrown

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepY = 46.dp.toPx()
            var y = 0f
            val rnd = Random(42)
            while (y < size.height) {
                val radius = (2 + rnd.nextInt(2)).dp.toPx()
                val jitterX = (rnd.nextInt(7) - 3).dp.toPx()
                drawCircle(
                    color = stoneColor.copy(alpha = 0.18f),
                    radius = radius,
                    center = Offset(size.width / 2 + jitterX, y)
                )
                y += stepY
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(prophetsList) { index, prophet ->
                ProphetPathNode(
                    prophet = prophet,
                    order = index + 1,
                    isLeft = index % 2 == 0,
                    isPlaying = playingResId != 0 && playingResId == prophet.audioRes,
                    onClick = { onProphetClick(prophet) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
