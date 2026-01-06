package com.ora.app.domain.usecase.auth

import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Email"))
        }
        if (!isValidEmail(email)) {
            return Result.error(AppError.Validation.InvalidEmail())
        }
        if (password.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Password"))
        }
        return authRepository.login(email.trim(), password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
