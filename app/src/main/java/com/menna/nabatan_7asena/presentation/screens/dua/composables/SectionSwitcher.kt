package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun SectionSwitcher(selectedSection: Int, onSectionSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(DuaRadius.pill),
        color = SummerTheme.colors.cardBackground,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SectionButton(
                text = genderedStringResource(GenderedStrings.duaProphets),
                icon = R.drawable.prayer,
                isSelected = selectedSection == 0,
                onClick = { onSectionSelected(0) },
                modifier = Modifier.weight(1f)
            )
            SectionButton(
                text = genderedStringResource(GenderedStrings.duaFortress),
                icon = R.drawable.hisn,
                isSelected = selectedSection == 1,
                onClick = { onSectionSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
