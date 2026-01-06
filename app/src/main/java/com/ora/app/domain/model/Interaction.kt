package com.ora.app.domain.model

import java.util.UUID

data class Interaction(
    val id: String = UUID.randomUUID().toString(),
    val userMessage: String,
    val assistantResponse: String? = null,
    val assistantReasoning: String? = null,
    val status: InteractionStatus = InteractionStatus.WAITING,
    val feedbackState: FeedbackState = FeedbackState.NONE,
    val toolCalls: List<ToolCall> = emptyList(),
    val timestamp: String
)

enum class InteractionStatus {
    WAITING,
    REASONING,
    RESPONDING,
    COMPLETE,
    ERROR
}

enum class FeedbackState {
    NONE,
    LIKED,
    DISLIKED
}
