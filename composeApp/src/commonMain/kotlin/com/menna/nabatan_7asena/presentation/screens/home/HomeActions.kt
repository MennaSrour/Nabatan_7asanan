package com.menna.nabatan_7asena.presentation.screens.home

interface HomeActions {
    fun buyGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration)
    fun equipGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, equipped: Boolean)
    fun openGarden()
    fun waterGarden()
    fun onTaskClicked(item: HomeUiState.UiTaskItem)
    fun retryLoadingData()
    fun playWelcomeSound()
    fun onTaskChecked(item: HomeUiState.UiTaskItem)
    fun playTaskSound(item: HomeUiState.UiTaskItem)
    fun loadSuggestions()
    fun openDailyGift(lockedReminder: String)
    fun dismissCelebration()
    fun onAddExtraTask(title: String)
}
