package com.ora.app.domain.usecase.session

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.repository.SessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class StreamResponseUseCaseTest {

    private lateinit var useCase: StreamResponseUseCase
    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setup() {
        sessionRepository = mockk()
        useCase = StreamResponseUseCase(sessionRepository)
    }

    @Test
    fun `returns flow from repository`() = runTest {
        val events = flowOf(
            StreamEvent.MessageStart,
            StreamEvent.Delta("Hello", "Hello"),
            StreamEvent.Done
        )
        every { sessionRepository.streamResponse("assistant", "stream_1") } returns events

        val flow = useCase("assistant", "stream_1")

        flow.test {
            assertThat(awaitItem()).isEqualTo(StreamEvent.MessageStart)
            assertThat(awaitItem()).isInstanceOf(StreamEvent.Delta::class.java)
            assertThat(awaitItem()).isEqualTo(StreamEvent.Done)
            awaitComplete()
        }
    }

    @Test
    fun `emits delta events with content`() = runTest {
        val events = flowOf(
            StreamEvent.Delta("Hello ", "Hello "),
            StreamEvent.Delta("World", "Hello World")
        )
        every { sessionRepository.streamResponse(any(), any()) } returns events

        val flow = useCase("assistant", "stream_1")

        flow.test {
            val first = awaitItem() as StreamEvent.Delta
            assertThat(first.content).isEqualTo("Hello ")

            val second = awaitItem() as StreamEvent.Delta
            assertThat(second.content).isEqualTo("World")
            assertThat(second.accumulated).isEqualTo("Hello World")

            awaitComplete()
        }
    }

    @Test
    fun `emits error events`() = runTest {
        val events = flowOf(
            StreamEvent.Error("Rate limit exceeded", "rate_limit")
        )
        every { sessionRepository.streamResponse(any(), any()) } returns events

        val flow = useCase("assistant", "stream_1")

        flow.test {
            val error = awaitItem() as StreamEvent.Error
            assertThat(error.message).isEqualTo("Rate limit exceeded")
            assertThat(error.code).isEqualTo("rate_limit")
            awaitComplete()
        }
    }

    @Test
    fun `calls repository with correct parameters`() = runTest {
        every { sessionRepository.streamResponse(any(), any()) } returns flowOf()

        useCase("coder", "stream_xyz")

        verify(exactly = 1) { sessionRepository.streamResponse("coder", "stream_xyz") }
    }
}
