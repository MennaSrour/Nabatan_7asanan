package com.menna.nabata_7asena.presentation.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.domain.Resource
import com.menna.nabata_7asena.domain.entity.LeaderboardEntry
import com.menna.nabata_7asena.domain.usecase.GetCurrentUserUseCase
import com.menna.nabata_7asena.domain.usecase.GetLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    getLeaderboardUseCase: GetLeaderboardUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

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
                _uiMessage.emit(leaderboardRes.throwable?.message ?: "فشل تحميل البيانات")
                LeaderboardUiState(isLoading = false)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeaderboardUiState())
}

data class LeaderboardUiState(
    val list: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String = "",
    val isLoading: Boolean = false
)