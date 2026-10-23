package com.menna.nabatan_7asena.presentation.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabatan_7asena.domain.usecase.GetLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    getLeaderboardUseCase: GetLeaderboardUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiMessage = Channel<LeaderboardEffect>(Channel.BUFFERED)
    val effects = _uiMessage.receiveAsFlow()

    val uiState = combine(
        getLeaderboardUseCase(),
        getCurrentUserUseCase()
    ) { leaderboardRes, currentUser ->
        when (leaderboardRes) {
            is Resource.Loading -> LeaderboardUiState(isLoading = true)
            is Resource.Success -> {
                val cleanList = leaderboardRes.data.distinctBy { it.userId }
                LeaderboardUiState(
                    list = cleanList,
                    currentUserId = currentUser.id,
                    isLoading = false
                )
            }
            is Resource.Error -> {
                sendMessage(leaderboardRes.throwable?.message ?: "فشل تحميل البيانات")
                LeaderboardUiState(isLoading = false)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeaderboardUiState())
    private suspend fun sendMessage(message: String) {
        _uiMessage.send(LeaderboardEffect.ShowMessage(message))
    }

}
