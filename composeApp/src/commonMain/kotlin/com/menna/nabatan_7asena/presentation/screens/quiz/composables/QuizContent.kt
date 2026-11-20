package com.menna.nabatan_7asena.presentation.screens.quiz.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizActions
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizMode
import com.menna.nabatan_7asena.presentation.screens.quiz.QuizUiState

@Composable
fun QuizContent(state: QuizUiState, actions: QuizActions) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        when {
            state.preCompetitionCountdown != null ->
            PreCompetitionCountdownScreen(value = requireNotNull(state.preCompetitionCountdown))

            state.mode == QuizMode.NONE -> QuizOverviewContent(
                state = state,
                onStudyClick = actions::startStudyMode,
                onCompetitionClick = actions::startCompetitionMode
            )

            else -> QuizFlashCardView(
                state = state,
                onSelectAnswer = actions::selectAnswer,
                onNextCard = actions::goToNextCard,
                onExit = actions::exitQuiz
            )
        }
    }
}
