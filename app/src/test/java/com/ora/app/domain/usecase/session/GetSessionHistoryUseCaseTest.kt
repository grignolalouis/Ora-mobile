package com.ora.app.domain.usecase.session

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.Message
import com.ora.app.domain.model.Session
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSessionHistoryUseCaseTest {

    private lateinit var useCase: GetSessionHistoryUseCase
    private lateinit var sessionRepository: SessionRepository

    private val mockSession = Session(
        id = "session_1",
        userId = "user_1",
        agentType = "assistant",
        title = "Test Chat",
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T01:00:00Z",
        messageCount = 2,
        metadata = null
    )

    private val mockHistory = listOf(
        Message(
            role = "user",
            content = "Hello",
            timestamp = "2024-01-01T00:00:00Z",
            metadata = null
        ),
        Message(
            role = "assistant",
            content = "Hi there!",
            timestamp = "2024-01-01T00:00:01Z",
            metadata = null
        )
    )

    private val mockSessionDetail = SessionDetail(mockSession, mockHistory)

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = GetSessionHistoryUseCase(sessionRepository)
    }

    @Test
    fun `returns session detail on success`() = runTest {
        coEvery { sessionRepository.getSession("assistant", "session_1") } returns Result.success(mockSessionDetail)

        val result = useCase("assistant", "session_1")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.session?.id).isEqualTo("session_1")
    }

    @Test
    fun `returns history with messages`() = runTest {
        coEvery { sessionRepository.getSession(any(), any()) } returns Result.success(mockSessionDetail)

        val result = useCase("assistant", "session_1")

        assertThat(result.getOrNull()?.history).hasSize(2)
        assertThat(result.getOrNull()?.history?.get(0)?.role).isEqualTo("user")
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { sessionRepository.getSession(any(), any()) } returns Result.error(AppError.Api.NotFound("Session not found"))

        val result = useCase("assistant", "non_existent")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Api.NotFound::class.java)
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        coEvery { sessionRepository.getSession(any(), any()) } returns Result.success(mockSessionDetail)

        useCase("coder", "session_abc")

        coVerify(exactly = 1) { sessionRepository.getSession("coder", "session_abc") }
    }
}
