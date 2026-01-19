# Feature: Chat

## Vue d'ensemble

La feature Chat est le cœur de l'application. Elle permet d'interagir avec différents agents IA via une interface de chat en temps réel utilisant Server-Sent Events (SSE) pour le streaming des réponses.

## Écrans

### Welcome Screen

```
┌─────────────────────────────────────────┐
│ ☰                           👤          │
├─────────────────────────────────────────┤
│                                         │
│           Bonjour, {name}!              │
│                                         │
│    Comment puis-je vous aider ?         │
│                                         │
│    ┌─────────┐  ┌─────────┐            │
│    │Assistant│  │  Code   │            │
│    │   🤖    │  │   💻    │            │
│    └─────────┘  └─────────┘            │
│                                         │
│    ┌─────────┐  ┌─────────┐            │
│    │ Writer  │  │Analyst  │            │
│    │   ✍️    │  │   📊    │            │
│    └─────────┘  └─────────┘            │
│                                         │
├─────────────────────────────────────────┤
│ [Message input...               ] [>]   │
└─────────────────────────────────────────┘
```

### Chat Screen

```
┌─────────────────────────────────────────┐
│ ☰  Session Title              👤        │
├─────────────────────────────────────────┤
│                                         │
│    ┌─────────────────────────────┐     │
│    │ User message               │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ 🤖 Assistant response...   │     │
│    │                             │     │
│    │ ```kotlin                   │     │
│    │ fun hello() = "Hi"         │     │
│    │ ```                         │     │
│    │                             │     │
│    │ [👍] [👎] [📋]             │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Another user message       │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ 🤖 ████████░░ Thinking...  │     │
│    └─────────────────────────────┘     │
│                                         │
├─────────────────────────────────────────┤
│ [Type a message...             ] [>]    │
└─────────────────────────────────────────┘
```

### Session Drawer

```
┌──────────────────┬──────────────────────┐
│                  │                      │
│  Sessions        │                      │
│                  │                      │
│  [🔍 Search...]  │                      │
│                  │                      │
│  📅 Today        │                      │
│  ├─ Session 1    │                      │
│  └─ Session 2    │        CHAT         │
│                  │       SCREEN        │
│  📅 Yesterday    │                      │
│  └─ Session 3    │                      │
│                  │                      │
│  📅 Last 7 days  │                      │
│  ├─ Session 4    │                      │
│  └─ Session 5    │                      │
│                  │                      │
│  [+ New Chat]    │                      │
│                  │                      │
└──────────────────┴──────────────────────┘
```

## Architecture MVI

### State

```kotlin
data class ChatState(
    // User
    val user: User? = null,

    // Agents
    val agents: List<Agent> = emptyList(),
    val selectedAgent: Agent? = null,
    val isLoadingAgents: Boolean = false,

    // Sessions
    val sessions: List<Session> = emptyList(),
    val activeSessionId: String? = null,
    val isLoadingSessions: Boolean = false,

    // Interactions
    val interactions: List<Interaction> = emptyList(),
    val isLoadingHistory: Boolean = false,

    // Input & Streaming
    val inputText: String = "",
    val isSending: Boolean = false,
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val currentStreamId: String? = null,

    // UI
    val isDrawerOpen: Boolean = false,
    val sessionSearchQuery: String = "",
    val error: String? = null
) : UiState {
    val isWelcomeScreen: Boolean get() = interactions.isEmpty()
    val filteredSessions: List<Session> get() = sessions.filter { ... }
    val canSend: Boolean get() = inputText.isNotBlank() && !isStreaming
    val activeSession: Session? get() = sessions.find { it.id == activeSessionId }
}
```

### Intents

```kotlin
sealed interface ChatIntent : UiIntent {
    // Data loading
    data object LoadUser : ChatIntent
    data object LoadAgents : ChatIntent
    data object LoadSessions : ChatIntent

    // Agent selection
    data class SelectAgent(val agentType: String) : ChatIntent

    // Session management
    data class SelectSession(val sessionId: String) : ChatIntent
    data object NewChat : ChatIntent
    data class DeleteSession(val sessionId: String) : ChatIntent

    // Messaging
    data class UpdateInput(val text: String) : ChatIntent
    data object SendMessage : ChatIntent
    data object CancelStreaming : ChatIntent

    // Feedback
    data class SetFeedback(val interactionIndex: Int, val feedback: FeedbackState) : ChatIntent
    data class CopyMessage(val content: String) : ChatIntent

    // UI
    data object ToggleDrawer : ChatIntent
    data object CloseDrawer : ChatIntent
    data class UpdateSearchQuery(val query: String) : ChatIntent

    // Error handling
    data object DismissError : ChatIntent
    data object RetryLastAction : ChatIntent

    // Auth
    data object Logout : ChatIntent
}
```

### Effects

```kotlin
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
```

## Streaming SSE

### Flux de streaming

