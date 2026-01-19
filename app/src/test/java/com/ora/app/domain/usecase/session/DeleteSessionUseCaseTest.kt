package com.ora.app.domain.usecase.session

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteSessionUseCaseTest {

    private lateinit var useCase: DeleteSessionUseCase
    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = DeleteSessionUseCase(sessionRepository)
    }

    @Test
    fun `deletes session successfully`() = runTest {
        coEvery { sessionRepository.deleteSession("assistant", "session_1") } returns Result.success(Unit)

        val result = useCase("assistant", "session_1")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { sessionRepository.deleteSession(any(), any()) } returns Result.error(AppError.Api.NotFound("Session not found"))

        val result = useCase("assistant", "non_existent")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Api.NotFound::class.java)
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        coEvery { sessionRepository.deleteSession(any(), any()) } returns Result.success(Unit)

        useCase("coder", "session_123")

        coVerify(exactly = 1) { sessionRepository.deleteSession("coder", "session_123") }
    }
}
