package com.ora.app.presentation.features.chat

import androidx.lifecycle.viewModelScope
import com.ora.app.core.error.toUserMessage
import com.ora.app.domain.model.FeedbackState
import com.ora.app.domain.model.Interaction
import com.ora.app.domain.model.InteractionStatus
import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.model.ToolCall
import com.ora.app.domain.model.ToolStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.ora.app.domain.usecase.agent.GetAgentsUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.domain.usecase.auth.LogoutUseCase
import com.ora.app.presentation.components.toast.ToastType
import com.ora.app.domain.usecase.session.CreateSessionUseCase
import com.ora.app.domain.usecase.session.DeleteSessionUseCase
import com.ora.app.domain.usecase.session.GetSessionHistoryUseCase
import com.ora.app.domain.usecase.session.GetSessionsUseCase
import com.ora.app.domain.usecase.session.SendMessageUseCase
import com.ora.app.domain.usecase.session.StreamResponseUseCase
import com.ora.app.presentation.mvi.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getAgentsUseCase: GetAgentsUseCase,
    private val getSessionsUseCase: GetSessionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val streamResponseUseCase: StreamResponseUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : MviViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {

    private var streamJob: Job? = null
    private var lastAction: ChatIntent? = null

    override suspend fun handleIntent(intent: ChatIntent) {
        when (intent) {
            ChatIntent.LoadUser -> loadUser()
            ChatIntent.LoadAgents -> loadAgents()
            is ChatIntent.SelectAgent -> selectAgent(intent.agentType)
            ChatIntent.LoadSessions -> loadSessions()
            is ChatIntent.SelectSession -> selectSession(intent.sessionId)
            ChatIntent.NewChat -> newChat()
            is ChatIntent.DeleteSession -> deleteSession(intent.sessionId)
            is ChatIntent.RenameSession -> { /* TODO */ }
            is ChatIntent.UpdateInput -> updateInput(intent.text)
            ChatIntent.SendMessage -> sendMessage()
            ChatIntent.CancelStreaming -> cancelStreaming()
            is ChatIntent.SetFeedback -> setFeedback(intent.interactionIndex, intent.feedback)
            is ChatIntent.CopyMessage -> copyMessage(intent.content)
            ChatIntent.ToggleDrawer -> toggleDrawer()
            ChatIntent.CloseDrawer -> closeDrawer()
            is ChatIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            ChatIntent.DismissError -> dismissError()
            ChatIntent.RetryLastAction -> retryLastAction()
            ChatIntent.Logout -> logout()
        }
    }

    private suspend fun loadUser() {
        getCurrentUserUseCase()
            .onSuccess { user ->
                setState { copy(user = user) }
            }
    }

    private suspend fun loadAgents() {
        lastAction = ChatIntent.LoadAgents
        setState { copy(isLoadingAgents = true, error = null) }

        getAgentsUseCase()
            .onSuccess { agents ->
                setState { copy(agents = agents, isLoadingAgents = false) }
                // LG: Auto-select first agent
                if (agents.isNotEmpty() && currentState.selectedAgent == null) {
                    selectAgent(agents.first().type)
                }
            }
            .onError { error ->
                setState { copy(isLoadingAgents = false, error = error.toUserMessage()) }
            }
    }

    private suspend fun selectAgent(agentType: String) {
        val agent = currentState.agents.find { it.type == agentType }
        if (agent != null) {
            setState {
                copy(
                    selectedAgent = agent,
                    activeSessionId = null,
                    interactions = emptyList(),
                    streamingContent = ""
                )
            }
            loadSessions()
        }
    }

    private suspend fun loadSessions() {
        val agentType = currentState.selectedAgent?.type ?: return
        lastAction = ChatIntent.LoadSessions
        setState { copy(isLoadingSessions = true, error = null) }

        getSessionsUseCase(agentType)
            .onSuccess { sessions ->
                setState { copy(sessions = sessions, isLoadingSessions = false) }
            }
            .onError { error ->
                setState { copy(isLoadingSessions = false, error = error.toUserMessage()) }
            }
    }

    private suspend fun selectSession(sessionId: String) {
        // LG: Annuler tout stream en cours avant de changer de session
        streamJob?.cancel()
        streamJob = null

        // LG: Reset TOUTES les données liées à la session précédente
        setState {
            copy(
                activeSessionId = sessionId,
                interactions = emptyList(), // LG: Important - reset interactions pour éviter "Thinking..." flash
                isDrawerOpen = false,
                isStreaming = false,
                isSending = false,
                streamingContent = "",
                currentStreamId = null,
                error = null
            )
        }
        loadSessionHistory(sessionId)
    }

    private suspend fun loadSessionHistory(sessionId: String) {
        val agentType = currentState.selectedAgent?.type ?: return
        setState { copy(isLoadingHistory = true, error = null) }

        getSessionHistoryUseCase(agentType, sessionId)
            .onSuccess { detail ->
                setState { copy(interactions = detail.interactions, isLoadingHistory = false) }
                sendEffect(ChatEffect.ScrollToBottom)
            }
            .onError { error ->
                setState { copy(isLoadingHistory = false, error = error.toUserMessage()) }
            }
    }

    private fun newChat() {
        // LG: Annuler tout stream en cours
        streamJob?.cancel()
        streamJob = null

        setState {
            copy(
                activeSessionId = null,
                interactions = emptyList(),
                streamingContent = "",
                currentStreamId = null,
                inputText = "",
                isDrawerOpen = false,
                isStreaming = false,
                isSending = false,
                error = null
            )
        }
    }

    private suspend fun deleteSession(sessionId: String) {
        val agentType = currentState.selectedAgent?.type ?: return

        deleteSessionUseCase(agentType, sessionId)
            .onSuccess {
                setState { copy(sessions = sessions.filter { it.id != sessionId }) }
                if (currentState.activeSessionId == sessionId) {
                    newChat()
                }
                sendEffect(ChatEffect.ShowToast("Session deleted", ToastType.Success))
            }
            .onError { error ->
                sendEffect(ChatEffect.ShowToast(error.toUserMessage(), ToastType.Error))
            }
    }

    private fun updateInput(text: String) {
        setState { copy(inputText = text) }
    }

    private suspend fun sendMessage() {
        val message = currentState.inputText.trim()
        if (message.isBlank()) return

        val agentType = currentState.selectedAgent?.type ?: return

        // LG: Clear input immediately
        setState { copy(inputText = "", isSending = true, error = null) }

        // LG: Lazy session creation
        val sessionId = currentState.activeSessionId ?: run {
            createSessionUseCase(agentType, null)
                .onSuccess { session ->
                    setState {
                        copy(
                            activeSessionId = session.id,
                            sessions = listOf(session) + sessions
                        )
                    }
                }
                .onError { error ->
                    setState { copy(isSending = false, error = error.toUserMessage()) }
                    return
                }
            currentState.activeSessionId ?: return
        }

        // LG: Add user message to UI
        val newInteraction = Interaction(
            userMessage = message,
            assistantResponse = "",
            status = InteractionStatus.PENDING,
            feedbackState = FeedbackState.NONE
        )
        setState { copy(interactions = interactions + newInteraction) }
        sendEffect(ChatEffect.ScrollToBottom)

        // LG: Send message to API
        sendMessageUseCase(agentType, sessionId, message)
            .onSuccess { streamId ->
                setState { copy(isSending = false, currentStreamId = streamId) }
                startStreaming(streamId)
            }
            .onError { error ->
                updateLastInteractionStatus(InteractionStatus.ERROR)
                setState { copy(isSending = false, error = error.toUserMessage()) }
            }
    }

    private fun startStreaming(streamId: String) {
        val agentType = currentState.selectedAgent?.type ?: return
        android.util.Log.d("ChatViewModel", "startStreaming called with streamId=$streamId, agentType=$agentType")
        streamJob?.cancel()
        setState { copy(isStreaming = true, streamingContent = "") }

        streamJob = viewModelScope.launch {
            android.util.Log.d("ChatViewModel", "Starting stream collection")
            streamResponseUseCase(agentType, streamId)
                .catch { e ->
                    updateLastInteractionStatus(InteractionStatus.ERROR)
                    setState {
                        copy(
                            isStreaming = false,
                            error = e.message ?: "Stream error"
                        )
                    }
                }
                .collect { event ->
                    handleStreamEvent(event)
                }
        }
    }

    private fun handleStreamEvent(event: StreamEvent) {
        when (event) {
            is StreamEvent.Delta -> {
                val newContent = currentState.streamingContent + event.content
                setState { copy(streamingContent = newContent) }
                updateLastInteractionResponse(newContent)
                sendEffect(ChatEffect.ScrollToBottom)
            }
            is StreamEvent.MessageComplete -> {
                updateLastInteractionStatus(InteractionStatus.COMPLETED)
                setState {
                    copy(
                        isStreaming = false,
                        streamingContent = "",
                        currentStreamId = null
                    )
                }
                // LG: Reload sessions to update message count
                viewModelScope.launch { loadSessions() }
            }
            is StreamEvent.Error -> {
                updateLastInteractionStatus(InteractionStatus.ERROR)
                setState {
                    copy(
                        isStreaming = false,
                        streamingContent = "",
                        currentStreamId = null,
                        error = event.message
                    )
                }
            }
            is StreamEvent.ThinkingStart -> {
                updateLastInteractionStatus(InteractionStatus.THINKING)
            }
            is StreamEvent.ThinkingEnd -> {
                updateLastInteractionStatus(InteractionStatus.STREAMING)
            }
            is StreamEvent.ToolCallEvent -> {
                // LG: Convertir ToolCallData en ToolCall et ajouter à l'interaction
                val newToolCalls = event.toolCalls.map { tc ->
                    val argsMap = try {
                        val jsonElement = Json.parseToJsonElement(tc.arguments)
                        jsonElement.jsonObject.mapValues { (_, v) ->
                            v.jsonPrimitive.content
                        }
                    } catch (_: Exception) {
                        mapOf("raw" to tc.arguments)
                    }
                    ToolCall(
                        id = tc.id,
                        name = tc.functionName,
                        arguments = argsMap,
                        status = ToolStatus.RUNNING
                    )
                }
                addToolCallsToLastInteraction(newToolCalls)
            }
            is StreamEvent.ToolResponseEvent -> {
                // LG: Mettre à jour les tool calls avec les résultats
                event.responses.forEach { response ->
                    updateToolCallResult(
                        toolId = response.toolId,
                        result = response.content,
                        error = response.error
                    )
                }
            }
            else -> { /* LG: Ignore other events (heartbeat, preprocessing, etc.) */ }
        }
    }

    private fun updateLastInteractionResponse(response: String) {
        val interactions = currentState.interactions.toMutableList()
        if (interactions.isNotEmpty()) {
            val last = interactions.last()
            interactions[interactions.lastIndex] = last.copy(
                assistantResponse = response,
                status = InteractionStatus.STREAMING
            )
            setState { copy(interactions = interactions) }
        }
    }

    private fun updateLastInteractionStatus(status: InteractionStatus) {
        val interactions = currentState.interactions.toMutableList()
        if (interactions.isNotEmpty()) {
            interactions[interactions.lastIndex] = interactions.last().copy(status = status)
            setState { copy(interactions = interactions) }
        }
    }

    private fun addToolCallsToLastInteraction(toolCalls: List<ToolCall>) {
        val interactions = currentState.interactions.toMutableList()
        if (interactions.isNotEmpty()) {
            val last = interactions.last()
            interactions[interactions.lastIndex] = last.copy(
                toolCalls = last.toolCalls + toolCalls
            )
            setState { copy(interactions = interactions) }
        }
    }

    private fun updateToolCallResult(toolId: String, result: String, error: String?) {
        val interactions = currentState.interactions.toMutableList()
        if (interactions.isNotEmpty()) {
            val last = interactions.last()
            val updatedToolCalls = last.toolCalls.map { tc ->
                if (tc.id == toolId) {
                    tc.copy(
                        status = if (error != null) ToolStatus.ERROR else ToolStatus.SUCCESS,
                        result = result,
                        error = error
                    )
                } else tc
            }
            interactions[interactions.lastIndex] = last.copy(toolCalls = updatedToolCalls)
            setState { copy(interactions = interactions) }
        }
    }

    private fun cancelStreaming() {
        streamJob?.cancel()
        streamJob = null
        updateLastInteractionStatus(InteractionStatus.COMPLETED)
        setState {
            copy(
                isStreaming = false,
                streamingContent = "",
                currentStreamId = null
            )
        }
    }

    private fun setFeedback(interactionIndex: Int, feedback: FeedbackState) {
        val interactions = currentState.interactions.toMutableList()
        if (interactionIndex in interactions.indices) {
            val current = interactions[interactionIndex]
            val newFeedback = if (current.feedbackState == feedback) {
                FeedbackState.NONE
            } else {
                feedback
            }
            interactions[interactionIndex] = current.copy(feedbackState = newFeedback)
            setState { copy(interactions = interactions) }
        }
    }

    private fun copyMessage(content: String) {
        sendEffect(ChatEffect.CopiedToClipboard(content))
    }

    private fun toggleDrawer() {
        setState { copy(isDrawerOpen = !isDrawerOpen) }
    }

    private fun closeDrawer() {
        setState { copy(isDrawerOpen = false) }
    }

    private fun updateSearchQuery(query: String) {
        setState { copy(sessionSearchQuery = query) }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    private suspend fun retryLastAction() {
        lastAction?.let { handleIntent(it) }
    }

    private suspend fun logout() {
        logoutUseCase()
        sendEffect(ChatEffect.NavigateToLogin)
    }

    override fun onCleared() {
        super.onCleared()
        streamJob?.cancel()
    }
}
