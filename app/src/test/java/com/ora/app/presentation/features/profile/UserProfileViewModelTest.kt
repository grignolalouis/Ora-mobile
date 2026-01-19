package com.ora.app.presentation.features.profile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ora.app.R
import com.ora.app.core.error.AppError
import com.ora.app.core.util.Result
import com.ora.app.domain.model.User
import com.ora.app.domain.usecase.auth.DeleteAccountUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.domain.usecase.auth.LogoutUseCase
import com.ora.app.domain.usecase.auth.UploadProfilePictureUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class UserProfileViewModelTest {

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var uploadProfilePictureUseCase: UploadProfilePictureUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockUser = User(
        id = "user_123",
        name = "Test User",
        email = "test@test.com",
        role = "user",
        verifiedEmail = true,
        profilePictureUrl = "https://example.com/avatar.jpg"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrentUserUseCase = mockk()
        deleteAccountUseCase = mockk()
        logoutUseCase = mockk()
        uploadProfilePictureUseCase = mockk()
        viewModel = UserProfileViewModel(
            getCurrentUserUseCase,
            deleteAccountUseCase,
            logoutUseCase,
            uploadProfilePictureUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadUser fetches user successfully`() = runTest {
        coEvery { getCurrentUserUseCase() } returns Result.success(mockUser)

        viewModel.dispatch(UserProfileIntent.LoadUser)

        assertThat(viewModel.state.value.user).isEqualTo(mockUser)
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `LoadUser sets loading state`() = runTest {
        coEvery { getCurrentUserUseCase() } returns Result.success(mockUser)

        viewModel.dispatch(UserProfileIntent.LoadUser)

        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `LoadUser handles error`() = runTest {
        coEvery { getCurrentUserUseCase() } returns Result.error(AppError.Network.NoConnection)

        viewModel.dispatch(UserProfileIntent.LoadUser)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `Logout triggers navigation`() = runTest {
        coEvery { logoutUseCase() } returns Result.success(Unit)

        viewModel.effect.test {
            viewModel.dispatch(UserProfileIntent.Logout)
            assertThat(awaitItem()).isEqualTo(UserProfileEffect.NavigateToLogin)
        }
    }

    @Test
    fun `ShowDeleteConfirmation sets flag`() = runTest {
        viewModel.dispatch(UserProfileIntent.ShowDeleteConfirmation)

        assertThat(viewModel.state.value.showDeleteConfirmation).isTrue()
    }

    @Test
    fun `HideDeleteConfirmation clears flag`() = runTest {
        viewModel.dispatch(UserProfileIntent.ShowDeleteConfirmation)
        viewModel.dispatch(UserProfileIntent.HideDeleteConfirmation)

        assertThat(viewModel.state.value.showDeleteConfirmation).isFalse()
    }

    @Test
    fun `ConfirmDeleteAccount deletes and navigates`() = runTest {
        coEvery { deleteAccountUseCase() } returns Result.success(Unit)

        viewModel.effect.test {
            viewModel.dispatch(UserProfileIntent.ConfirmDeleteAccount)
            val effect1 = awaitItem()
            assertThat(effect1).isInstanceOf(UserProfileEffect.ShowToast::class.java)
            assertThat((effect1 as UserProfileEffect.ShowToast).messageResId).isEqualTo(R.string.account_deleted)
            assertThat(awaitItem()).isEqualTo(UserProfileEffect.NavigateToLogin)
        }
    }

    @Test
    fun `ConfirmDeleteAccount handles error`() = runTest {
        coEvery { deleteAccountUseCase() } returns Result.error(AppError.Api.ServerError())

        viewModel.dispatch(UserProfileIntent.ConfirmDeleteAccount)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isDeleting).isFalse()
    }

    @Test
    fun `UploadProfilePicture success updates user`() = runTest {
        val newUrl = "https://example.com/new-avatar.jpg"
        coEvery { getCurrentUserUseCase() } returns Result.success(mockUser)
        coEvery { uploadProfilePictureUseCase(any(), any(), any(), any()) } returns Result.success(newUrl)

        viewModel.dispatch(UserProfileIntent.LoadUser)
        viewModel.dispatch(UserProfileIntent.UploadProfilePicture("avatar.jpg", "image/jpeg", byteArrayOf(1, 2, 3)))

        assertThat(viewModel.state.value.user?.profilePictureUrl).isEqualTo(newUrl)
        assertThat(viewModel.state.value.isUploadingPicture).isFalse()
    }

    @Test
    fun `UploadProfilePicture shows toast on success`() = runTest {
        val newUrl = "https://example.com/new-avatar.jpg"
        coEvery { getCurrentUserUseCase() } returns Result.success(mockUser)
        coEvery { uploadProfilePictureUseCase(any(), any(), any(), any()) } returns Result.success(newUrl)

        viewModel.dispatch(UserProfileIntent.LoadUser)

        viewModel.effect.test {
            viewModel.dispatch(UserProfileIntent.UploadProfilePicture("avatar.jpg", "image/jpeg", byteArrayOf(1, 2, 3)))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(UserProfileEffect.ShowToast::class.java)
            assertThat((effect as UserProfileEffect.ShowToast).messageResId).isEqualTo(R.string.profile_picture_updated)
        }
    }

    @Test
    fun `DismissError clears error`() = runTest {
        coEvery { getCurrentUserUseCase() } returns Result.error(AppError.Network.NoConnection)

        viewModel.dispatch(UserProfileIntent.LoadUser)
        assertThat(viewModel.state.value.error).isNotNull()

        viewModel.dispatch(UserProfileIntent.DismissError)
        assertThat(viewModel.state.value.error).isNull()
    }
}
