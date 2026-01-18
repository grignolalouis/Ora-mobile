package com.ora.app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    val title: String? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class SendMessageRequest(
    val message: String
)
