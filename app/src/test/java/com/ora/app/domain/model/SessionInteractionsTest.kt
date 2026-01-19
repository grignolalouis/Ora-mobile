package com.ora.app.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionInteractionsTest {

    private val baseSession = Session(
        id = "session_1",
        userId = "user_1",
        agentType = "assistant",
        title = "Test Session",
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
        messageCount = 0,
        metadata = null
    )

    @Test
    fun `simple user assistant pair`() {
        val history = listOf(
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
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].userMessage).isEqualTo("Hello")
        assertThat(detail.interactions[0].assistantResponse).isEqualTo("Hi there!")
        assertThat(detail.interactions[0].status).isEqualTo(InteractionStatus.COMPLETED)
    }

    @Test
    fun `user message without assistant response is PENDING`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Hello",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].userMessage).isEqualTo("Hello")
        assertThat(detail.interactions[0].assistantResponse).isEmpty()
        assertThat(detail.interactions[0].status).isEqualTo(InteractionStatus.PENDING)
    }

    @Test
    fun `user message with tool calls in response`() {
        val history = listOf(
            Message(
                role = "user",
                content = "What is the weather?",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_123",
                        name = "get_weather",
                        arguments = """{"city": "Paris"}"""
                    )
                )
            ),
            Message(
                role = "tool",
                content = "Weather in Paris: 20C",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null,
                toolId = "call_123",
                toolName = "get_weather"
            ),
            Message(
                role = "assistant",
                content = "The weather in Paris is 20C.",
                timestamp = "2024-01-01T00:00:03Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].userMessage).isEqualTo("What is the weather?")
        assertThat(detail.interactions[0].assistantResponse).isEqualTo("The weather in Paris is 20C.")
        assertThat(detail.interactions[0].toolCalls).hasSize(1)
        assertThat(detail.interactions[0].toolCalls[0].name).isEqualTo("get_weather")
        assertThat(detail.interactions[0].toolCalls[0].status).isEqualTo(ToolStatus.SUCCESS)
        assertThat(detail.interactions[0].toolCalls[0].result).isEqualTo("Weather in Paris: 20C")
    }

    @Test
    fun `tool call without response is PENDING`() {
        val history = listOf(
            Message(
                role = "user",
                content = "What is the weather?",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_123",
                        name = "get_weather",
                        arguments = """{"city": "Paris"}"""
                    )
                )
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].toolCalls).hasSize(1)
        assertThat(detail.interactions[0].toolCalls[0].status).isEqualTo(ToolStatus.PENDING)
        assertThat(detail.interactions[0].toolCalls[0].result).isNull()
    }

    @Test
    fun `multiple tool calls in single response`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Compare weather",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_1",
                        name = "get_weather",
                        arguments = """{"city": "Paris"}"""
                    ),
                    ToolCallHistory(
                        id = "call_2",
                        name = "get_weather",
                        arguments = """{"city": "London"}"""
                    )
                )
            ),
            Message(
                role = "tool",
                content = "Paris: 20C",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null,
                toolId = "call_1",
                toolName = "get_weather"
            ),
            Message(
                role = "tool",
                content = "London: 15C",
                timestamp = "2024-01-01T00:00:03Z",
                metadata = null,
                toolId = "call_2",
                toolName = "get_weather"
            ),
            Message(
                role = "assistant",
                content = "Paris is 20C, London is 15C.",
                timestamp = "2024-01-01T00:00:04Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].toolCalls).hasSize(2)
        assertThat(detail.interactions[0].toolCalls[0].result).isEqualTo("Paris: 20C")
        assertThat(detail.interactions[0].toolCalls[1].result).isEqualTo("London: 15C")
    }

    @Test
    fun `assistant message after tool completion has content`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Check weather",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "Let me check...",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_1",
                        name = "weather",
                        arguments = "{}"
                    )
                )
            ),
            Message(
                role = "tool",
                content = "20C",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null,
                toolId = "call_1",
                toolName = "weather"
            ),
            Message(
                role = "assistant",
                content = "It's 20 degrees.",
                timestamp = "2024-01-01T00:00:03Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions[0].assistantResponse).isEqualTo("It's 20 degrees.")
    }

    @Test
    fun `multiple interactions in session`() {
        val history = listOf(
            Message(
                role = "user",
                content = "First question",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "First answer",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null
            ),
            Message(
                role = "user",
                content = "Second question",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "Second answer",
                timestamp = "2024-01-01T00:00:03Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(2)
        assertThat(detail.interactions[0].userMessage).isEqualTo("First question")
        assertThat(detail.interactions[0].assistantResponse).isEqualTo("First answer")
        assertThat(detail.interactions[1].userMessage).isEqualTo("Second question")
        assertThat(detail.interactions[1].assistantResponse).isEqualTo("Second answer")
    }

    @Test
    fun `empty message history returns empty interactions`() {
        val detail = SessionDetail(baseSession, emptyList())

        assertThat(detail.interactions).isEmpty()
    }

    @Test
    fun `malformed JSON tool arguments fallback to raw`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Test",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_1",
                        name = "tool",
                        arguments = "not valid json"
                    )
                )
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].toolCalls).hasSize(1)
        assertThat(detail.interactions[0].toolCalls[0].arguments).containsEntry("raw", "not valid json")
    }

    @Test
    fun `tool response matching by ID`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Test",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "specific_id_abc",
                        name = "my_tool",
                        arguments = "{}"
                    )
                )
            ),
            Message(
                role = "tool",
                content = "Wrong response",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null,
                toolId = "different_id",
                toolName = "other_tool"
            ),
            Message(
                role = "tool",
                content = "Correct response",
                timestamp = "2024-01-01T00:00:03Z",
                metadata = null,
                toolId = "specific_id_abc",
                toolName = "my_tool"
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions[0].toolCalls[0].result).isEqualTo("Correct response")
        assertThat(detail.interactions[0].toolCalls[0].status).isEqualTo(ToolStatus.SUCCESS)
    }

    @Test
    fun `tool arguments parsed as map`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Test",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_1",
                        name = "search",
                        arguments = """{"query": "test", "limit": "10"}"""
                    )
                )
            )
        )
        val detail = SessionDetail(baseSession, history)

        val args = detail.interactions[0].toolCalls[0].arguments
        assertThat(args).containsEntry("query", "test")
        assertThat(args).containsEntry("limit", "10")
    }

    @Test
    fun `assistant only messages are skipped at beginning`() {
        val history = listOf(
            Message(
                role = "assistant",
                content = "I am ready to help",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "user",
                content = "Hello",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "Hi!",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions).hasSize(1)
        assertThat(detail.interactions[0].userMessage).isEqualTo("Hello")
        assertThat(detail.interactions[0].assistantResponse).isEqualTo("Hi!")
    }

    @Test
    fun `multiple assistant messages per user uses first without tool calls`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Question",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "First response",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions[0].assistantResponse).isEqualTo("First response")
    }

    @Test
    fun `timestamp from user message is preserved`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Hello",
                timestamp = "2024-06-15T10:30:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "Hi!",
                timestamp = "2024-06-15T10:30:05Z",
                metadata = null
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions[0].timestamp).isEqualTo("2024-06-15T10:30:00Z")
    }

    @Test
    fun `tool message without toolId is ignored for matching`() {
        val history = listOf(
            Message(
                role = "user",
                content = "Test",
                timestamp = "2024-01-01T00:00:00Z",
                metadata = null
            ),
            Message(
                role = "assistant",
                content = "",
                timestamp = "2024-01-01T00:00:01Z",
                metadata = null,
                toolCalls = listOf(
                    ToolCallHistory(
                        id = "call_1",
                        name = "tool",
                        arguments = "{}"
                    )
                )
            ),
            Message(
                role = "tool",
                content = "Response without ID",
                timestamp = "2024-01-01T00:00:02Z",
                metadata = null,
                toolId = null,
                toolName = "tool"
            )
        )
        val detail = SessionDetail(baseSession, history)

        assertThat(detail.interactions[0].toolCalls[0].status).isEqualTo(ToolStatus.PENDING)
    }
}
