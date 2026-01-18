package com.ora.app.domain.usecase.auth

import com.ora.app.core.util.Result
import com.ora.app.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> {
        return authRepository.deleteAccount()
    }
}
