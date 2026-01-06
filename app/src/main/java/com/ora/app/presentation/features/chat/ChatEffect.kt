package com.ora.app.presentation.features.chat

import com.ora.app.presentation.mvi.UiEffect

sealed interface ChatEffect : UiEffect {
    data object ScrollToBottom : ChatEffect
    data class CopiedToClipboard(val message: String) : ChatEffect
    data class ShowToast(val message: String) : ChatEffect
    data object NavigateToLogin : ChatEffect
}
