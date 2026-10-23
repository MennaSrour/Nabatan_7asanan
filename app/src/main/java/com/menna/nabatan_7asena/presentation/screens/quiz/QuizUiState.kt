package com.menna.nabatan_7asena.presentation.screens.quiz

import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.presentation.common.GenderedString

enum class AnswerResult { NONE, CORRECT, WRONG }
enum class QuizMode { NONE, STUDY, COMPETITION }

data class QuizCardState(
    val question: QuizQuestion,
    val selectedIndex: Int? = null,
    val isFlipped: Boolean = false,
    val result: AnswerResult = AnswerResult.NONE
)

data class QuizUiState(
    val isLoading: Boolean = true,
    val isCompetitionDay: Boolean = false,
    val winners: List<QuizWinner> = emptyList(),
    val studyCards: List<QuizCardState> = emptyList(),
    val competitionCards: List<QuizCardState> = emptyList(),
    val mode: QuizMode = QuizMode.NONE,
    val currentIndex: Int = 0,
    val competitionScore: Int = 0,
    val competitionFinished: Boolean = false,
    val errorMessage: GenderedString? = null,

    val streak: Int = 0,
    val lastAnswerCorrect: Boolean? = null,
    val lastAnswerPoints: Int = 0,
    val answerFeedbackToken: Int = 0,

    val preCompetitionCountdown: Int? = null,
    val questionTimeLeftMs: Long = QUESTION_TIME_MS
) {
    companion object {
        const val QUESTION_TIME_MS = 15_000L
    }
}
