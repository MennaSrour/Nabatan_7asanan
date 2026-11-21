package com.menna.nabatan_7asena.presentation.screens.home

sealed interface HomeEffect {
    data object NavigateToQuran : HomeEffect
    data object NavigateToSebha : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
}
