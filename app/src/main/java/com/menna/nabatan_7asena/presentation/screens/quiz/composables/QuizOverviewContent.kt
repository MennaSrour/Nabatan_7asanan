package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizUiState
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun QuizOverviewContent(
    state: QuizUiState,
    onStudyClick: () -> Unit,
    onCompetitionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
            Brush.verticalGradient(
                listOf(SummerTheme.colors.wisdomStart, SummerTheme.colors.dayBackground)
            )
        )
    ) {
        LivelyBackgroundDecor(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            MascotHeader(isCompetitionDay = state.isCompetitionDay)
            Spacer(Modifier.height(22.dp))

            if (state.isLoading) {
                LoadingSprout()
            } else {
                state.errorMessage?.let { errorMessage ->
                    ErrorNote(genderedStringResource(errorMessage))
                    Spacer(Modifier.height(16.dp))
                }

                Box(modifier = Modifier.popIn(delayMillis = 0)) {
                    if (state.winners.isNotEmpty()) {
                        WinnersSection(state.winners)
                    } else {
                        EmptyWinnersHint()
                    }
                }

                Spacer(Modifier.height(28.dp))
                if (state.isCompetitionDay) {
                    Box(modifier = Modifier.popIn(delayMillis = 150)) {
                        CompetitionEntryCard(isCompetitionDay = true, onClick = onCompetitionClick)
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.popIn(delayMillis = 300)) {
                        StudyEntryCard(onClick = onStudyClick)
                    }
                } else {
                    Box(modifier = Modifier.popIn(delayMillis = 150)) {
                        StudyEntryCard(onClick = onStudyClick)
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.popIn(delayMillis = 300)) {
                        CompetitionEntryCard(isCompetitionDay = false, onClick = onCompetitionClick)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
