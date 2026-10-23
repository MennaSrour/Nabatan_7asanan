package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BadgesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {

            getCurrentUserUseCase().collect { user: User ->
                _uiState.value = BadgesUiState(
                    name = user.name,
                    totalStars = user.totalStars,
                    currentStreak = user.currentStreak,
                    treeStage = user.treeStage,
                    quranPartsFinished = user.quranPartsFinished
                )
            }
        }
    }
}
