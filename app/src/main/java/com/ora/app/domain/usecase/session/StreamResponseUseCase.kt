package com.ora.app.domain.usecase.session

import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class StreamResponseUseCase(private val sessionRepository: SessionRepository) {

    operator fun invoke(streamId: String): Flow<StreamEvent> {
        return sessionRepository.streamResponse(streamId)
    }
}
