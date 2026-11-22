package com.menna.nabatan_7asena.presentation.screens.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.AchievementProgress
import com.menna.nabatan_7asena.domain.repository.GardenRepository
import com.menna.nabatan_7asena.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
open class BadgesViewModel(
    getCurrentUser: GetCurrentUserUseCase,
    gardenRepository: GardenRepository,
    progress: AchievementProgress
) : ViewModel() {
    val uiState = getCurrentUser().flatMapLatest { user ->
        combine(gardenRepository.observe(user.id), progress.observe(user.id)) { garden, achievements ->
            BadgesUiState(
                totalStars = user.totalStars,
                currentStreak = user.currentStreak,
                quranPartsFinished = user.quranPartsFinished,
                fajrDays = achievements.fajrDays,
                azkarDays = achievements.azkarDays,
                wateredGarden = (garden as? Resource.Success)?.data?.lastWateredDate != null,
                finishedFriday = achievements.finishedFriday
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BadgesUiState())
}
