package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 100.dp)
    ) { data ->
        Card(
            shape = SummerTheme.shapes.taskCard,
            colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.backgroundLight),
            border = BorderStroke(2.dp, SummerTheme.colors.goldWarm),
            modifier = Modifier
                .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            // ملحوظة تصميم: التوست الأصلي كان بأيقونتين (يمين وشمال) حوالين النص —
            // ده مناسب لعنوان بارز في Dialog، لكن في التوست بيبقى صخب بصري زيادة.
            // أيقونة واحدة سابقة للنص هي النمط الطبيعي لأي Snackbar احترافي.
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = SummerTheme.colors.goldWarm,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = data.visuals.message,
                    color = SummerTheme.colors.textDarkBrown,
                    style = SummerTheme.typography.snackbarText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
