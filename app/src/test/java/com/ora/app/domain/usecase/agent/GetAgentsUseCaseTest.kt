package com.ora.app.domain.usecase.agent

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.Agent
import com.ora.app.domain.repository.AgentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetAgentsUseCaseTest {

    private lateinit var useCase: GetAgentsUseCase
    private lateinit var agentRepository: AgentRepository

    private val mockAgents = listOf(
        Agent(
            type = "assistant",
            name = "Assistant",
            description = "General purpose assistant",
            greeting = "Hello!",
            version = "1.0.0",
            capabilities = listOf("chat", "code"),
            icon = "assistant_icon"
        ),
        Agent(
            type = "coder",
            name = "Coder",
            description = "Code expert",
            greeting = "Ready to code!",
            version = "1.0.0",
            capabilities = listOf("code", "debug"),
            icon = "coder_icon"
        )
    )

    @Before
    fun setup() {
        agentRepository = mockk()
        useCase = GetAgentsUseCase(agentRepository)
    }

    @Test
    fun `returns agent list on success`() = runTest {
        coEvery { agentRepository.getAgents() } returns Result.success(mockAgents)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
        assertThat(result.getOrNull()?.get(0)?.type).isEqualTo("assistant")
    }

    @Test
    fun `returns empty list when no agents`() = runTest {
        coEvery { agentRepository.getAgents() } returns Result.success(emptyList())

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { agentRepository.getAgents() } returns Result.error(AppError.Network.NoConnection)

        val result = useCase()

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.NoConnection)
    }

    @Test
    fun `calls repository getAgents`() = runTest {
        coEvery { agentRepository.getAgents() } returns Result.success(mockAgents)

        useCase()

        coVerify(exactly = 1) { agentRepository.getAgents() }
    }
}
