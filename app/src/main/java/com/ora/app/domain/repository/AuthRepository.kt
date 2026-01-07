package com.ora.app.domain.repository

import com.ora.app.core.util.Result
import com.ora.app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun refreshTokens(): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun uploadProfilePicture(
        userId: String,
        fileName: String,
        contentType: String,
        fileBytes: ByteArray
    ): Result<String>
}
