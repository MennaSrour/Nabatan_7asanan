package com.menna.nabatan_7asena.presentation.screens.login

import com.menna.nabatan_7asena.domain.entity.User

data class LoginUiState(
    val name: String = "",
    val selectedGender: User.Gender = User.Gender.BOY,
    val selectedBoyAvatarIndex: Int = 0,
    val selectedGirlAvatarIndex: Int = 0,
    val isLoading: Boolean = false
)
