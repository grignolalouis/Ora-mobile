package com.ora.app.domain.usecase.auth

import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.core.validation.ValidationConstants
import com.ora.app.core.validation.ValidationUtils
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        if (name.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Name"))
        }
        if (name.length > ValidationConstants.MAX_NAME_LENGTH) {
            return Result.error(AppError.Validation.FieldTooLong("Name", ValidationConstants.MAX_NAME_LENGTH))
        }
        if (email.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Email"))
        }
        if (!ValidationUtils.isValidEmail(email)) {
            return Result.error(AppError.Validation.InvalidEmail())
        }
        if (password.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Password"))
        }
        if (!ValidationUtils.isValidPassword(password)) {
            return Result.error(AppError.Validation.InvalidPassword())
        }
        return authRepository.register(name.trim(), email.trim(), password)
    }
}
