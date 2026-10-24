package com.menna.nabatan_7asena.presentation.common.components.avatar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun AvatarThumbnail(avatarRes: Int, isSelected: Boolean, onClick: () -> Unit, avatarDescription: String, enabled: Boolean = true) {
    Box(contentAlignment = Alignment.TopEnd) {
        Surface(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(18.dp),
            color = if (isSelected) SummerTheme.colors.goldWarm.copy(alpha = 0.15f) else SummerTheme.colors.dayBackground,
            border = BorderStroke(
                if (isSelected) 3.dp else 1.dp,
                if (isSelected) SummerTheme.colors.goldWarm else SummerTheme.colors.chipUnselected
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .semantics { contentDescription = avatarDescription; selected = isSelected }
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isSelected) {
            Surface(
                shape = CircleShape,
                color = SummerTheme.colors.successGreen,
                modifier = Modifier
                    .padding(6.dp)
                    .size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = SummerTheme.colors.white,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
