package com.menna.nabatan_7asena.presentation.screens.home

sealed interface HomeEffect {
    data object NavigateToDailyGift : HomeEffect
    data object NavigateToGarden : HomeEffect
    data class NavigateToQuran(val page: Int?) : HomeEffect
    data object NavigateToKhatmah : HomeEffect
    data object NavigateToSebha : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
}
