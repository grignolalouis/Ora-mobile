package com.ora.app.presentation.features.profile

import com.ora.app.domain.model.User
import com.ora.app.presentation.mvi.UiState

data class UserProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isUploadingPicture: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val error: String? = null
) : UiState
