package com.ora.app.presentation.features.profile

import com.ora.app.core.error.toUserMessage
import com.ora.app.domain.usecase.auth.DeleteAccountUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.presentation.mvi.MviViewModel

class UserProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : MviViewModel<UserProfileState, UserProfileIntent, UserProfileEffect>(UserProfileState()) {

    override suspend fun handleIntent(intent: UserProfileIntent) {
        when (intent) {
            UserProfileIntent.LoadUser -> loadUser()
            UserProfileIntent.ShowDeleteConfirmation -> showDeleteConfirmation()
            UserProfileIntent.HideDeleteConfirmation -> hideDeleteConfirmation()
            UserProfileIntent.ConfirmDeleteAccount -> deleteAccount()
            UserProfileIntent.DismissError -> dismissError()
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
}
