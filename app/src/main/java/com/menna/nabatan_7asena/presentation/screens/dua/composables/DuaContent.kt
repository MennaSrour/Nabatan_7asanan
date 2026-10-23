package com.menna.nabatan_7asena.presentation.screens.dua.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.screens.dua.DuaActions
import com.menna.nabatan_7asena.presentation.screens.dua.DuaUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun DuaContent(state: DuaUiState, actions: DuaActions) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: الأنبياء, 1: الحصن
    val playingResId = state.playingResId

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                Brush.verticalGradient(
                    listOf(SummerTheme.colors.wisdomStart, SummerTheme.colors.dayBackground)
                )
            )
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader()

            Spacer(modifier = Modifier.height(DuaSpacing.l))

            SectionSwitcher(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it }
            )

            Spacer(modifier = Modifier.height(DuaSpacing.l))

            Crossfade(targetState = selectedSection, label = "sections_fade") { section ->
                when (section) {
                    0 -> ProphetsPathList(
                        playingResId = playingResId,
                        onProphetClick = { actions.toggleAudio(it.audioRes) }
                    )
                    else -> AzkarList(
                        playingResId = playingResId,
                        onZekrClick = { actions.toggleAudio(it.audioRes) }
                    )
                }
            }
        }
    }
}
