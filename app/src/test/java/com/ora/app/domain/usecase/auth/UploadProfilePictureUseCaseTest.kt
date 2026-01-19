package com.ora.app.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UploadProfilePictureUseCaseTest {

    private lateinit var useCase: UploadProfilePictureUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        useCase = UploadProfilePictureUseCase(authRepository)
    }

    @Test
    fun `uploads picture successfully`() = runTest {
        val imageUrl = "https://example.com/images/avatar.jpg"
        coEvery {
            authRepository.uploadProfilePicture(any(), any(), any(), any())
        } returns Result.success(imageUrl)

        val result = useCase(
            userId = "user_123",
            fileName = "avatar.jpg",
            contentType = "image/jpeg",
            fileBytes = byteArrayOf(1, 2, 3)
        )

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `returns URL on success`() = runTest {
        val imageUrl = "https://example.com/images/new-avatar.jpg"
        coEvery {
            authRepository.uploadProfilePicture(any(), any(), any(), any())
        } returns Result.success(imageUrl)

        val result = useCase(
            userId = "user_123",
            fileName = "photo.png",
            contentType = "image/png",
            fileBytes = byteArrayOf(1, 2, 3, 4, 5)
        )

        assertThat(result.getOrNull()).isEqualTo(imageUrl)
    }

    @Test
    fun `handles upload error`() = runTest {
        coEvery {
            authRepository.uploadProfilePicture(any(), any(), any(), any())
        } returns Result.error(AppError.Network.Timeout)

        val result = useCase(
            userId = "user_123",
            fileName = "avatar.jpg",
            contentType = "image/jpeg",
            fileBytes = byteArrayOf(1, 2, 3)
        )

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.Timeout)
    }

    @Test
    fun `passes correct parameters to repository`() = runTest {
        coEvery {
            authRepository.uploadProfilePicture(any(), any(), any(), any())
        } returns Result.success("url")

        val userId = "user_456"
        val fileName = "profile.jpg"
        val contentType = "image/jpeg"
        val fileBytes = byteArrayOf(10, 20, 30)

        useCase(userId, fileName, contentType, fileBytes)

        coVerify(exactly = 1) {
            authRepository.uploadProfilePicture(userId, fileName, contentType, fileBytes)
        }
    }
}
