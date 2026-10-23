package com.menna.nabatan_7asena.presentation.screens.onboarding.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun PermissionInfoCard(
    icon: ImageVector,
    text: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = SummerTheme.Shapes.MediumRounded,
        colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.wisdomStart)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SummerTheme.colors.wisdomAccent,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = SummerTheme.colors.wisdomTextPrimary,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )
        }
    }
}
