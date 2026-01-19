package com.ora.app.presentation.features.auth

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ora.app.R
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.core.validation.ValidationUtils
import com.ora.app.domain.model.User
import com.ora.app.domain.usecase.auth.LoginUseCase
import com.ora.app.domain.usecase.auth.RegisterUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockUser = User(
        id = "user_123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        registerUseCase = mockk()
        viewModel = AuthViewModel(loginUseCase, registerUseCase)
        mockkObject(ValidationUtils)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(ValidationUtils)
    }

    @Test
    fun `UpdateEmail updates state`() = runTest {
        viewModel.dispatch(AuthIntent.UpdateEmail("new@email.com"))

        assertThat(viewModel.state.value.email).isEqualTo("new@email.com")
        assertThat(viewModel.state.value.emailError).isNull()
    }

    @Test
    fun `UpdatePassword updates state`() = runTest {
        viewModel.dispatch(AuthIntent.UpdatePassword("newpassword"))

        assertThat(viewModel.state.value.password).isEqualTo("newpassword")
        assertThat(viewModel.state.value.passwordError).isNull()
    }

    @Test
    fun `UpdateName updates state`() = runTest {
        viewModel.dispatch(AuthIntent.UpdateName("John Doe"))

        assertThat(viewModel.state.value.name).isEqualTo("John Doe")
        assertThat(viewModel.state.value.nameError).isNull()
    }

    @Test
    fun `UpdateConfirmPassword updates state`() = runTest {
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.UpdateConfirmPassword("password123"))

        assertThat(viewModel.state.value.confirmPassword).isEqualTo("password123")
        assertThat(viewModel.state.value.confirmPasswordError).isNull()
    }

    @Test
    fun `UpdateConfirmPassword shows error when mismatch`() = runTest {
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.UpdateConfirmPassword("different"))

        assertThat(viewModel.state.value.confirmPasswordError).isEqualTo(R.string.passwords_not_match)
    }

    @Test
    fun `TogglePasswordVisibility toggles`() = runTest {
        assertThat(viewModel.state.value.isPasswordVisible).isFalse()

        viewModel.dispatch(AuthIntent.TogglePasswordVisibility)
        assertThat(viewModel.state.value.isPasswordVisible).isTrue()

        viewModel.dispatch(AuthIntent.TogglePasswordVisibility)
        assertThat(viewModel.state.value.isPasswordVisible).isFalse()
    }

    @Test
    fun `Login with valid credentials navigates to chat`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        coEvery { loginUseCase(any(), any()) } returns Result.success(mockUser)

        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))

        viewModel.effect.test {
            viewModel.dispatch(AuthIntent.Login)
            assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToChat)
        }
    }

    @Test
    fun `Login sets loading state`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        coEvery { loginUseCase(any(), any()) } returns Result.success(mockUser)

        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.Login)

        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `Login with invalid email shows error`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.error(AppError.Validation.InvalidEmail())

        viewModel.dispatch(AuthIntent.UpdateEmail("invalid"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.Login)

        assertThat(viewModel.state.value.emailError).isEqualTo(R.string.invalid_email)
    }

    @Test
    fun `Login with blank password shows error`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.error(AppError.Validation.FieldRequired("Password"))

        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword(""))
        viewModel.dispatch(AuthIntent.Login)

        assertThat(viewModel.state.value.passwordError).isEqualTo(R.string.password_required)
    }

    @Test
    fun `Register with password mismatch shows error`() = runTest {
        viewModel.dispatch(AuthIntent.UpdateName("Test"))
        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.UpdateConfirmPassword("different"))
        viewModel.dispatch(AuthIntent.Register)

        assertThat(viewModel.state.value.confirmPasswordError).isEqualTo(R.string.passwords_not_match)
    }

    @Test
    fun `Register with valid data navigates to chat`() = runTest {
        every { ValidationUtils.isValidEmail("test@test.com") } returns true
        every { ValidationUtils.isValidPassword("password123") } returns true
        coEvery { registerUseCase(any(), any(), any()) } returns Result.success(mockUser)

        viewModel.dispatch(AuthIntent.UpdateName("Test User"))
        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.UpdateConfirmPassword("password123"))

        viewModel.effect.test {
            viewModel.dispatch(AuthIntent.Register)
            assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToChat)
        }
    }

    @Test
    fun `ClearErrors resets error state`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.error(AppError.Validation.InvalidEmail())
        viewModel.dispatch(AuthIntent.UpdateEmail("invalid"))
        viewModel.dispatch(AuthIntent.Login)

        assertThat(viewModel.state.value.emailError).isNotNull()

        viewModel.dispatch(AuthIntent.ClearErrors)

        assertThat(viewModel.state.value.emailError).isNull()
        assertThat(viewModel.state.value.passwordError).isNull()
        assertThat(viewModel.state.value.nameError).isNull()
        assertThat(viewModel.state.value.confirmPasswordError).isNull()
    }

    @Test
    fun `isLoginValid returns true when fields are valid`() = runTest {
        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))

        assertThat(viewModel.state.value.isLoginValid).isTrue()
    }

    @Test
    fun `isLoginValid returns false when email is blank`() = runTest {
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))

        assertThat(viewModel.state.value.isLoginValid).isFalse()
    }

    @Test
    fun `isRegisterValid returns true when all fields are valid`() = runTest {
        viewModel.dispatch(AuthIntent.UpdateName("Test"))
        viewModel.dispatch(AuthIntent.UpdateEmail("test@test.com"))
        viewModel.dispatch(AuthIntent.UpdatePassword("password123"))
        viewModel.dispatch(AuthIntent.UpdateConfirmPassword("password123"))

        assertThat(viewModel.state.value.isRegisterValid).isTrue()
    }
}
