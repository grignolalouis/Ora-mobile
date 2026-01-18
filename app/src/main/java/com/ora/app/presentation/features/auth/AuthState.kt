package com.ora.app.presentation.features.auth

import androidx.annotation.StringRes
import com.ora.app.presentation.mvi.UiState

data class AuthState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    val isPasswordVisible: Boolean = false
) : UiState {

    val isLoginValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                emailError == null && passwordError == null

    val isRegisterValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && confirmPassword.isNotBlank() &&
                nameError == null && emailError == null &&
                passwordError == null && confirmPasswordError == null
}
