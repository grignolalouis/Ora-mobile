# Ora - Development Tracker

> Document de suivi du developpement de l'application Ora
> Derniere mise a jour: 2024-01-15

## Statut Global

| Phase | Statut | Progression |
|-------|--------|-------------|
| Phase 1 - Core Infrastructure | En cours | 30% |
| Phase 2 - Domain Layer | Non commence | 0% |
| Phase 3 - Data Layer | Non commence | 0% |
| Phase 4 - Auth Presentation | Non commence | 0% |
| Phase 5 - Chat Feature | Non commence | 0% |
| Phase 6 - Polish | Non commence | 0% |

---

## Phase 1 - Core Infrastructure

### Configuration Projet
- [x] build.gradle.kts (root) - Plugins serialization
- [x] app/build.gradle.kts - Dependencies (Ktor, Koin, Compose, etc.)
- [x] libs.versions.toml - Version catalog
- [x] AndroidManifest.xml - Permissions + cleartext traffic
- [x] network_security_config.xml - Local domains
- [x] .gitignore - Complete

### Core/Error
- [ ] `core/error/AppError.kt` - Sealed class erreurs
- [ ] `core/util/Result.kt` - Result wrapper

### Core/Network
- [ ] `core/network/ApiConfig.kt` - BASE_URL config
- [ ] `core/network/HttpClientFactory.kt` - Ktor client
- [ ] `core/network/AuthInterceptor.kt` - Bearer token

### Core/Storage
- [ ] `core/storage/TokenManager.kt` - Encrypted tokens
- [ ] `core/storage/PreferencesManager.kt` - DataStore

### Core/Utils
- [ ] `core/util/CoroutineDispatchers.kt`
- [ ] `core/util/DateTimeUtil.kt`

### Core/DI
- [ ] `core/di/CoreModule.kt`
- [ ] `core/di/NetworkModule.kt`

### Application
- [ ] `OraApplication.kt` - Koin init

---

## Phase 2 - Domain Layer

### Models
- [ ] `domain/model/User.kt`
- [ ] `domain/model/Agent.kt`
- [ ] `domain/model/Session.kt`
- [ ] `domain/model/Interaction.kt`
- [ ] `domain/model/StreamEvent.kt`
- [ ] `domain/model/ToolCall.kt`

### Repository Interfaces
- [ ] `domain/repository/AuthRepository.kt`
- [ ] `domain/repository/AgentRepository.kt`
- [ ] `domain/repository/SessionRepository.kt`

### Use Cases - Auth
- [ ] `domain/usecase/auth/LoginUseCase.kt`
- [ ] `domain/usecase/auth/RegisterUseCase.kt`
- [ ] `domain/usecase/auth/LogoutUseCase.kt`
- [ ] `domain/usecase/auth/RefreshTokenUseCase.kt`
- [ ] `domain/usecase/auth/GetCurrentUserUseCase.kt`

### Use Cases - Agent
- [ ] `domain/usecase/agent/GetAgentsUseCase.kt`
- [ ] `domain/usecase/agent/GetAgentInfoUseCase.kt`

### Use Cases - Session
- [ ] `domain/usecase/session/GetSessionsUseCase.kt`
- [ ] `domain/usecase/session/CreateSessionUseCase.kt`
- [ ] `domain/usecase/session/GetSessionHistoryUseCase.kt`
- [ ] `domain/usecase/session/DeleteSessionUseCase.kt`
- [ ] `domain/usecase/session/SendMessageUseCase.kt`
- [ ] `domain/usecase/session/StreamResponseUseCase.kt`

---

## Phase 3 - Data Layer

### DTOs Request
- [ ] `data/remote/dto/request/LoginRequest.kt`
- [ ] `data/remote/dto/request/RegisterRequest.kt`
- [ ] `data/remote/dto/request/SendMessageRequest.kt`
- [ ] `data/remote/dto/request/CreateSessionRequest.kt`

### DTOs Response
- [ ] `data/remote/dto/response/BaseResponse.kt`
- [ ] `data/remote/dto/response/AuthResponse.kt`
- [ ] `data/remote/dto/response/UserResponse.kt`
- [ ] `data/remote/dto/response/AgentResponse.kt`
- [ ] `data/remote/dto/response/SessionResponse.kt`
- [ ] `data/remote/dto/response/StreamResponse.kt`

### Mappers
- [ ] `data/mapper/UserMapper.kt`
- [ ] `data/mapper/AgentMapper.kt`
- [ ] `data/mapper/SessionMapper.kt`
- [ ] `data/mapper/SSEEventMapper.kt`

### API Services
- [ ] `data/remote/api/AuthApiService.kt`
- [ ] `data/remote/api/UserApiService.kt`
- [ ] `data/remote/api/AgentApiService.kt`

### SSE Client
- [ ] `data/remote/sse/SSEEvent.kt`
- [ ] `data/remote/sse/SSEEventParser.kt`
- [ ] `data/remote/sse/SSEClient.kt`

### Repository Implementations
- [ ] `data/repository/AuthRepositoryImpl.kt`
- [ ] `data/repository/AgentRepositoryImpl.kt`
- [ ] `data/repository/SessionRepositoryImpl.kt`

### DI Modules
- [ ] `core/di/RepositoryModule.kt`
- [ ] `core/di/UseCaseModule.kt`

---

## Phase 4 - Auth Presentation

### MVI Base
- [ ] `presentation/mvi/UiState.kt`
- [ ] `presentation/mvi/UiIntent.kt`
- [ ] `presentation/mvi/UiEffect.kt`
- [ ] `presentation/mvi/MviViewModel.kt`

