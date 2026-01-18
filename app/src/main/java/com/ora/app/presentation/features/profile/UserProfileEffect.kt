package com.ora.app.presentation.features.profile

import androidx.annotation.StringRes
import com.ora.app.presentation.mvi.UiEffect

sealed interface UserProfileEffect : UiEffect {
    data object NavigateToLogin : UserProfileEffect
    data class ShowToast(val message: String? = null, @StringRes val messageResId: Int? = null) : UserProfileEffect
}
