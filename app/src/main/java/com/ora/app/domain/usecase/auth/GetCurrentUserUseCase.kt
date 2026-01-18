package com.ora.app.domain.usecase.auth

import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}
