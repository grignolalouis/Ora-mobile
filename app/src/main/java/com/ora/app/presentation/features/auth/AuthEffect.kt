package com.ora.app.presentation.features.auth

import com.ora.app.presentation.mvi.UiEffect

sealed interface AuthEffect : UiEffect {
    data object NavigateToChat : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}
