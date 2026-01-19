package com.ora.app.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.storage.TokenManager
import com.ora.app.core.util.Result
import com.ora.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {

    private lateinit var useCase: LogoutUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var tokenManager: TokenManager

    @Before
    fun setup() {
        authRepository = mockk()
        tokenManager = mockk(relaxed = true)
        useCase = LogoutUseCase(authRepository, tokenManager)
    }

    @Test
    fun `calls repository logout`() = runTest {
        coEvery { authRepository.logout() } returns Result.success(Unit)

        useCase()

        coVerify(exactly = 1) { authRepository.logout() }
    }

    @Test
    fun `clears tokens on logout`() = runTest {
        coEvery { authRepository.logout() } returns Result.success(Unit)

        useCase()

        verify(exactly = 1) { tokenManager.clear() }
    }

    @Test
    fun `returns success on complete`() = runTest {
        coEvery { authRepository.logout() } returns Result.success(Unit)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `clears tokens even on logout error`() = runTest {
        coEvery { authRepository.logout() } returns Result.error(AppError.Network.NoConnection)

        useCase()

        verify(exactly = 1) { tokenManager.clear() }
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { authRepository.logout() } returns Result.error(AppError.Network.Timeout)

        val result = useCase()

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.Timeout)
    }
}
