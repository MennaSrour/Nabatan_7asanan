package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AzkarList(playingResId: Int?, onZekrClick: (Zekr) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(azkarList) { index, zekr ->
            DuaCard(
                zekr = zekr,
                order = index + 1,
                isPlaying = playingResId != 0 && playingResId == zekr.audioRes,
                onClick = { onZekrClick(zekr) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
