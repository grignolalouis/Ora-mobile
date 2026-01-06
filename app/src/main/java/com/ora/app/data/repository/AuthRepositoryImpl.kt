package com.ora.app.data.repository

import com.ora.app.core.error.ErrorMapper
import com.ora.app.core.storage.TokenManager
import com.ora.app.core.util.DateTimeUtil
import com.ora.app.core.util.Result
import com.ora.app.data.mapper.toDomain
import com.ora.app.data.remote.api.AuthApiService
import com.ora.app.data.remote.dto.request.LoginRequest
import com.ora.app.data.remote.dto.request.RegisterRequest
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = try {
        val response = api.login(LoginRequest(email, password))
        saveTokens(response.tokens.access.token, response.tokens.access.expires)
        Result.success(response.user.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> = try {
        val response = api.register(RegisterRequest(name, email, password))
        saveTokens(response.tokens.access.token, response.tokens.access.expires)
        Result.success(response.user.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun logout(): Result<Unit> = try {
        api.logout()
        tokenManager.clear()
        Result.success(Unit)
    } catch (e: Exception) {
        tokenManager.clear()
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun refreshTokens(): Result<User> = try {
        val response = api.refreshTokens()
        saveTokens(response.tokens.access.token, response.tokens.access.expires)
        Result.success(response.user.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun getCurrentUser(): Result<User> = try {
        val response = api.getCurrentUser()
        Result.success(response.user.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun deleteAccount(): Result<Unit> = try {
        api.deleteAccount()
        tokenManager.clear()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    private fun saveTokens(token: String, expires: String) {
        tokenManager.accessToken = token
        tokenManager.tokenExpiry = DateTimeUtil.parseIsoToMillis(expires)
    }
}
