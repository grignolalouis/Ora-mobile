package com.ora.app.presentation.features.chat

import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.Interaction
import com.ora.app.domain.model.Session
import com.ora.app.presentation.mvi.UiState

data class ChatState(
    // LG: Agent selection
    val agents: List<Agent> = emptyList(),
    val selectedAgent: Agent? = null,
    val isLoadingAgents: Boolean = false,

    // LG: Sessions
    val sessions: List<Session> = emptyList(),
    val activeSessionId: String? = null,
    val isLoadingSessions: Boolean = false,

    // LG: Messages
    val interactions: List<Interaction> = emptyList(),
    val isLoadingHistory: Boolean = false,

    // LG: Input
    val inputText: String = "",
    val isSending: Boolean = false,

    // LG: Streaming
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val currentStreamId: String? = null,

    // LG: Drawer
    val isDrawerOpen: Boolean = false,
    val sessionSearchQuery: String = "",

    // LG: Errors
    val error: String? = null
) : UiState {

    val isWelcomeScreen: Boolean
        get() = activeSessionId == null && interactions.isEmpty()

    val filteredSessions: List<Session>
        get() = if (sessionSearchQuery.isBlank()) {
            sessions
        } else {
            sessions.filter {
                it.title?.contains(sessionSearchQuery, ignoreCase = true) == true
            }
        }

    val canSend: Boolean
        get() = inputText.isNotBlank() && !isSending && !isStreaming

    val activeSession: Session?
        get() = sessions.find { it.id == activeSessionId }
}
