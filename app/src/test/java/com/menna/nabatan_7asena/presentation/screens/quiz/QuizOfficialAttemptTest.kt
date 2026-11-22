package com.menna.nabatan_7asena.presentation.screens.quiz

import androidx.lifecycle.ViewModelStore
import com.menna.nabatan_7asena.domain.entity.*
import com.menna.nabatan_7asena.domain.repository.*
import com.menna.nabatan_7asena.domain.usecase.*
import com.menna.nabatan_7asena.presentation.platform.ScreenAudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class QuizOfficialAttemptTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private var used: OfficialQuizAttempt? = null
    private var verificationDelayMs = 0L
    private var starts = 0
    private var studies = 0
    private var day = kotlinx.datetime.LocalDate(2026, 11, 27)
    private val quiz = object : QuizRepository {
        override suspend fun getWeeklyStudyQuestions(): List<QuizQuestion> { studies++; return listOf(QuizQuestion(1, "سؤال", listOf("نعم", "لا"), 0)) }
        override suspend fun getWeeklyCompetitionQuestions() = getWeeklyStudyQuestions()
        override fun isCompetitionDayToday() = true
        override suspend fun submitCompetitionScore(score: Int) = Unit
        override suspend fun getRecentWeekWinners() = emptyList<QuizWinner>()
    }
    private val official = object : OfficialQuizRepository {
        override suspend fun status(): OfficialQuizAttempt? {
            delay(verificationDelayMs)
            return used
        }
        override suspend fun begin(): OfficialQuizAttempt { starts++; return OfficialQuizAttempt("2026-11-21", "child", "token").also { used = it } }
        override suspend fun finish(attempt: OfficialQuizAttempt, score: Int) = attempt.copy(score = score, completed = true).also { used = it }
        override suspend fun syncPending() = Unit
    }
    private lateinit var viewModel: QuizViewModel
    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = QuizViewModel(GetWeeklyStudyQuestionsUseCase(quiz), GetWeeklyCompetitionQuestionsUseCase(quiz),
            IsCompetitionDayUseCase(quiz), official, GetRecentQuizWinnersUseCase(quiz), object : ScreenAudioPlayer {
                override fun play(resource: Int, onFinished: () -> Unit) = Unit
                override fun stop() = Unit
            }, today = { day })
        store.put("quiz", viewModel)
    }
    @After fun teardown() { store.clear(); Dispatchers.resetMain() }

    @Test fun slowServerVerificationStillEnablesFridayChallenge() = runTest(dispatcher) {
        verificationDelayMs = 6_000
        runCurrent()
        advanceTimeBy(6_000)
        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.officialStatusAvailable)
        viewModel.startCompetitionMode()
        advanceTimeBy(2_200)
        runCurrent()
        assertEquals(1, starts)
        assertEquals(QuizMode.COMPETITION, viewModel.uiState.value.mode)
    }

    @Test fun practiceCanBeRepeatedAfterAnOfficialCompletionButExamCannotRestart() = runTest(dispatcher) {
        used = OfficialQuizAttempt("2026-11-21", "child", "used", 4, true)
        viewModel.retryOfficialStatus()
        runCurrent()
        repeat(3) {
            viewModel.startStudyMode(); runCurrent()
            assertEquals(QuizMode.STUDY, viewModel.uiState.value.mode)
            viewModel.exitQuiz(); runCurrent()
        }
        viewModel.startCompetitionMode(); runCurrent()
        assertEquals(0, starts)
        assertEquals(3, studies)
        assertEquals(4, viewModel.uiState.value.officialAttempt?.score)
    }

    @Test fun repeatedStartTapsReserveOnlyOnceAndExitingConsumesTheOfficialAttempt() = runTest(dispatcher) {
        runCurrent()
        viewModel.startCompetitionMode()
        viewModel.startCompetitionMode()
        advanceTimeBy(2200); runCurrent()
        assertEquals(1, starts)
        assertEquals(QuizMode.COMPETITION, viewModel.uiState.value.mode)
        viewModel.selectAnswer(0)
        viewModel.exitQuiz(); runCurrent()
        assertEquals(QuizMode.NONE, viewModel.uiState.value.mode)
        assertEquals(1, viewModel.uiState.value.officialAttempt?.score)
        viewModel.startCompetitionMode(); runCurrent()
        assertEquals(1, starts)
    }
    @Test fun aNewWeekRefreshUnlocksOnlyTheNewChallengeWithoutRetakingTheOldOne() = runTest(dispatcher) {
        used = OfficialQuizAttempt("2026-11-21", "child", "used", 4, true)
        viewModel.retryOfficialStatus()
        runCurrent()
        assertNotNull(viewModel.uiState.value.officialAttempt)
        day = kotlinx.datetime.LocalDate(2026, 12, 4)
        used = null
        viewModel.refreshAvailability(); runCurrent()
        assertNull(viewModel.uiState.value.officialAttempt)
        viewModel.startCompetitionMode()
        advanceTimeBy(2200); runCurrent()
        assertEquals(1, starts)
    }

}
