package com.ora.app.domain.usecase.session

import com.ora.app.core.util.Result
import com.ora.app.domain.repository.SessionRepository

class DeleteSessionUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(agentType: String, sessionId: String): Result<Unit> {
        return sessionRepository.deleteSession(agentType, sessionId)
    }
}
