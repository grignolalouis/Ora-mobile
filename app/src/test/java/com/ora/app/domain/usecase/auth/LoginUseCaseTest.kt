package com.ora.app.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.core.validation.ValidationUtils
import com.ora.app.domain.model.User
import com.ora.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var useCase: LoginUseCase
    private lateinit var authRepository: AuthRepository

    private val mockUser = User(
        id = "123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = null
    )

    @Before
    fun setup() {
        authRepository = mockk()
        useCase = LoginUseCase(authRepository)
        mockkObject(ValidationUtils)
    }

    @After
    fun tearDown() {
        unmockkObject(ValidationUtils)
    }

    @Test
    fun `returns error when email is blank`() = runTest {
        val result = useCase("", "password123")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldRequired::class.java)
    }

    @Test
    fun `returns error when email is invalid`() = runTest {
        every { ValidationUtils.isValidEmail("invalid-email") } returns false

        val result = useCase("invalid-email", "password123")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.InvalidEmail::class.java)
    }

    @Test
    fun `returns error when password is blank`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true

        val result = useCase("test@test.com", "")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldRequired::class.java)
    }

    @Test
    fun `returns user on success`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        coEvery { authRepository.login(any(), any()) } returns Result.success(mockUser)

        val result = useCase("test@test.com", "password123")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.email).isEqualTo("test@test.com")
    }

    @Test
    fun `trims email before login`() = runTest {
        every { ValidationUtils.isValidEmail("  test@test.com  ") } returns true
        coEvery { authRepository.login("test@test.com", any()) } returns Result.success(mockUser)

        val result = useCase("  test@test.com  ", "password123")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `propagates repository error`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        coEvery { authRepository.login(any(), any()) } returns Result.error(AppError.Network.NoConnection)

        val result = useCase("test@test.com", "password123")

        assertThat(result.isError).isTrue()
        assertThat(result.errorOrNull()).isEqualTo(AppError.Network.NoConnection)
    }
}
