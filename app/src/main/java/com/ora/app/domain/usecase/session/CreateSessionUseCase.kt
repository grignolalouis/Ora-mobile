package com.ora.app.domain.usecase.session

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Session
import com.ora.app.domain.repository.SessionRepository
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(agentType: String, title: String? = null): Result<Session> {
        return sessionRepository.createSession(agentType, title)
    }
}
