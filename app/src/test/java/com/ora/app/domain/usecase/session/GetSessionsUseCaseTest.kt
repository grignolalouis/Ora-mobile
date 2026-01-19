package com.ora.app.domain.usecase.session

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.Session
import com.ora.app.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSessionsUseCaseTest {

    private lateinit var useCase: GetSessionsUseCase
    private lateinit var sessionRepository: SessionRepository

    private val mockSessions = listOf(
        Session(
            id = "session_1",
            userId = "user_1",
            agentType = "assistant",
            title = "First chat",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T01:00:00Z",
            messageCount = 5,
            metadata = null
        ),
        Session(
            id = "session_2",
            userId = "user_1",
            agentType = "assistant",
            title = "Second chat",
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T01:00:00Z",
            messageCount = 10,
            metadata = null
        )
    )

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = GetSessionsUseCase(sessionRepository)
    }

    @Test
    fun `returns sessions on success`() = runTest {
        coEvery { sessionRepository.getSessions("assistant") } returns Result.success(mockSessions)

        val result = useCase("assistant")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
    }

    @Test
    fun `returns empty list when no sessions`() = runTest {
        coEvery { sessionRepository.getSessions("coder") } returns Result.success(emptyList())

        val result = useCase("coder")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { sessionRepository.getSessions(any()) } returns Result.error(AppError.Network.Timeout)

        val result = useCase("assistant")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.Timeout)
    }

    @Test
    fun `calls repository with correct agent type`() = runTest {
        coEvery { sessionRepository.getSessions("coder") } returns Result.success(emptyList())

        useCase("coder")

        coVerify(exactly = 1) { sessionRepository.getSessions("coder") }
    }
}
