package com.ora.app.domain.usecase.auth

import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository

class RefreshTokenUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<User> {
        return authRepository.refreshTokens()
    }
}
