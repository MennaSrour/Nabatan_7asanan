package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.domain.usecase.GetRecentQuizWinnersUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyCompetitionQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.GetWeeklyStudyQuestionsUseCase
import com.menna.nabatan_7asena.domain.usecase.IsCompetitionDayUseCase
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import com.menna.nabatan_7asena.platform.Log
import com.menna.nabatan_7asena.domain.entity.OfficialQuizAttempt
import com.menna.nabatan_7asena.domain.entity.OfficialAttemptAlreadyUsed
import kotlinx.coroutines.launch

private const val OFFICIAL_REQUEST_TIMEOUT_MS = 15_000L

open class QuizViewModel constructor(
    private val getWeeklyStudyQuestions: GetWeeklyStudyQuestionsUseCase,
    private val getWeeklyCompetitionQuestions: GetWeeklyCompetitionQuestionsUseCase,
    private val isCompetitionDay: IsCompetitionDayUseCase,
    private val official: com.menna.nabatan_7asena.domain.repository.OfficialQuizRepository,
    private val getRecentWinners: GetRecentQuizWinnersUseCase,
    private val feedbackPlayer: ScreenAudioPlayer,
    private val today: () -> kotlinx.datetime.LocalDate = com.menna.nabatan_7asena.domain.util.QuizWeek::today
) : ViewModel(), QuizActions {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var overviewDate: kotlinx.datetime.LocalDate? = null
    private var overviewJob: Job? = null
    private var winnersJob: Job? = null
    private var activeAttempt: OfficialQuizAttempt? = null

    private var timerJob: Job? = null

    init {
        loadOverview()
    }

    fun refreshAvailability() {
        if (overviewDate != today() && _uiState.value.mode == QuizMode.NONE && !_uiState.value.startingCompetition) loadOverview()
    }

    override fun retryOfficialStatus() {
        if (_uiState.value.mode == QuizMode.NONE && !_uiState.value.startingCompetition) loadOverview()
    }

    private fun loadOverview() {
        if (overviewJob?.isActive == true) return
        val date = today()
        val dateChanged = overviewDate != date
        overviewDate = date
        overviewJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isCompetitionDay = isCompetitionDay(),
                    officialAttempt = if (dateChanged) null else it.officialAttempt,
                    officialStatusAvailable = false,
                    errorMessage = null
                )
            }
            try {
                val verified = withTimeoutOrNull(OFFICIAL_REQUEST_TIMEOUT_MS) {
                    val attempt = official.status()
                    _uiState.update { it.copy(officialAttempt = attempt, officialStatusAvailable = true) }
                    true
                }
                if (verified == null) Log.w("FridayQuiz", "Official attempt verification timed out")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FridayQuiz", "Could not verify official attempt", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
        if (dateChanged || winnersJob?.isActive != true) {
            winnersJob?.cancel()
            winnersJob = viewModelScope.launch {
                try {
                    val winners = withTimeoutOrNull(5000) { getRecentWinners() }
                    if (winners != null) _uiState.update { it.copy(winners = winners) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("FridayQuiz", "Could not load recent winners", e)
                }
                try {
                    official.syncPending()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("FridayQuiz", "Could not sync pending official attempts", e)
                }
            }
        }
    }

    override fun startStudyMode() {
        if (_uiState.value.startingCompetition || _uiState.value.submittingCompetition || _uiState.value.mode == QuizMode.COMPETITION) return
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
        val current = _uiState.value
        if (!isCompetitionDay() || current.mode != QuizMode.NONE || current.startingCompetition ||
            !current.officialStatusAvailable || current.officialAttempt != null) return
        _uiState.update { it.copy(startingCompetition = true) }
        viewModelScope.launch {
            try {
                val questions = getWeeklyCompetitionQuestions()
                check(questions.isNotEmpty()) { "No challenge questions" }
                val attempt = withTimeout(OFFICIAL_REQUEST_TIMEOUT_MS) { official.begin() }
                activeAttempt = attempt
                _uiState.update { it.copy(officialAttempt = attempt, competitionCards = questions.map { question -> QuizCardState(question) }, preCompetitionCountdown = 3) }
                runPreCompetitionCountdown()
            } catch (e: OfficialAttemptAlreadyUsed) {
                _uiState.update { it.copy(officialAttempt = e.attempt) }
            } catch (e: CancellationException) {
                if (e !is kotlinx.coroutines.TimeoutCancellationException) throw e
                _uiState.update { it.copy(errorMessage = GenderedStrings.quizCompetitionError, officialStatusAvailable = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = GenderedStrings.quizCompetitionError) }
            } finally { _uiState.update { it.copy(startingCompetition = false) } }
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
        if (state.mode == QuizMode.NONE || state.submittingCompetition) return
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
        if (state.mode == QuizMode.NONE || state.submittingCompetition) return
        val isCompetition = state.mode == QuizMode.COMPETITION
        val cards = if (isCompetition) state.competitionCards else state.studyCards
        if (state.submittingCompetition || isCompetition && cards.getOrNull(state.currentIndex)?.isFlipped != true) return
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= cards.size) {
            timerJob?.cancel()
            if (isCompetition) submitAndFinishCompetition() else exitQuiz()
            return
        }
        _uiState.value = state.copy(currentIndex = nextIndex)
        if (isCompetition) startQuestionTimer()
    }

    private fun submitAndFinishCompetition(exitToOverview: Boolean = false) {
        val state = _uiState.value
        val attempt = activeAttempt ?: return
        if (state.submittingCompetition) return
        _uiState.update { it.copy(submittingCompetition = true) }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(errorMessage = null) }
                val saved = official.finish(attempt, state.competitionScore)
                _uiState.update { current ->
                    if (current.officialAttempt?.token != attempt.token) current
                    else current.copy(officialAttempt = saved, competitionScore = saved.score, competitionFinished = true, submittingCompetition = false,
                        mode = if (exitToOverview) QuizMode.NONE else current.mode, preCompetitionCountdown = null)
                }
                try {
                    official.syncPending()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("FridayQuiz", "Could not sync pending official attempts", e)
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _uiState.update { it.copy(errorMessage = GenderedStrings.quizOfficialSaveError) } }
            finally { _uiState.update { it.copy(submittingCompetition = false) } }
        }
    }

    override fun exitQuiz() {
        timerJob?.cancel()
        if (_uiState.value.mode == QuizMode.COMPETITION && !_uiState.value.competitionFinished) {
            submitAndFinishCompetition(exitToOverview = true)
            return
        }
        _uiState.value = _uiState.value.copy(mode = QuizMode.NONE, streak = 0)
        loadOverview()
    }

    override fun onCleared() {
        feedbackPlayer.stop()
        super.onCleared()
        timerJob?.cancel()
    }
}
