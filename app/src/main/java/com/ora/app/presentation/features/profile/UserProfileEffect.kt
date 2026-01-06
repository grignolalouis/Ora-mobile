package com.ora.app.presentation.features.profile

import com.ora.app.presentation.mvi.UiEffect

sealed class UserProfileEffect : UiEffect {
    data object NavigateToLogin : UserProfileEffect()
    data class ShowToast(val message: String) : UserProfileEffect()
}
