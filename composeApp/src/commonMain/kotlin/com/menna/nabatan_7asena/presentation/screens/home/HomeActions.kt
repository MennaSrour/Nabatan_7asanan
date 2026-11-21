package com.menna.nabatan_7asena.presentation.screens.home

interface HomeActions {
    fun onTaskClicked(item: HomeUiState.UiTaskItem)
    fun retryLoadingData()
    fun playWelcomeSound()
    fun onTaskChecked(item: HomeUiState.UiTaskItem)
    fun playTaskSound(item: HomeUiState.UiTaskItem)
    fun loadSuggestions()
    fun openDailyChallenge()
    fun dismissCelebration()
    fun onAddExtraTask(title: String)
    fun closeChallenge()
    fun onCorrectAnswer()
}
