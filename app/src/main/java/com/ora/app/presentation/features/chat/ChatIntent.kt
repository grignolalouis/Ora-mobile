package com.ora.app.presentation.features.chat

import com.ora.app.domain.model.FeedbackState
import com.ora.app.presentation.mvi.UiIntent

sealed interface ChatIntent : UiIntent {
    // LG: Init
    data object LoadUser : ChatIntent
    data object LoadAgents : ChatIntent
    data class SelectAgent(val agentType: String) : ChatIntent

    // LG: Session management
    data object LoadSessions : ChatIntent
    data class SelectSession(val sessionId: String) : ChatIntent
    data object NewChat : ChatIntent
    data class DeleteSession(val sessionId: String) : ChatIntent

    // LG: Messages
    data class UpdateInput(val text: String) : ChatIntent
    data object SendMessage : ChatIntent
    data object CancelStreaming : ChatIntent

    // LG: Feedback
    data class SetFeedback(val interactionIndex: Int, val feedback: FeedbackState) : ChatIntent
    data class CopyMessage(val content: String) : ChatIntent

    // LG: Drawer
    data object ToggleDrawer : ChatIntent
    data object CloseDrawer : ChatIntent
    data class UpdateSearchQuery(val query: String) : ChatIntent

    // LG: Errors
    data object DismissError : ChatIntent
    data object RetryLastAction : ChatIntent

    // LG: Auth
    data object Logout : ChatIntent
}
