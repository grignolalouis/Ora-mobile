package com.ora.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.ora.app.data.remote.dto.response.MessageDto
import com.ora.app.data.remote.dto.response.SessionDetailDto
import com.ora.app.data.remote.dto.response.SessionDto
import com.ora.app.data.remote.dto.response.ToolCallHistoryDto
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test

class SessionMapperTest {

    @Test
    fun `SessionDto toDomain mapping`() {
        val dto = SessionDto(
            sessionId = "session_123",
            userId = "user_456",
            agentType = "assistant",
            title = "Test Chat",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T01:00:00Z",
            messageCount = 10,
            metadata = null
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isEqualTo("session_123")
        assertThat(domain.userId).isEqualTo("user_456")
        assertThat(domain.agentType).isEqualTo("assistant")
        assertThat(domain.title).isEqualTo("Test Chat")
        assertThat(domain.createdAt).isEqualTo("2024-01-01T00:00:00Z")
        assertThat(domain.updatedAt).isEqualTo("2024-01-01T01:00:00Z")
        assertThat(domain.messageCount).isEqualTo(10)
    }

    @Test
    fun `SessionDto toDomain with null title`() {
        val dto = SessionDto(
            sessionId = "session_1",
            userId = "user_1",
            agentType = "coder",
            title = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            messageCount = 0,
            metadata = null
        )

        val domain = dto.toDomain()

        assertThat(domain.title).isNull()
    }

    @Test
    fun `SessionDto toDomain with metadata`() {
        val metadata = JsonObject(mapOf(
            "key1" to JsonPrimitive("value1"),
            "key2" to JsonPrimitive("value2")
        ))
        val dto = SessionDto(
            sessionId = "session_1",
            userId = "user_1",
            agentType = "assistant",
            title = "Test",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            messageCount = 0,
            metadata = metadata
        )

        val domain = dto.toDomain()

        assertThat(domain.metadata).isNotNull()
        assertThat(domain.metadata).containsEntry("key1", "value1")
        assertThat(domain.metadata).containsEntry("key2", "value2")
    }

    @Test
    fun `SessionDetailDto toDomain with history`() {
        val dto = SessionDetailDto(
            sessionId = "session_1",
            userId = "user_1",
            agentType = "assistant",
            title = "Chat",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T01:00:00Z",
            messageCount = 2,
            metadata = null,
            history = listOf(
                MessageDto(
                    role = "user",
                    content = "Hello",
                    timestamp = "2024-01-01T00:00:00Z",
                    metadata = null
                ),
                MessageDto(
                    role = "assistant",
                    content = "Hi!",
                    timestamp = "2024-01-01T00:00:01Z",
                    metadata = null
                )
            ),
            summary = null
        )

        val domain = dto.toDomain()

        assertThat(domain.session.id).isEqualTo("session_1")
        assertThat(domain.history).hasSize(2)
        assertThat(domain.history[0].role).isEqualTo("user")
        assertThat(domain.history[1].role).isEqualTo("assistant")
    }

    @Test
    fun `MessageDto toDomain with tool calls`() {
        val dto = MessageDto(
            role = "assistant",
            content = "",
            timestamp = "2024-01-01T00:00:00Z",
            metadata = null,
            toolCalls = listOf(
                ToolCallHistoryDto(
                    id = "call_123",
                    name = "get_weather",
                    arguments = """{"city": "Paris"}"""
                )
            )
        )

        val domain = dto.toDomain()

        assertThat(domain.toolCalls).hasSize(1)
        assertThat(domain.toolCalls?.get(0)?.id).isEqualTo("call_123")
        assertThat(domain.toolCalls?.get(0)?.name).isEqualTo("get_weather")
        assertThat(domain.toolCalls?.get(0)?.arguments).contains("Paris")
    }

    @Test
    fun `MessageDto toDomain without tool calls`() {
        val dto = MessageDto(
            role = "user",
            content = "Hello",
            timestamp = "2024-01-01T00:00:00Z",
            metadata = null,
            toolCalls = null
        )

        val domain = dto.toDomain()

        assertThat(domain.toolCalls).isNull()
    }

    @Test
    fun `MessageDto toDomain with tool response fields`() {
        val dto = MessageDto(
            role = "tool",
            content = "Weather: 20C",
            timestamp = "2024-01-01T00:00:00Z",
            metadata = null,
            toolId = "call_123",
            toolName = "get_weather"
        )

        val domain = dto.toDomain()

        assertThat(domain.role).isEqualTo("tool")
        assertThat(domain.toolId).isEqualTo("call_123")
        assertThat(domain.toolName).isEqualTo("get_weather")
    }

    @Test
    fun `ToolCallHistoryDto toDomain`() {
        val dto = ToolCallHistoryDto(
            id = "call_abc",
            name = "search",
            arguments = """{"query": "test"}"""
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isEqualTo("call_abc")
        assertThat(domain.name).isEqualTo("search")
        assertThat(domain.arguments).isEqualTo("""{"query": "test"}""")
    }

    @Test
    fun `empty history list`() {
        val dto = SessionDetailDto(
            sessionId = "session_1",
            userId = "user_1",
            agentType = "assistant",
            title = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            messageCount = 0,
            metadata = null,
            history = emptyList(),
            summary = null
        )

        val domain = dto.toDomain()

        assertThat(domain.history).isEmpty()
    }

    @Test
    fun `null metadata handling`() {
        val dto = SessionDto(
            sessionId = "session_1",
            userId = "user_1",
            agentType = "assistant",
            title = "Test",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            messageCount = 0,
            metadata = null
        )

        val domain = dto.toDomain()

        assertThat(domain.metadata).isNull()
    }
}
