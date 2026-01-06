package com.ora.app.domain.usecase.auth

import com.ora.app.core.storage.TokenManager
import com.ora.app.core.util.Result
import com.ora.app.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {

    suspend operator fun invoke(): Result<Unit> {
        val result = authRepository.logout()
        tokenManager.clear()
        return result
    }
}
