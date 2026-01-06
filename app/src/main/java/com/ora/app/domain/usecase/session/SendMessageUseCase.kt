package com.ora.app.domain.usecase.session

import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.repository.SessionRepository

class SendMessageUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(
        agentType: String,
        sessionId: String,
        message: String
    ): Result<String> {
        if (message.isBlank()) {
            return Result.error(AppError.Validation.FieldRequired("Message"))
        }
        return sessionRepository.sendMessage(agentType, sessionId, message.trim())
    }
}
