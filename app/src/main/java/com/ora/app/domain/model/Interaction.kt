package com.ora.app.domain.model

import java.util.UUID

data class Interaction(
    val id: String = UUID.randomUUID().toString(),
    val userMessage: String,
    val assistantResponse: String = "",
    val assistantReasoning: String? = null,
    val status: InteractionStatus = InteractionStatus.PENDING,
    val feedbackState: FeedbackState = FeedbackState.NONE,
    val toolCalls: List<ToolCall> = emptyList(),
    val timestamp: String = ""
)

enum class InteractionStatus {
    PENDING,
    THINKING,
    STREAMING,
    COMPLETED,
    ERROR
}

enum class FeedbackState {
    NONE,
    POSITIVE,
    NEGATIVE
}
