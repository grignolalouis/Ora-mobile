package com.ora.app.presentation.features.profile

import com.ora.app.core.error.toUserMessage
import com.ora.app.domain.usecase.auth.DeleteAccountUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.domain.usecase.auth.LogoutUseCase
import com.ora.app.domain.usecase.auth.UploadProfilePictureUseCase
import com.ora.app.presentation.mvi.MviViewModel

class UserProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : MviViewModel<UserProfileState, UserProfileIntent, UserProfileEffect>(UserProfileState()) {

    override suspend fun handleIntent(intent: UserProfileIntent) {
        when (intent) {
            UserProfileIntent.LoadUser -> loadUser()
            UserProfileIntent.Logout -> logout()
            UserProfileIntent.ShowDeleteConfirmation -> showDeleteConfirmation()
            UserProfileIntent.HideDeleteConfirmation -> hideDeleteConfirmation()
            UserProfileIntent.ConfirmDeleteAccount -> deleteAccount()
            UserProfileIntent.DismissError -> dismissError()
            is UserProfileIntent.UploadProfilePicture -> uploadProfilePicture(
                intent.fileName,
                intent.contentType,
                intent.fileBytes
            )
        }
    }

    private suspend fun loadUser() {
        setState { copy(isLoading = true, error = null) }

        getCurrentUserUseCase()
            .onSuccess { user ->
                setState { copy(user = user, isLoading = false) }
            }
            .onError { error ->
                setState { copy(isLoading = false, error = error.toUserMessage()) }
            }
    }

    private fun showDeleteConfirmation() {
        setState { copy(showDeleteConfirmation = true) }
    }

    private fun hideDeleteConfirmation() {
        setState { copy(showDeleteConfirmation = false) }
    }

    private suspend fun deleteAccount() {
        setState { copy(isDeleting = true, showDeleteConfirmation = false) }

        deleteAccountUseCase()
            .onSuccess {
                sendEffect(UserProfileEffect.ShowToast("Account deleted"))
                sendEffect(UserProfileEffect.NavigateToLogin)
            }
            .onError { error ->
                setState { copy(isDeleting = false, error = error.toUserMessage()) }
            }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    private suspend fun logout() {
        logoutUseCase()
        sendEffect(UserProfileEffect.NavigateToLogin)
    }

    private suspend fun uploadProfilePicture(
        fileName: String,
        contentType: String,
        fileBytes: ByteArray
    ) {
        val userId = state.value.user?.id ?: return
        setState { copy(isUploadingPicture = true) }

        uploadProfilePictureUseCase(userId, fileName, contentType, fileBytes)
            .onSuccess { newUrl ->
                // Update user with new profile picture URL
                setState { copy(
                    isUploadingPicture = false,
                    user = user?.copy(profilePictureUrl = newUrl)
                )}
                sendEffect(UserProfileEffect.ShowToast("Profile picture updated"))
            }
            .onError { error ->
                setState { copy(isUploadingPicture = false) }
                sendEffect(UserProfileEffect.ShowToast(error.toUserMessage()))
            }
    }
}
