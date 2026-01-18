package com.ora.app.presentation.features.auth

import com.ora.app.core.error.AppError
import com.ora.app.core.error.toUserMessage
import com.ora.app.domain.usecase.auth.LoginUseCase
import com.ora.app.domain.usecase.auth.RegisterUseCase
import com.ora.app.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : MviViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail -> updateEmail(intent.email)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateName -> updateName(intent.name)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            AuthIntent.TogglePasswordVisibility -> togglePasswordVisibility()
            AuthIntent.Login -> login()
            AuthIntent.Register -> register()
            AuthIntent.ClearErrors -> clearErrors()
        }
    }

    private fun updateEmail(email: String) {
        setState { copy(email = email, emailError = null) }
    }

    private fun updatePassword(password: String) {
        setState { copy(password = password, passwordError = null) }
    }

    private fun updateName(name: String) {
        setState { copy(name = name, nameError = null) }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val error = if (confirmPassword.isNotEmpty() && confirmPassword != currentState.password) {
            "Passwords do not match"
        } else null
        setState { copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
    }

    private fun togglePasswordVisibility() {
        setState { copy(isPasswordVisible = !isPasswordVisible) }
    }

    private suspend fun login() {
        setState { copy(isLoading = true) }

        loginUseCase(currentState.email, currentState.password)
            .onSuccess {
                setState { copy(isLoading = false) }
                sendEffect(AuthEffect.NavigateToChat)
            }
            .onError { error ->
                setState { copy(isLoading = false) }
                handleError(error)
            }
    }

    private suspend fun register() {
        if (currentState.password != currentState.confirmPassword) {
            setState { copy(confirmPasswordError = "Passwords do not match") }
            return
        }

        setState { copy(isLoading = true) }

        registerUseCase(currentState.name, currentState.email, currentState.password)
            .onSuccess {
                setState { copy(isLoading = false) }
                sendEffect(AuthEffect.NavigateToChat)
            }
            .onError { error ->
                setState { copy(isLoading = false) }
                handleError(error)
            }
    }

    private fun handleError(error: AppError) {
        when (error) {
            is AppError.Validation.InvalidEmail -> setState { copy(emailError = "Invalid email") }
            is AppError.Validation.InvalidPassword -> setState { copy(passwordError = "Password too short") }
            is AppError.Validation.FieldRequired -> {
                when (error.field) {
                    "Email" -> setState { copy(emailError = "Email required") }
                    "Password" -> setState { copy(passwordError = "Password required") }
                    "Name" -> setState { copy(nameError = "Name required") }
                    else -> sendEffect(AuthEffect.ShowError(error.toUserMessage()))
                }
            }
            else -> sendEffect(AuthEffect.ShowError(error.toUserMessage()))
        }
    }

    private fun clearErrors() {
        setState {
            copy(
                emailError = null,
                passwordError = null,
                nameError = null,
                confirmPasswordError = null
            )
        }
    }
}
