package com.ora.app.data.mapper

import com.ora.app.data.remote.dto.response.MessageDto
import com.ora.app.data.remote.dto.response.SessionDetailDto
import com.ora.app.data.remote.dto.response.SessionDto
import com.ora.app.data.remote.dto.response.ToolCallHistoryDto
import com.ora.app.domain.model.Message
import com.ora.app.domain.model.Session
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.model.ToolCallHistory
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

fun SessionDto.toDomain(): Session = Session(
    id = sessionId,
    userId = userId,
    agentType = agentType,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    messageCount = messageCount,
    metadata = metadata?.toMap()
)

fun SessionDetailDto.toDomain(): SessionDetail = SessionDetail(
    session = Session(
        id = sessionId,
        userId = userId,
        agentType = agentType,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        messageCount = messageCount,
        metadata = metadata?.toMap()
    ),
    history = history.map { it.toDomain() }
)

fun MessageDto.toDomain(): Message = Message(
    role = role,
    content = content,
    timestamp = timestamp,
    metadata = metadata?.toMap(),
    toolCalls = toolCalls?.map { it.toDomain() },
    toolId = toolId,
    toolName = toolName
)

fun ToolCallHistoryDto.toDomain(): ToolCallHistory = ToolCallHistory(
    id = id,
    name = name,
    arguments = arguments
)

private fun JsonObject.toMap(): Map<String, Any> = entries.associate { (k, v) ->
    k to (v.jsonPrimitive.content)
}
