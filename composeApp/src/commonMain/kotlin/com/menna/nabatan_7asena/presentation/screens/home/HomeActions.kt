package com.menna.nabatan_7asena.presentation.screens.home

interface HomeActions {
    fun buyGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration)
    fun equipGardenItem(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, equipped: Boolean)
    fun buyGardenProduct(product: com.menna.nabatan_7asena.domain.entity.GardenProduct)
    fun plantGardenTree(treeId: String, plot: Int?)
    fun showGardenTree(treeId: String, visible: Boolean)
    fun placeGardenDecoration(item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, plot: Int)
    fun manageGardenPlacement(id: String, plot: Int? = null, visible: Boolean? = null, remove: Boolean = false)
    fun openGarden()
    fun waterGarden(treeId: String? = null)
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
