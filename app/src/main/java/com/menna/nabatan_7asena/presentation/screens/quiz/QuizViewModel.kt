package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.usecase.GetRecentQuizWinnersUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyCompetitionQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyStudyQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.IsCompetitionDayUseCase
import com.menna.nabatan_7asena.domain.usecase.SubmitCompetitionScoreUseCase
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getWeeklyStudyQuestions: GetWeeklyStudyQuestionsUseCase,
    private val getWeeklyCompetitionQuestions: GetWeeklyCompetitionQuestionsUseCase,
    private val isCompetitionDay: IsCompetitionDayUseCase,
    private val submitCompetitionScore: SubmitCompetitionScoreUseCase,
    private val getRecentWinners: GetRecentQuizWinnersUseCase,
    private val feedbackPlayer: ScreenAudioPlayer
) : ViewModel(), QuizActions {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = GenderedStrings.quizOverviewError
                )
            }
        }
    }

    override fun startStudyMode() {
        viewModelScope.launch {
            try {
                val questions = getWeeklyStudyQuestions()
                _uiState.value = _uiState.value.copy(
                    mode = QuizMode.STUDY,
                    studyCards = questions.map { QuizCardState(it) },
                    currentIndex = 0,
                    streak = 0
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = GenderedStrings.quizStudyError)
            }
        }
    }

    override fun startCompetitionMode() {
        viewModelScope.launch {
            try {
                val questions = getWeeklyCompetitionQuestions()
                _uiState.value = _uiState.value.copy(
                    competitionCards = questions.map { QuizCardState(it) },
                    preCompetitionCountdown = 3
                )
                runPreCompetitionCountdown()
            } catch (e: CancellationException) {
                throw e
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

    override fun selectAnswer(choiceIndex: Int) {
        val state = _uiState.value
        val isCompetition = state.mode == QuizMode.COMPETITION
        val cards = if (isCompetition) state.competitionCards else state.studyCards
        val index = state.currentIndex
        val card = cards.getOrNull(index) ?: return

        if (card.isFlipped) return

        if (isCompetition) timerJob?.cancel()

        val isCorrect = choiceIndex == card.question.correctIndex
        feedbackPlayer.play(if (isCorrect) R.raw.quiz_correct else R.raw.quiz_wrong)
        _effects.trySend(QuizEffect.AnswerFeedback(isCorrect))
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

    override fun goToNextCard() {
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
            }
            _uiState.value = state.copy(competitionFinished = true)
        }
    }

    override fun exitQuiz() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(mode = QuizMode.NONE, streak = 0)
        loadOverview()
    }

    override fun onCleared() {
        feedbackPlayer.stop()
        super.onCleared()
        timerJob?.cancel()
    }
}
