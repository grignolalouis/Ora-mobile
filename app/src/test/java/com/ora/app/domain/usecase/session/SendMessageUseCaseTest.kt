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

class SendMessageUseCaseTest {

    private lateinit var useCase: SendMessageUseCase
    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = SendMessageUseCase(sessionRepository)
    }

    @Test
    fun `sends message successfully`() = runTest {
        coEvery { sessionRepository.sendMessage(any(), any(), any()) } returns Result.success("stream_123")

        val result = useCase("assistant", "session_1", "Hello!")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("stream_123")
    }

    @Test
    fun `returns error when message is blank`() = runTest {
        val result = useCase("assistant", "session_1", "")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldRequired::class.java)
    }

    @Test
    fun `returns error when message is whitespace only`() = runTest {
        val result = useCase("assistant", "session_1", "   ")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldRequired::class.java)
    }

    @Test
    fun `trims message before sending`() = runTest {
        coEvery { sessionRepository.sendMessage("assistant", "session_1", "Hello") } returns Result.success("stream_1")

        useCase("assistant", "session_1", "  Hello  ")

        coVerify(exactly = 1) { sessionRepository.sendMessage("assistant", "session_1", "Hello") }
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { sessionRepository.sendMessage(any(), any(), any()) } returns Result.error(AppError.Network.NoConnection)

        val result = useCase("assistant", "session_1", "Test message")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.NoConnection)
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        coEvery { sessionRepository.sendMessage(any(), any(), any()) } returns Result.success("stream_id")

        useCase("coder", "session_xyz", "Write code")

        coVerify(exactly = 1) { sessionRepository.sendMessage("coder", "session_xyz", "Write code") }
    }
}
