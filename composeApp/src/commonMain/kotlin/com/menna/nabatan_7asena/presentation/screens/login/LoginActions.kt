package com.menna.nabatan_7asena.presentation.screens.login

import com.menna.nabatan_7asena.domain.entity.User

interface LoginActions {
    fun onNameChange(name: String)
    fun onGenderSelected(gender: User.Gender)
    fun onAvatarSelected(index: Int)
    fun onStartClicked()
}
