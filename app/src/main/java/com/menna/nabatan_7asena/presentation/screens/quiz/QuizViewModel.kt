package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.entity.QuizQuestion
import com.menna.nabatan_7asena.domain.entity.QuizWinner
import com.menna.nabatan_7asena.domain.usecase.GetRecentQuizWinnersUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyCompetitionQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyStudyQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.IsCompetitionDayUseCase
import com.menna.nabatan_7asena.domain.usecase.SubmitCompetitionScoreUseCase
import com.menna.nabatan_7asena.presentation.common.GenderedString
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getWeeklyStudyQuestions: GetWeeklyStudyQuestionsUseCase,
    private val getWeeklyCompetitionQuestions: GetWeeklyCompetitionQuestionsUseCase,
    private val isCompetitionDay: IsCompetitionDayUseCase,
    private val submitCompetitionScore: SubmitCompetitionScoreUseCase,
    private val getRecentWinners: GetRecentQuizWinnersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadOverview()
    }

    private fun loadOverview() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val winners = getRecentWinners()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompetitionDay = isCompetitionDay(),
                    winners = winners
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = GenderedStrings.quizOverviewError
                )
            }
        }
    }

    fun startStudyMode() {
        viewModelScope.launch {
            try {
                val questions = getWeeklyStudyQuestions()
                _uiState.value = _uiState.value.copy(
                    mode = QuizMode.STUDY,
                    studyCards = questions.map { QuizCardState(it) },
                    currentIndex = 0,
                    streak = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = GenderedStrings.quizStudyError)
            }
        }
    }


    fun startCompetitionMode() {
        viewModelScope.launch {
            try {
                val questions = getWeeklyCompetitionQuestions()
                _uiState.value = _uiState.value.copy(
                    competitionCards = questions.map { QuizCardState(it) },
                    preCompetitionCountdown = 3
                )
                runPreCompetitionCountdown()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = GenderedStrings.quizCompetitionError)
            }
        }
    }

    private suspend fun runPreCompetitionCountdown() {
        for (tick in intArrayOf(3, 2, 1)) {
            _uiState.value = _uiState.value.copy(preCompetitionCountdown = tick)
            delay(700)
        }
        _uiState.value = _uiState.value.copy(
            mode = QuizMode.COMPETITION,
            currentIndex = 0,
            competitionScore = 0,
            competitionFinished = false,
            streak = 0,
            preCompetitionCountdown = null
        )
        startQuestionTimer()
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = QuizUiState.QUESTION_TIME_MS
            _uiState.value = _uiState.value.copy(questionTimeLeftMs = remaining)
            val tick = 100L
            while (remaining > 0) {
                delay(tick)
                remaining -= tick
                _uiState.value = _uiState.value.copy(questionTimeLeftMs = remaining.coerceAtLeast(0))
            }
            val current = _uiState.value
            val card = current.competitionCards.getOrNull(current.currentIndex)
            if (card != null && !card.isFlipped) {
                selectAnswer(-1) // مفيش وقت اتاخد إجابة فيه = غلط تلقائيًا
            }
        }
    }

    fun selectAnswer(choiceIndex: Int) {
        val state = _uiState.value
        val isCompetition = state.mode == QuizMode.COMPETITION
        val cards = if (isCompetition) state.competitionCards else state.studyCards
        val index = state.currentIndex
        val card = cards.getOrNull(index) ?: return

        if (card.isFlipped) return

        if (isCompetition) timerJob?.cancel()

        val isCorrect = choiceIndex == card.question.correctIndex
        val updatedCard = card.copy(
            selectedIndex = choiceIndex.takeIf { it >= 0 },
            isFlipped = true,
            result = if (isCorrect) AnswerResult.CORRECT else AnswerResult.WRONG
        )
        val updatedCards = cards.toMutableList().apply { set(index, updatedCard) }

        val newStreak = if (isCorrect) state.streak + 1 else 0
        val pointsEarned = if (isCorrect) 10 else 0

        _uiState.value = if (isCompetition) {
            state.copy(
                competitionCards = updatedCards,
                competitionScore = state.competitionScore + if (isCorrect) 1 else 0,
                streak = newStreak,
                lastAnswerCorrect = isCorrect,
                lastAnswerPoints = pointsEarned,
                answerFeedbackToken = state.answerFeedbackToken + 1
            )
        } else {
            state.copy(
                studyCards = updatedCards,
                streak = newStreak,
                lastAnswerCorrect = isCorrect,
                lastAnswerPoints = pointsEarned,
                answerFeedbackToken = state.answerFeedbackToken + 1
            )
        }
    }

    fun goToNextCard() {
        val state = _uiState.value
        val isCompetition = state.mode == QuizMode.COMPETITION
        val cards = if (isCompetition) state.competitionCards else state.studyCards
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= cards.size) {
            timerJob?.cancel()
            if (isCompetition) submitAndFinishCompetition() else exitQuiz()
            return
        }
        _uiState.value = state.copy(currentIndex = nextIndex)
        if (isCompetition) startQuestionTimer()
    }

    private fun submitAndFinishCompetition() {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                submitCompetitionScore(state.competitionScore)
            } catch (e: Exception) {
            }
            _uiState.value = state.copy(competitionFinished = true)
        }
    }

    fun exitQuiz() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(mode = QuizMode.NONE, streak = 0)
        loadOverview()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}