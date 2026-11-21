package com.menna.nabatan_7asena.presentation.screens.login

import com.menna.nabatan_7asena.presentation.common.GenderedString

sealed class LoginEffect {
    object NavigateToHome : LoginEffect()
    data class ShowError(val message: GenderedString) : LoginEffect()
}
