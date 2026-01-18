package com.ora.app.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SessionsListResponse(
    val code: Int,
    val status: String,
    val message: String,
    val sessions: List<SessionDto>
)

@Serializable
data class SessionResponse(
    val code: Int,
    val status: String,
    val message: String,
    val session: SessionDto
)

@Serializable
data class SessionDetailResponse(
    val code: Int,
    val status: String,
    val message: String,
    val session: SessionDetailDto
)

@Serializable
data class SessionDto(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("agent_type")
    val agentType: String,
    val title: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("message_count")
    val messageCount: Int,
    val metadata: JsonObject? = null
)

@Serializable
data class SessionDetailDto(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("agent_type")
    val agentType: String,
    val title: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("message_count")
    val messageCount: Int,
    val metadata: JsonObject? = null,
    val history: List<MessageDto>,
    val summary: String? = null
)

@Serializable
data class MessageDto(
    val role: String,
    val content: String,
    val timestamp: String,
    val metadata: JsonObject? = null,
    // LG: Tool calls sur les messages assistant
    @SerialName("tool_calls")
    val toolCalls: List<ToolCallHistoryDto>? = null,
    // LG: Pour les messages role='tool' (réponses d'outils)
    @SerialName("tool_id")
    val toolId: String? = null,
    @SerialName("tool_name")
    val toolName: String? = null
)

@Serializable
data class ToolCallHistoryDto(
    val id: String,
    val name: String,
    val arguments: String // JSON string
)
