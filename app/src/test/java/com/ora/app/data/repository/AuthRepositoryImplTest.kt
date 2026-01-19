package com.ora.app.data.repository

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.storage.TokenManager
import com.ora.app.data.remote.api.AuthApiService
import com.ora.app.data.remote.dto.request.LoginRequest
import com.ora.app.data.remote.dto.request.RegisterRequest
import com.ora.app.data.remote.dto.response.AccessTokenDto
import com.ora.app.data.remote.dto.response.AuthResponse
import com.ora.app.data.remote.dto.response.BaseResponse
import com.ora.app.data.remote.dto.response.ProfilePictureResponse
import com.ora.app.data.remote.dto.response.TokensDto
import com.ora.app.data.remote.dto.response.UserDto
import com.ora.app.data.remote.dto.response.UserResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private lateinit var api: AuthApiService
    private lateinit var tokenManager: TokenManager

    private val mockUserDto = UserDto(
        id = "user_123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = null
    )

    private val mockTokensDto = TokensDto(
        access = AccessTokenDto(
            token = "access_token_123",
            expires = "2025-01-01T00:00:00Z"
        )
    )

    private val mockAuthResponse = AuthResponse(
        code = 200,
        status = "success",
        message = "Login successful",
        user = mockUserDto,
        tokens = mockTokensDto
    )

    @Before
    fun setup() {
        api = mockk()
        tokenManager = mockk(relaxed = true)
        repository = AuthRepositoryImpl(api, tokenManager)
    }

    @Test
    fun `login success saves tokens`() = runTest {
        coEvery { api.login(any()) } returns mockAuthResponse

        val result = repository.login("test@test.com", "password123")

        assertThat(result.isSuccess).isTrue()
        verify { tokenManager.accessToken = "access_token_123" }
    }

    @Test
    fun `login success returns user`() = runTest {
        coEvery { api.login(any()) } returns mockAuthResponse

        val result = repository.login("test@test.com", "password123")

        assertThat(result.getOrNull()?.email).isEqualTo("test@test.com")
        assertThat(result.getOrNull()?.name).isEqualTo("Test User")
    }

    @Test
    fun `login error mapped correctly`() = runTest {
        coEvery { api.login(any()) } throws IOException("Network error")

        val result = repository.login("test@test.com", "password")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Network::class.java)
    }

    @Test
    fun `register success saves tokens`() = runTest {
        coEvery { api.register(any()) } returns mockAuthResponse

        val result = repository.register("Test", "test@test.com", "password123")

        assertThat(result.isSuccess).isTrue()
        verify { tokenManager.accessToken = "access_token_123" }
    }

    @Test
    fun `register success returns user`() = runTest {
        coEvery { api.register(any()) } returns mockAuthResponse

        val result = repository.register("Test User", "test@test.com", "password123")

        assertThat(result.getOrNull()?.name).isEqualTo("Test User")
    }

    @Test
    fun `logout clears tokens`() = runTest {
        coEvery { api.logout() } returns BaseResponse(200, "success", "Logged out")

        repository.logout()

        verify { tokenManager.clear() }
    }

    @Test
    fun `logout clears tokens even on error`() = runTest {
        coEvery { api.logout() } throws IOException("Network error")

        repository.logout()

        verify { tokenManager.clear() }
    }

    @Test
    fun `getCurrentUser returns user`() = runTest {
        coEvery { api.getCurrentUser() } returns UserResponse(
            code = 200,
            status = "success",
            message = "User retrieved",
            user = mockUserDto
        )

        val result = repository.getCurrentUser()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.id).isEqualTo("user_123")
    }

    @Test
    fun `deleteAccount success clears tokens`() = runTest {
        coEvery { api.deleteAccount() } returns BaseResponse(200, "success", "Account deleted")

        val result = repository.deleteAccount()

        assertThat(result.isSuccess).isTrue()
        verify { tokenManager.clear() }
    }

    @Test
    fun `uploadProfilePicture success`() = runTest {
        coEvery { api.uploadProfilePicture(any(), any(), any(), any()) } returns ProfilePictureResponse(
            code = 200,
            status = "success",
            message = "Uploaded",
            profilePictureUrl = "https://cdn.example.com/avatar.jpg"
        )

        val result = repository.uploadProfilePicture(
            "user_123",
            "avatar.jpg",
            "image/jpeg",
            byteArrayOf(1, 2, 3)
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("https://cdn.example.com/avatar.jpg")
    }

    @Test
    fun `refreshTokens updates storage`() = runTest {
        coEvery { api.refreshTokens() } returns mockAuthResponse

        val result = repository.refreshTokens()

        assertThat(result.isSuccess).isTrue()
        verify { tokenManager.accessToken = "access_token_123" }
    }
}
