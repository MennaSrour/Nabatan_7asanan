package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
internal fun ProphetMedallionIcon(icon: Int, sizeDp: Int) {
    Image(
        painter = painterResource(id = icon),
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}
