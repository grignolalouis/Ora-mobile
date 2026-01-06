package com.ora.app.domain.model

data class Session(
    val id: String,
    val userId: String,
    val agentType: String,
    val title: String?,
    val createdAt: String,
    val updatedAt: String,
    val messageCount: Int,
    val metadata: Map<String, Any>?
)

data class Message(
    val role: String,
    val content: String,
    val timestamp: String,
    val metadata: Map<String, Any>?
)

data class SessionDetail(
    val session: Session,
    val history: List<Message>
)
