package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserRankUseCase
import com.menna.nabatan_7asena.domain.usecase.GetLeaderboardUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
open class LeaderboardViewModel(
    getLeaderboardUseCase: GetLeaderboardUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserRankUseCase: GetCurrentUserRankUseCase
) : ViewModel() {
    private val messages = Channel<LeaderboardEffect>(Channel.BUFFERED)
    val effects = messages.receiveAsFlow()

    // A newer identity, score or leaderboard cancels the previous rank lookup.
    val uiState: StateFlow<LeaderboardUiState> = combine(
        getLeaderboardUseCase(), getCurrentUserUseCase()
    ) { leaderboard, user -> leaderboard to user }.transformLatest { (leaderboard, user) ->
        val currentUser = user.takeUnless { it.id.isBlank() || it.id == "local" }
        val state = LeaderboardUiState(
            list = (leaderboard as? Resource.Success)?.data.orEmpty().distinctBy { it.userId },
            currentUserId = user.id, currentUser = currentUser,
            isLoading = leaderboard is Resource.Loading, isRankLoading = currentUser != null
        )
        emit(state)
        if (leaderboard is Resource.Loading) return@transformLatest
        if (leaderboard is Resource.Error) {
            messages.send(LeaderboardEffect.ShowMessage(leaderboard.throwable?.message ?: "فشل تحميل البيانات"))
        }
        if (currentUser == null) return@transformLatest
        val rank = getCurrentUserRankUseCase(currentUser, state.list)
        emit(state.copy(currentUserRank = (rank as? Resource.Success)?.data,
            isRankLoading = false, isRankUnavailable = rank !is Resource.Success))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeaderboardUiState(isLoading = true))
}
