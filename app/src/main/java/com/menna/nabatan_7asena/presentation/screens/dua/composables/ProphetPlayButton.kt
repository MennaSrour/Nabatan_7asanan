package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun ProphetPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isPlaying) SummerTheme.colors.statGreenStart else SummerTheme.colors.statGreenStart.copy(alpha = 0.55f),
        animationSpec = tween(200),
        label = "prophet_play_bg"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(28.dp)
            .background(backgroundColor, CircleShape)
            .clickable(onClickLabel = if (isPlaying) genderedStringResource(GenderedStrings.duaStopStory) else genderedStringResource(GenderedStrings.duaListenStory)) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
            contentDescription = null,
            tint = SummerTheme.colors.white,
            modifier = Modifier.size(15.dp)
        )
    }
}
