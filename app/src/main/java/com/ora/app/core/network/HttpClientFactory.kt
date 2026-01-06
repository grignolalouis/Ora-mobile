package com.ora.app.core.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(tokenProvider: () -> String?): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT
            requestTimeoutMillis = ApiConfig.READ_TIMEOUT
            socketTimeoutMillis = ApiConfig.READ_TIMEOUT
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.BODY
        }

        defaultRequest {
            url(ApiConfig.fullBaseUrl)
            contentType(ContentType.Application.Json)
            tokenProvider()?.let { token ->
                headers.append("Authorization", "Bearer $token")
            }
        }
    }

    // LG: SSE client sans timeout court
    fun createForSSE(tokenProvider: () -> String?): HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = ApiConfig.SSE_TIMEOUT
            socketTimeoutMillis = ApiConfig.SSE_TIMEOUT
        }

        defaultRequest {
            url(ApiConfig.fullBaseUrl)
            tokenProvider()?.let { token ->
                headers.append("Authorization", "Bearer $token")
            }
        }
    }
}
