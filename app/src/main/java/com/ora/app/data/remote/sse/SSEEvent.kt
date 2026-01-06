package com.ora.app.data.remote.sse

data class SSEEvent(
    val event: String,
    val data: String,
    val id: String? = null
)
