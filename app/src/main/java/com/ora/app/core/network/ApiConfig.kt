package com.ora.app.core.network

import com.ora.app.BuildConfig

object ApiConfig {
    val BASE_URL: String = BuildConfig.API_BASE_URL
    const val API_VERSION = "api/v1"

    val fullBaseUrl: String get() = BASE_URL

    // LG: Timeouts in ms
    const val CONNECT_TIMEOUT = 30_000L
    const val READ_TIMEOUT = 30_000L
    const val WRITE_TIMEOUT = 30_000L
    const val SSE_TIMEOUT = 300_000L // 5 min for streaming
}
