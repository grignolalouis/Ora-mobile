package com.ora.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.ora.app.data.remote.dto.response.AgentDto
import org.junit.Test

class AgentMapperTest {

    @Test
    fun `AgentDto toDomain all fields`() {
        val dto = AgentDto(
            type = "assistant",
            name = "General Assistant",
            description = "A helpful AI assistant",
            greeting = "Hello! How can I help?",
            version = "1.0.0",
            capabilities = listOf("chat", "code", "analysis"),
            icon = "assistant_icon"
        )

        val domain = dto.toDomain()

        assertThat(domain.type).isEqualTo("assistant")
        assertThat(domain.name).isEqualTo("General Assistant")
        assertThat(domain.description).isEqualTo("A helpful AI assistant")
        assertThat(domain.greeting).isEqualTo("Hello! How can I help?")
        assertThat(domain.version).isEqualTo("1.0.0")
        assertThat(domain.capabilities).containsExactly("chat", "code", "analysis")
        assertThat(domain.icon).isEqualTo("assistant_icon")
    }

    @Test
    fun `AgentDto with null greeting`() {
        val dto = AgentDto(
            type = "coder",
            name = "Code Expert",
            description = "Specialized in coding",
            greeting = null,
            version = "2.0.0",
            capabilities = listOf("code"),
            icon = "coder_icon"
        )

        val domain = dto.toDomain()

        assertThat(domain.greeting).isNull()
    }

    @Test
    fun `AgentDto capabilities list mapping`() {
        val dto = AgentDto(
            type = "multi",
            name = "Multi Agent",
            description = "Multiple capabilities",
            greeting = "Hi",
            version = "1.0.0",
            capabilities = listOf("a", "b", "c", "d"),
            icon = "multi_icon"
        )

        val domain = dto.toDomain()

        assertThat(domain.capabilities).hasSize(4)
        assertThat(domain.capabilities).containsExactly("a", "b", "c", "d").inOrder()
    }

    @Test
    fun `AgentDto empty capabilities list`() {
        val dto = AgentDto(
            type = "basic",
            name = "Basic Agent",
            description = "Basic functionality",
            greeting = "Hello",
            version = "0.1.0",
            capabilities = emptyList(),
            icon = "basic_icon"
        )

        val domain = dto.toDomain()

        assertThat(domain.capabilities).isEmpty()
    }
}
