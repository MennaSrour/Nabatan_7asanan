package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.lifecycle.ViewModelStore
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.*
import com.menna.nabatan_7asena.testsupport.FakeUserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeUserRepository()
    private val store = ViewModelStore()
    private lateinit var viewModel: LeaderboardViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = LeaderboardViewModel(GetLeaderboardUseCase(repository), GetCurrentUserUseCase(repository), GetCurrentUserRankUseCase(repository))
        store.put("leaderboard", viewModel)
    }
    @After fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun insideTop50UsesExactListPositionWithoutAggregationEvenForTiedScores() = runTest(dispatcher) {
        val entries = (1..50).map { entry(if (it == 50) repository.user.value.id else "user-$it") }
        repository.leaderboard.value = Resource.Success(entries)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertEquals(50L, viewModel.uiState.value.currentUserRank)
        assertEquals(2, viewModel.uiState.value.currentUser?.completedKhatmahs)
        assertEquals(entries, viewModel.uiState.value.list)
        assertTrue(repository.rankRequests.isEmpty())
    }

    @Test fun outsideTop50DisplaysAggregatedRankAndSavedProfileWithoutChangingList() = runTest(dispatcher) {
        val entries = (1..50).map { entry("user-$it") }
        repository.leaderboard.value = Resource.Success(entries)
        repository.user.value = repository.user.value.copy(completedKhatmahs = 4, avatarIndex = 2)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertEquals(124L, viewModel.uiState.value.currentUserRank)
        assertEquals(listOf(50), repository.rankRequests)
        assertEquals(repository.user.value, viewModel.uiState.value.currentUser)
        assertEquals(entries, viewModel.uiState.value.list)
        assertFalse(viewModel.uiState.value.isRankLoading)
    }

    @Test fun pendingLookupKeepsListAndProfileVisible() = runTest(dispatcher) {
        val pending = CompletableDeferred<Resource<Long>>()
        repository.fetchRank = { pending.await() }
        val entries = listOf(entry("other"))
        repository.leaderboard.value = Resource.Success(entries)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertTrue(viewModel.uiState.value.isRankLoading)
        assertEquals(entries, viewModel.uiState.value.list)
        assertEquals(repository.user.value, viewModel.uiState.value.currentUser)
        pending.complete(Resource.Success(51L))
        runCurrent()
        assertEquals(51L, viewModel.uiState.value.currentUserRank)
    }

    @Test fun failureDoesNotInventRankOrRemovePodium() = runTest(dispatcher) {
        val entries = (1..50).map { entry("user-$it") }
        repository.leaderboard.value = Resource.Success(entries)
        repository.fetchRank = { Resource.Error(IllegalStateException("offline")) }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertNull(viewModel.uiState.value.currentUserRank)
        assertTrue(viewModel.uiState.value.isRankUnavailable)
        assertFalse(viewModel.uiState.value.isRankLoading)
        assertEquals(entries, viewModel.uiState.value.list)
    }

    @Test fun waitsForTop50BeforeRequestingOutsideRank() = runTest(dispatcher) {
        repository.leaderboard.value = Resource.Loading
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertTrue(repository.rankRequests.isEmpty())
        repository.leaderboard.value = Resource.Success(listOf(entry(repository.user.value.id)))
        runCurrent()
        assertEquals(1L, viewModel.uiState.value.currentUserRank)
        assertTrue(repository.rankRequests.isEmpty())
    }

    @Test fun newerScoreCancelsOldLookupAndCannotBeOverwrittenByItsResult() = runTest(dispatcher) {
        val pending = CompletableDeferred<Resource<Long>>()
        var cancelled = false
        repository.fetchRank = { score ->
            if (score == 50) try { pending.await() } finally { cancelled = true }
            else Resource.Success(80L)
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        repository.user.value = repository.user.value.copy(totalStars = 100)
        runCurrent()
        assertTrue(cancelled)
        pending.complete(Resource.Success(124L))
        runCurrent()
        assertEquals(80L, viewModel.uiState.value.currentUserRank)
        assertEquals(100, viewModel.uiState.value.currentUser?.totalStars)
        assertEquals(listOf(50, 100), repository.rankRequests)
    }

    @Test fun guestDoesNotQueryRankOrDisplayRegisteredChildCard() = runTest(dispatcher) {
        repository.user.value = repository.user.value.copy(id = "local")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertNull(viewModel.uiState.value.currentUser)
        assertTrue(repository.rankRequests.isEmpty())
    }

    @Test fun top50FailureStillAllowsIndependentCurrentUserRank() = runTest(dispatcher) {
        repository.leaderboard.value = Resource.Error(IllegalStateException("list unavailable"))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        runCurrent()
        assertEquals(124L, viewModel.uiState.value.currentUserRank)
        assertTrue(viewModel.uiState.value.list.isEmpty())
    }

    private fun entry(id: String) = LeaderboardEntry(id, "طفل", User.Gender.BOY, 100, 2, rank = 1)
}
