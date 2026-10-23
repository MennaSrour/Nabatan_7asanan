package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.presentation.screens.quran.QuranLayoutState
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuranJuzIndexItem(juz: Int, isSelected: Boolean, actions: QuranActions, layout: QuranLayoutState) {
    val q = SummerTheme.quran
    val scope = layout.scope
    val drawerState = layout.drawerState
    NavigationDrawerItem(
        label = {
            Text(
                genderedStringResource(GenderedStrings.quranJuz, formatJuzNumber(juz)),
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize   = 15.sp
            )
        },
        selected = isSelected,
        onClick  = {
            actions.goToJuz(juz)
            scope.launch { drawerState.close() }
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor   = q.accent.copy(0.15f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor        = q.accentStrong,
            unselectedTextColor      = q.textPrimary
        ),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
