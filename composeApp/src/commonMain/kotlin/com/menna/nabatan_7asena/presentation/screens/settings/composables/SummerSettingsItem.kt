package com.menna.nabatan_7asena.presentation.screens.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun SummerSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = SummerTheme.Colors.PrimarySummerBlue,
    hasSwitch: Boolean = false,
    isSwitchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        shape = SummerTheme.Shapes.MediumRounded,
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.cardBackground),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, iconColor.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
            if (hasSwitch) onSwitchChange?.invoke(!isSwitchChecked) else onClick()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                    Brush.radialGradient(
                        colors = listOf(
                            iconColor.copy(alpha = 0.2f),
                            iconColor.copy(alpha = 0.1f)
                        )
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SummerTheme.colors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = SummerTheme.colors.textBrown,
                    lineHeight = 18.sp
                )
            }

            if (hasSwitch) {
                Switch(
                    checked = isSwitchChecked,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SummerTheme.colors.white,
                        checkedTrackColor = SummerTheme.Colors.TaskCompletedGreen,
                        uncheckedThumbColor = SummerTheme.colors.white,
                        uncheckedTrackColor = SummerTheme.colors.chipUnselected
                    )
                )
            }
        }
    }
}
