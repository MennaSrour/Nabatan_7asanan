package com.menna.nabata_7asena.presentation.screens.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {


    data class UserStatsUi(
        val name: String = "",
        val totalStars: Int = 0,
        val currentStreak: Int = 0,
        val treeStage: Int = 1,
        val quranPartsFinished: Int = 0
    )

    private val _userStats = MutableStateFlow(UserStatsUi())
    val userStats = _userStats.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {

            getCurrentUserUseCase().collect { user: User ->
                _userStats.value = UserStatsUi(
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