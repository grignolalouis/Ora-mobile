package com.ora.app.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamResponse(
    val code: Int,
    val status: String,
    val message: String,
    val stream: StreamInfoDto
)

@Serializable
data class StreamInfoDto(
    @SerialName("stream_id")
    val streamId: String,
    @SerialName("session_id")
    val sessionId: String,
    val message: String
)
