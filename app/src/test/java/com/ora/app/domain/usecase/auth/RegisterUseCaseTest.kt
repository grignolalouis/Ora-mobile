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

class RegisterUseCaseTest {

    private lateinit var useCase: RegisterUseCase
    private lateinit var authRepository: AuthRepository

    private val mockUser = User(
        id = "123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = false,
        profilePictureUrl = null
    )

    @Before
    fun setup() {
        authRepository = mockk()
        useCase = RegisterUseCase(authRepository)
        mockkObject(ValidationUtils)
    }

    @After
    fun tearDown() {
        unmockkObject(ValidationUtils)
    }

    @Test
    fun `returns error when name is blank`() = runTest {
        val result = useCase("", "test@test.com", "password123")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldRequired::class.java)
    }

    @Test
    fun `returns error when name is too long`() = runTest {
        val longName = "a".repeat(51)
        val result = useCase(longName, "test@test.com", "password123")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.FieldTooLong::class.java)
    }

    @Test
    fun `returns error when email is invalid`() = runTest {
        every { ValidationUtils.isValidEmail("invalid") } returns false

        val result = useCase("Test", "invalid", "password123")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.InvalidEmail::class.java)
    }

    @Test
    fun `returns error when password is too short`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        every { ValidationUtils.isValidPassword("short") } returns false

        val result = useCase("Test", "test@test.com", "short")
        assertThat(result.errorOrNull()).isInstanceOf(AppError.Validation.InvalidPassword::class.java)
    }

    @Test
    fun `returns user on success`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        every { ValidationUtils.isValidPassword("password123") } returns true
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(mockUser)

        val result = useCase("Test User", "test@test.com", "password123")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.name).isEqualTo("Test User")
    }
}
