package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranIndexHeading() {
    val q = SummerTheme.quran
    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            genderedStringResource(GenderedStrings.quranIndex),
            style      = MaterialTheme.typography.titleMedium,
            color      = q.textPrimary,
            fontWeight = FontWeight.Black
        )
    }
}
