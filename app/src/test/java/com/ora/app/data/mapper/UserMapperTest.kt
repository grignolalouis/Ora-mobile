package com.ora.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.ora.app.data.remote.dto.response.UserDto
import org.junit.Test

class UserMapperTest {

    @Test
    fun `UserDto toDomain all fields`() {
        val dto = UserDto(
            id = "user_123",
            name = "John Doe",
            email = "john@example.com",
            role = "user",
            verifiedEmail = true,
            profilePictureUrl = "https://example.com/avatar.jpg"
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isEqualTo("user_123")
        assertThat(domain.name).isEqualTo("John Doe")
        assertThat(domain.email).isEqualTo("john@example.com")
        assertThat(domain.role).isEqualTo("user")
        assertThat(domain.verifiedEmail).isTrue()
        assertThat(domain.profilePictureUrl).isEqualTo("https://example.com/avatar.jpg")
    }

    @Test
    fun `UserDto with null profilePictureUrl`() {
        val dto = UserDto(
            id = "user_456",
            name = "Jane Doe",
            email = "jane@example.com",
            role = "admin",
            verifiedEmail = false,
            profilePictureUrl = null
        )

        val domain = dto.toDomain()

        assertThat(domain.profilePictureUrl).isNull()
    }

    @Test
    fun `UserDto field mapping correctness`() {
        val dto = UserDto(
            id = "id_abc",
            name = "Test Name",
            email = "test@test.com",
            role = "moderator",
            verifiedEmail = true,
            profilePictureUrl = "https://cdn.example.com/pic.png"
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isNotEmpty()
        assertThat(domain.name).isNotEmpty()
        assertThat(domain.email).contains("@")
        assertThat(domain.role).isEqualTo("moderator")
    }

    @Test
    fun `UserDto with unverified email`() {
        val dto = UserDto(
            id = "user_789",
            name = "New User",
            email = "new@example.com",
            role = "user",
            verifiedEmail = false,
            profilePictureUrl = null
        )

        val domain = dto.toDomain()

        assertThat(domain.verifiedEmail).isFalse()
    }

    @Test
    fun `UserDto preserves all data in conversion`() {
        val dto = UserDto(
            id = "preserve_id",
            name = "Preserve Name",
            email = "preserve@email.com",
            role = "preserve_role",
            verifiedEmail = true,
            profilePictureUrl = "preserve_url"
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isEqualTo(dto.id)
        assertThat(domain.name).isEqualTo(dto.name)
        assertThat(domain.email).isEqualTo(dto.email)
        assertThat(domain.role).isEqualTo(dto.role)
        assertThat(domain.verifiedEmail).isEqualTo(dto.verifiedEmail)
        assertThat(domain.profilePictureUrl).isEqualTo(dto.profilePictureUrl)
    }
}
