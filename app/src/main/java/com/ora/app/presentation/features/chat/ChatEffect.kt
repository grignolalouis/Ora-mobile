package com.ora.app.presentation.features.chat

import androidx.annotation.StringRes
import com.ora.app.presentation.designsystem.components.toast.ToastType
import com.ora.app.presentation.mvi.UiEffect

sealed interface ChatEffect : UiEffect {
    data object ScrollToBottom : ChatEffect
    data class CopiedToClipboard(val message: String) : ChatEffect
    data class ShowToast(
        val message: String? = null,
        @StringRes val messageResId: Int? = null,
        val type: ToastType = ToastType.Info
    ) : ChatEffect
    data object NavigateToLogin : ChatEffect
}
