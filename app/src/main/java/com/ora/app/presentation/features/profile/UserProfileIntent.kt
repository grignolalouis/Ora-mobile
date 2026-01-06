package com.ora.app.presentation.features.profile

import com.ora.app.presentation.mvi.UiIntent

sealed class UserProfileIntent : UiIntent {
    data object LoadUser : UserProfileIntent()
    data object ShowDeleteConfirmation : UserProfileIntent()
    data object HideDeleteConfirmation : UserProfileIntent()
    data object ConfirmDeleteAccount : UserProfileIntent()
    data object DismissError : UserProfileIntent()
}
