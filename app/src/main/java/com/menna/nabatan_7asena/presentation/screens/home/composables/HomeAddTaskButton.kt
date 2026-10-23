package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeAddTaskButton(onClick: () -> Unit) {
    Box(Modifier.padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal, vertical = 6.dp)) {
        Surface(onClick = onClick, shape = RoundedCornerShape(22.dp), color = SummerTheme.colors.cardBackground,
            border = BorderStroke(2.dp, SummerTheme.colors.addBtnBorder),
            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(22.dp), spotColor = SummerTheme.colors.addBtnShadow)) {
            HomeAddTaskLabel()
        }
    }
}
