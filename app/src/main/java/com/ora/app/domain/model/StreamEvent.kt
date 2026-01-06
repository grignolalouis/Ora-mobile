package com.ora.app.domain.model

sealed class StreamEvent {
    data class Delta(val content: String, val accumulated: String?) : StreamEvent()
    data class Reasoning(val reasoning: String, val accumulated: String?) : StreamEvent()
    data class ToolCallEvent(val toolCalls: List<ToolCallData>) : StreamEvent()
    data class ToolResponseEvent(val responses: List<ToolResponseData>) : StreamEvent()
    data class MessageComplete(val id: String, val content: String, val usage: Usage?) : StreamEvent()
    data class Error(val error: String, val code: String?) : StreamEvent()
    data object ReasoningStart : StreamEvent()
    data object ReasoningEnd : StreamEvent()
    data object MessageStart : StreamEvent()
    data object MessageEnd : StreamEvent()
    data object Done : StreamEvent()
    data object Heartbeat : StreamEvent()
}

data class ToolCallData(
    val id: String,
    val type: String,
    val functionName: String,
    val arguments: String
)

data class ToolResponseData(
    val toolId: String,
    val content: String,
    val error: String?
)

data class Usage(
    val inputTokens: Int,
    val outputTokens: Int
)
