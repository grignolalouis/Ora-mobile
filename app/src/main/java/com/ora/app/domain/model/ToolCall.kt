package com.ora.app.domain.model

data class ToolCall(
    val id: String,
    val name: String,
    val arguments: Map<String, Any>,
    val status: ToolStatus,
    val result: String? = null,
    val error: String? = null,
    val durationMs: Long? = null
)

enum class ToolStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    ERROR
}
