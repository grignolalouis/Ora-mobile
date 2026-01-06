package com.ora.app.domain.usecase.session

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Session
import com.ora.app.domain.repository.SessionRepository

class GetSessionsUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(agentType: String): Result<List<Session>> {
        return sessionRepository.getSessions(agentType)
    }
}
