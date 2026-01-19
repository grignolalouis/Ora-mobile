# Pattern MVI (Model-View-Intent)

## Principe

MVI est un pattern d'architecture unidirectionnel pour la gestion d'état UI.

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│    ┌───────┐   Intent   ┌───────────┐   State   ┌───────┐     │
│    │  View │ ─────────▶ │ ViewModel │ ────────▶ │ View  │     │
│    │       │            │           │           │       │     │
│    └───────┘            └─────┬─────┘           └───────┘     │
│                               │                               │
│                           Effect                              │
│                               │                               │
│                               ▼                               │
│                        One-shot events                        │
│                  (Navigation, Toast, etc.)                    │
│                                                               │
└─────────────────────────────────────────────────────────────────┘
```

## Composants

### 1. State (Model)

Représente l'état complet de l'UI à un instant T. **Immuable**.

```kotlin
data class ChatState(
    val user: User? = null,
    val agents: List<Agent> = emptyList(),
    val selectedAgent: Agent? = null,
    val isLoadingAgents: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val activeSessionId: String? = null,
    val interactions: List<Interaction> = emptyList(),
    val inputText: String = "",
    val isStreaming: Boolean = false,
    val error: String? = null
) : UiState {
    // Computed properties
    val canSend: Boolean get() = inputText.isNotBlank() && !isStreaming
    val isWelcomeScreen: Boolean get() = interactions.isEmpty()
}
```

### 2. Intent

Actions utilisateur ou événements système. **Sealed interface**.

```kotlin
sealed interface ChatIntent : UiIntent {
    data object LoadAgents : ChatIntent
    data class SelectAgent(val agentType: String) : ChatIntent
    data class UpdateInput(val text: String) : ChatIntent
    data object SendMessage : ChatIntent
    data object CancelStreaming : ChatIntent
    data class CopyMessage(val content: String) : ChatIntent
    data object Logout : ChatIntent
}
```

### 3. Effect

Événements one-shot (non persistés dans le state).

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

## Implementation

### MviViewModel (Base class)

```kotlin
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    protected val currentState: S get() = _state.value

    fun dispatch(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)

    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
```

### ViewModel concret

```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getAgentsUseCase: GetAgentsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val streamResponseUseCase: StreamResponseUseCase
) : MviViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {

    override suspend fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadAgents -> loadAgents()
            is ChatIntent.SelectAgent -> selectAgent(intent.agentType)
            is ChatIntent.UpdateInput -> updateInput(intent.text)
            is ChatIntent.SendMessage -> sendMessage()
            is ChatIntent.CancelStreaming -> cancelStreaming()
            is ChatIntent.CopyMessage -> copyMessage(intent.content)
            is ChatIntent.Logout -> logout()
            // ...
        }
    }

    private suspend fun loadAgents() {
        setState { copy(isLoadingAgents = true) }

        getAgentsUseCase().onSuccess { agents ->
            setState { copy(agents = agents, isLoadingAgents = false) }
        }.onError { error ->
            setState { copy(isLoadingAgents = false, error = error.toUserMessage()) }
        }
    }

    private fun updateInput(text: String) {
        setState { copy(inputText = text) }
    }

    private fun copyMessage(content: String) {
        sendEffect(ChatEffect.CopiedToClipboard(content))
    }
}
```

### Composable (View)

```kotlin
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatEffect.ScrollToBottom -> { /* scroll */ }
                is ChatEffect.CopiedToClipboard -> { /* show toast */ }
                is ChatEffect.NavigateToLogin -> onLogout()
                is ChatEffect.ShowToast -> { /* show toast */ }
            }
        }
    }

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.dispatch(ChatIntent.LoadAgents)
    }

    // UI
    ChatScreenContent(
        state = state,
        onSendMessage = { viewModel.dispatch(ChatIntent.SendMessage) },
        onInputChange = { viewModel.dispatch(ChatIntent.UpdateInput(it)) },
        onAgentSelect = { viewModel.dispatch(ChatIntent.SelectAgent(it)) }
    )
}
```

## Flux complet

```
1. User clicks "Send"
         │
         ▼
2. Composable dispatches Intent
   viewModel.dispatch(ChatIntent.SendMessage)
         │
         ▼
3. ViewModel handles Intent
   handleIntent(intent) → sendMessage()
         │
         ▼
4. ViewModel updates State
   setState { copy(isSending = true) }
         │
         ▼
5. State flows to Composable
   state.collectAsStateWithLifecycle()
         │
         ▼
6. Composable recomposes
   UI shows loading indicator
         │
         ▼
7. UseCase completes
   Result.success(streamId)
         │
         ▼
8. ViewModel sends Effect
   sendEffect(ChatEffect.ScrollToBottom)
         │
         ▼
9. Composable handles Effect
   LaunchedEffect collects and scrolls
```

## Avantages MVI

| Avantage | Description |
|----------|-------------|
| **Prédictibilité** | State unique, flux unidirectionnel |
| **Testabilité** | Inputs (Intent) → Outputs (State/Effect) |
| **Debug** | State complet à tout moment |
| **Time-travel** | Historique des states possible |
| **Thread-safety** | StateFlow thread-safe |
