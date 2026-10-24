package com.menna.nabatan_7asena.presentation.common.components.avatar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun AvatarPreview(avatarRes: Int, onEditClick: () -> Unit, enabled: Boolean = true) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Surface(
            shape = CircleShape,
            color = SummerTheme.colors.cardBackground,
            border = BorderStroke(3.dp, SummerTheme.colors.goldWarm),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(130.dp)
                .clickable(enabled = enabled, role = Role.Button) { onEditClick() }
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            shape = CircleShape,
            color = SummerTheme.colors.wisdomAccent,
            shadowElevation = 4.dp,
            modifier = Modifier
                .size(36.dp)
                .clickable(enabled = enabled, role = Role.Button) { onEditClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = genderedStringResource(GenderedStrings.loginChangeAvatar),
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
