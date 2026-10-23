package com.menna.nabatan_7asena.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
internal fun ClayImage(
    resName: String,
    fallbackIcon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val context = LocalContext.current
    val resId = remember(resName) {
        context.resources.getIdentifier(resName, "drawable", context.packageName)
    }
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
            colorFilter = if (tint != Color.Unspecified) ColorFilter.tint(tint) else null
        )
    } else {
        Box(
            modifier = modifier
                .background(tint.copy(alpha = 0.12f), CircleShape)
                .border(1.5.dp, tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}