### Theme
- [ ] `presentation/theme/Color.kt` (modifier)
- [ ] `presentation/theme/Type.kt` (modifier)
- [ ] `presentation/theme/Dimensions.kt`
- [ ] `presentation/theme/Theme.kt` (modifier)

### Common Components
- [ ] `presentation/components/common/LoadingIndicator.kt`
- [ ] `presentation/components/common/ErrorDisplay.kt`
- [ ] `presentation/components/common/OraTextField.kt`
- [ ] `presentation/components/common/OraButton.kt`

### Navigation
- [ ] `presentation/navigation/Routes.kt`
- [ ] `presentation/navigation/NavGraph.kt`

### Auth Feature
- [ ] `presentation/features/auth/AuthState.kt`
- [ ] `presentation/features/auth/AuthIntent.kt`
- [ ] `presentation/features/auth/AuthEffect.kt`
- [ ] `presentation/features/auth/AuthViewModel.kt`
- [ ] `presentation/features/auth/LoginScreen.kt`
- [ ] `presentation/features/auth/RegisterScreen.kt`

### MainActivity
- [ ] `MainActivity.kt` (modifier)

---

## Phase 5 - Chat Feature

### Chat MVI
- [ ] `presentation/features/chat/ChatState.kt`
- [ ] `presentation/features/chat/ChatIntent.kt`
- [ ] `presentation/features/chat/ChatEffect.kt`
- [ ] `presentation/features/chat/ChatViewModel.kt`

### Drawer Components
- [ ] `presentation/features/chat/components/drawer/SearchBar.kt`
- [ ] `presentation/features/chat/components/drawer/SessionItem.kt`
- [ ] `presentation/features/chat/components/drawer/SessionDrawer.kt`

### Message Components
- [ ] `presentation/features/chat/components/messages/UserMessage.kt`
- [ ] `presentation/features/chat/components/messages/MessageFooter.kt`
- [ ] `presentation/features/chat/components/messages/AssistantMessage.kt`
- [ ] `presentation/features/chat/components/messages/MessagePair.kt`
- [ ] `presentation/features/chat/components/messages/MessagesList.kt`
- [ ] `presentation/features/chat/components/messages/ThinkingIndicator.kt`
- [ ] `presentation/features/chat/components/messages/StreamingCursor.kt`

### Input Components
- [ ] `presentation/features/chat/components/input/MessageInput.kt`

### Screens
- [ ] `presentation/features/chat/WelcomeContent.kt`
- [ ] `presentation/features/chat/ChatScreen.kt`
- [ ] `presentation/features/chat/components/ChatTopBar.kt`

---

## Phase 6 - Polish

### Markdown
- [ ] `presentation/components/markdown/MarkdownContent.kt`
- [ ] `presentation/components/markdown/CodeBlock.kt`

### Tool Calls (P1)
- [ ] `presentation/features/chat/components/tools/ToolStatusBadge.kt`
- [ ] `presentation/features/chat/components/tools/ToolCallCard.kt`

### Error UI
- [ ] `presentation/components/common/RetryableError.kt`

### Reasoning (P1)
- [ ] `presentation/features/chat/components/messages/ReasoningBox.kt`

---

## Notes de Developpement

### Backend Local
- URL: `http://10.0.2.2:8080` (emulateur Android)
- URL: `http://192.168.x.x:8080` (device physique)

### SSE Streaming
- Timeout: 5 minutes
- Events: delta, reasoning, tool_call, tool_response, done, error
- Reconnection avec exponential backoff

### Points d'attention
1. Lazy session creation (pas de session avant le premier message)
2. Navigation Drawer = overlay (pas side-by-side)
3. Message Footer uniquement quand streaming termine
4. Agent isolation (chaque agent = ecosysteme separe)

---

---

## Tests Unitaires (au fur et a mesure)

### Dependencies a ajouter
```kotlin
// Dans libs.versions.toml
mockk = "1.13.10"
truth = "1.4.2"
turbine = "1.1.0"

// Dans app/build.gradle.kts
testImplementation("io.mockk:mockk:1.13.10")
testImplementation("com.google.truth:truth:1.4.2")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("io.ktor:ktor-client-mock:3.0.2")
```

### Tests Core
- [ ] `ErrorMapperTest.kt`
- [ ] `ResultTest.kt`
- [ ] `DateTimeUtilTest.kt`

### Tests Domain (Use Cases)
- [ ] `LoginUseCaseTest.kt`
- [ ] `RegisterUseCaseTest.kt`
- [ ] `GetSessionsUseCaseTest.kt`
- [ ] `SendMessageUseCaseTest.kt`
- [ ] `StreamResponseUseCaseTest.kt`

### Tests Data
- [ ] `UserMapperTest.kt`
- [ ] `SessionMapperTest.kt`
- [ ] `SSEEventMapperTest.kt`
- [ ] `SSEEventParserTest.kt`
- [ ] `AuthRepositoryImplTest.kt`
- [ ] `SessionRepositoryImplTest.kt`

### Tests Presentation
- [ ] `AuthViewModelTest.kt`
- [ ] `ChatViewModelTest.kt`
- [ ] `ChatStateTest.kt`

---

## References

- API Docs: `/Users/grignolalouis/Desktop/dev/Vetco/docs/ORA_API_DOCUMENTATION.md`
- Requirements: `/Users/grignolalouis/Desktop/dev/Vetco/docs/ORA_AGENT_REQUIREMENTS.md`
- Architecture: `/Users/grignolalouis/Desktop/dev/Vetco/docs/ORA_ARCHITECTURE.md`
