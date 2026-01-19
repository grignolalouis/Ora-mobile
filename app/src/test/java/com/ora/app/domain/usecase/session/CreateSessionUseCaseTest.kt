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

class CreateSessionUseCaseTest {

    private lateinit var useCase: CreateSessionUseCase
    private lateinit var sessionRepository: SessionRepository

    private val mockSession = Session(
        id = "session_new",
        userId = "user_1",
        agentType = "assistant",
        title = "New Chat",
        createdAt = "2024-01-15T00:00:00Z",
        updatedAt = "2024-01-15T00:00:00Z",
        messageCount = 0,
        metadata = null
    )

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = CreateSessionUseCase(sessionRepository)
    }

    @Test
    fun `creates session successfully`() = runTest {
        coEvery { sessionRepository.createSession("assistant", any()) } returns Result.success(mockSession)

        val result = useCase("assistant")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.id).isEqualTo("session_new")
    }

    @Test
    fun `creates session with title`() = runTest {
        val sessionWithTitle = mockSession.copy(title = "My Custom Title")
        coEvery { sessionRepository.createSession("assistant", "My Custom Title") } returns Result.success(sessionWithTitle)

        val result = useCase("assistant", "My Custom Title")

        assertThat(result.getOrNull()?.title).isEqualTo("My Custom Title")
    }

    @Test
    fun `creates session without title`() = runTest {
        coEvery { sessionRepository.createSession("coder", null) } returns Result.success(mockSession.copy(title = null))

        val result = useCase("coder", null)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { sessionRepository.createSession(any(), any()) } returns Result.error(AppError.Api.ServerError())

        val result = useCase("assistant")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Api.ServerError::class.java)
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        coEvery { sessionRepository.createSession(any(), any()) } returns Result.success(mockSession)

        useCase("coder", "Code session")

        coVerify(exactly = 1) { sessionRepository.createSession("coder", "Code session") }
    }
}
