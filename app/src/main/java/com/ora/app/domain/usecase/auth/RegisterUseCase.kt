package com.ora.app.domain.usecase.auth

import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        if (name.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Name"))
        }
        if (name.length > 50) {
            return Result.error(AppError.Validation.FieldTooLong("Name", 50))
        }
        if (email.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Email"))
        }
        if (!isValidEmail(email)) {
            return Result.error(AppError.Validation.InvalidEmail())
        }
        if (password.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Password"))
        }
        if (password.length < 8) {
            return Result.error(AppError.Validation.InvalidPassword())
        }
        return authRepository.register(name.trim(), email.trim(), password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