```
┌─────────────────────────────────────────────────────────────────┐
│                      STREAMING FLOW                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│    User sends message                                           │
│          │                                                      │
│          ▼                                                      │
│    ┌─────────────┐                                             │
│    │ SendMessage │  POST /agents/{type}/sessions/{id}/message  │
│    │   UseCase   │ ───────────────────────────────────────────▶│
│    └──────┬──────┘                                             │
│           │                                                     │
│           │ Returns streamId                                    │
│           ▼                                                     │
│    ┌─────────────┐                                             │
│    │StreamResponse│ GET /agents/{type}/stream/{streamId}       │
│    │   UseCase   │ ◀────────────────────────────────────(SSE)──│
│    └──────┬──────┘                                             │
│           │                                                     │
│           │ Flow<StreamEvent>                                   │
│           ▼                                                     │
│    ┌─────────────────────────────────────────────────────────┐ │
│    │                    Event Types                          │ │
│    ├─────────────────────────────────────────────────────────┤ │
│    │  thinking_start  → Show "Thinking..." indicator         │ │
│    │  delta           → Append content to response           │ │
│    │  reasoning       → Store reasoning (collapsible)        │ │
│    │  tool_call       → Show tool execution UI               │ │
│    │  tool_response   → Update tool result                   │ │
│    │  thinking_end    → Hide thinking indicator              │ │
│    │  message_complete→ Finalize response                    │ │
│    │  done            → Close stream                         │ │
│    └─────────────────────────────────────────────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### StreamEvent types

```kotlin
sealed class StreamEvent {
    data class Delta(val content: String) : StreamEvent()
    data class Reasoning(val reasoning: String) : StreamEvent()
    data class ToolCallEvent(val toolCalls: List<ToolCallData>) : StreamEvent()
    data class ToolResponseEvent(val responses: List<ToolResponseData>) : StreamEvent()
    data class MessageComplete(val id: String, val content: String) : StreamEvent()
    data class Error(val message: String) : StreamEvent()
    data object ThinkingStart : StreamEvent()
    data object ThinkingEnd : StreamEvent()
    data object Done : StreamEvent()
    // ...
}
```

## Interaction Model

```kotlin
data class Interaction(
    val id: String,
    val userMessage: String,
    val assistantResponse: String,
    val assistantReasoning: String?,
    val status: InteractionStatus,
    val feedbackState: FeedbackState,
    val toolCalls: List<ToolCall>,
    val timestamp: String
)

enum class InteractionStatus {
    PENDING,    // Message sent, waiting
    THINKING,   // AI is thinking
    STREAMING,  // Receiving response
    COMPLETED,  // Response complete
    ERROR       // Error occurred
}

enum class FeedbackState {
    NONE, POSITIVE, NEGATIVE
}
```

## Tool Calls

```kotlin
data class ToolCall(
    val id: String,
    val name: String,
    val arguments: Map<String, Any>,
    val status: ToolStatus,
    val result: String?,
    val error: String?,
    val durationMs: Long?
)

enum class ToolStatus {
    PENDING, RUNNING, SUCCESS, ERROR
}
```

## Components

| Composant | Description |
|-----------|-------------|
| `ChatScreen` | Écran principal avec drawer |
| `WelcomeContent` | Grid d'agents quand pas de session |
| `MessagesList` | LazyColumn des interactions |
| `MessagePair` | User message + Assistant response |
| `UserMessage` | Bulle message utilisateur |
| `AssistantMessage` | Bulle avec Markdown |
| `MessageInput` | Champ de saisie + bouton envoi |
| `ThinkingIndicator` | Animation "thinking..." |
| `ToolsContainer` | Affichage des tool calls |
| `SessionDrawer` | Liste des sessions |

## API Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/agents` | GET | Liste des agents |
| `/agents/{type}/sessions` | GET | Sessions d'un agent |
| `/agents/{type}/sessions` | POST | Créer session |
| `/agents/{type}/sessions/{id}` | GET | Détail session |
| `/agents/{type}/sessions/{id}` | DELETE | Supprimer session |
| `/agents/{type}/sessions/{id}/message` | POST | Envoyer message |
| `/agents/{type}/stream/{streamId}` | GET (SSE) | Stream réponse |

## Fichiers

```
presentation/features/chat/
├── ChatViewModel.kt
├── ChatState.kt
├── ChatIntent.kt
├── ChatEffect.kt
├── ChatScreen.kt
└── components/
    ├── ChatTopBar.kt
    ├── WelcomeContent.kt
    ├── SessionDrawer.kt
    ├── MessagesList.kt
    ├── MessagePair.kt
    ├── UserMessage.kt
    ├── AssistantMessage.kt
    ├── MessageInput.kt
    ├── ThinkingIndicator.kt
    └── ToolsContainer.kt

domain/usecase/session/
├── CreateSessionUseCase.kt
├── GetSessionsUseCase.kt
├── GetSessionHistoryUseCase.kt
├── DeleteSessionUseCase.kt
├── SendMessageUseCase.kt
└── StreamResponseUseCase.kt
```
