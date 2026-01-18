package com.ora.app.domain.usecase.session

import com.ora.app.core.util.Result
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionHistoryUseCase @Inject constructor(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(agentType: String, sessionId: String): Result<SessionDetail> {
        return sessionRepository.getSession(agentType, sessionId)
    }
}
