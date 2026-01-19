package com.ora.app.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCurrentUserUseCaseTest {

    private lateinit var useCase: GetCurrentUserUseCase
    private lateinit var authRepository: AuthRepository

    private val mockUser = User(
        id = "user_123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = "https://example.com/avatar.jpg"
    )

    @Before
    fun setup() {
        authRepository = mockk()
        useCase = GetCurrentUserUseCase(authRepository)
    }

    @Test
    fun `returns user on success`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.success(mockUser)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockUser)
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.error(AppError.Auth.Unauthorized)

        val result = useCase()

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Auth.Unauthorized)
    }

    @Test
    fun `propagates network error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.error(AppError.Network.NoConnection)

        val result = useCase()

        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.NoConnection)
    }

    @Test
    fun `calls repository getCurrentUser`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.success(mockUser)

        useCase()

        coVerify(exactly = 1) { authRepository.getCurrentUser() }
    }
}
