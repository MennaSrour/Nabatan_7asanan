package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeLoadingIndicator() {
    Box(Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SummerTheme.colors.goldWarm)
    }
}
