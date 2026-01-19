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

class DeleteAccountUseCaseTest {

    private lateinit var useCase: DeleteAccountUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        useCase = DeleteAccountUseCase(authRepository)
    }

    @Test
    fun `deletes account successfully`() = runTest {
        coEvery { authRepository.deleteAccount() } returns Result.success(Unit)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { authRepository.deleteAccount() } returns Result.error(AppError.Api.ServerError())

        val result = useCase()

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Api.ServerError::class.java)
    }

    @Test
    fun `calls repository deleteAccount`() = runTest {
        coEvery { authRepository.deleteAccount() } returns Result.success(Unit)

        useCase()

        coVerify(exactly = 1) { authRepository.deleteAccount() }
    }
}
