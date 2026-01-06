package com.ora.app.presentation.features.auth

import com.ora.app.presentation.mvi.UiIntent

sealed interface AuthIntent : UiIntent {
    data class UpdateEmail(val email: String) : AuthIntent
    data class UpdatePassword(val password: String) : AuthIntent
    data class UpdateName(val name: String) : AuthIntent
    data class UpdateConfirmPassword(val confirmPassword: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object Login : AuthIntent
    data object Register : AuthIntent
    data object ClearErrors : AuthIntent
}
