package com.ora.app.domain.usecase.session

import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamResponseUseCase @Inject constructor(private val sessionRepository: SessionRepository) {

    operator fun invoke(agentType: String, streamId: String): Flow<StreamEvent> {
        return sessionRepository.streamResponse(agentType, streamId)
    }
}
