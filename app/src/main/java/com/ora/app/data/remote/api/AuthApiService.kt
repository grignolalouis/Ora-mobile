package com.ora.app.data.remote.api

import com.ora.app.data.remote.dto.request.LoginRequest
import com.ora.app.data.remote.dto.request.RegisterRequest
import com.ora.app.data.remote.dto.response.AuthResponse
import com.ora.app.data.remote.dto.response.BaseResponse
import com.ora.app.data.remote.dto.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse =
        client.post("auth/login") { setBody(request) }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        client.post("auth/register") { setBody(request) }.body()

    suspend fun logout(): BaseResponse =
        client.post("auth/logout").body()

    suspend fun refreshTokens(): AuthResponse =
        client.post("auth/refresh-tokens").body()

    suspend fun getCurrentUser(): UserResponse =
        client.get("users/me").body()

    suspend fun deleteAccount(): BaseResponse =
        client.delete("users/me").body()
}
