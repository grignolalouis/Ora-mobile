package com.ora.app.domain.usecase.auth

import com.ora.app.core.util.Result
import com.ora.app.domain.repository.AuthRepository

class UploadProfilePictureUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        userId: String,
        fileName: String,
        contentType: String,
        fileBytes: ByteArray
    ): Result<String> {
        return authRepository.uploadProfilePicture(userId, fileName, contentType, fileBytes)
    }
}
