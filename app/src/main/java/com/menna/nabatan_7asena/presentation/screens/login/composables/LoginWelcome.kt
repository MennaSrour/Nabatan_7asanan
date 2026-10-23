package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginWelcome() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            genderedStringResource(GenderedStrings.loginWelcome),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SummerTheme.colors.wisdomAccent
        )
        Spacer(Modifier.height(8.dp))
        Text(
            genderedStringResource(GenderedStrings.loginIntroduction),
            fontSize = 16.sp,
            color = SummerTheme.colors.textBrown,
            fontWeight = FontWeight.Bold
        )
    }
}
