# Technical Specifications - Ora

## 1. Architecture

### 1.1 Clean Architecture

Ora follows Clean Architecture principles to ensure separation of concerns, testability, and maintainability. The codebase is organized into four distinct layers, each with clear responsibilities and dependencies flowing inward.

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation                         │
│         (Screens, ViewModels, UI Components)            │
├─────────────────────────────────────────────────────────┤
│                      Domain                             │
│            (Use Cases, Models, Interfaces)              │
├─────────────────────────────────────────────────────────┤
│                       Data                              │
│      (Repositories, API Services, Mappers, DTOs)        │
├─────────────────────────────────────────────────────────┤
│                       Core                              │
│        (Utils, Error Handling, DI Modules)              │
└─────────────────────────────────────────────────────────┘
```

**Core Layer** contains utilities, constants, error handling mechanisms, and dependency injection modules. This layer has no dependencies on other layers and provides foundational components used throughout the application.

**Data Layer** implements the repository interfaces defined in the domain layer. It handles all external data operations including API calls, local storage, and data mapping. DTOs are converted to domain models through dedicated mapper classes, ensuring the domain layer remains independent of external data representations.

**Domain Layer** represents the business logic of the application. It defines the core models (User, Agent, Session, Message), repository interfaces, and use cases. This layer is completely independent of frameworks and external libraries, making it highly testable and portable.

**Presentation Layer** handles all UI-related concerns using Jetpack Compose. ViewModels manage UI state and coordinate with use cases to perform business operations. The layer implements the MVI pattern for predictable state management.

### 1.2 MVI Pattern

The application uses the Model-View-Intent (MVI) pattern for state management in the presentation layer.

**Why MVI over MVVM?**

MVI was chosen over MVVM for several technical reasons specific to Ora's requirements:

The first consideration is **unidirectional data flow**. In a chat application with real-time streaming, predictable state changes are critical. MVI enforces a single direction: Intent → Model → View, eliminating ambiguity about how and when state changes occur. MVVM's bidirectional binding can lead to unpredictable state updates when handling rapid SSE events.

The second reason is **state immutability**. MVI uses immutable state objects, making it trivial to compare previous and current states. This is essential for optimizing Compose recomposition during streaming, where content updates multiple times per second.

The third advantage is **debugging and reproducibility**. Every state transition in MVI is triggered by an explicit Intent. This creates a clear audit trail, making it easier to debug issues in complex flows like tool call sequences or authentication refreshes.

**Implementation Structure:**

```kotlin
// State - Immutable data class representing UI state
data class ChatState(
    val messages: List<Message>,
    val isStreaming: Boolean,
    val streamingContent: String,
    val error: String?
)

// Intent - Sealed interface for user actions
sealed interface ChatIntent {
    data class SendMessage(val content: String) : ChatIntent
    object CancelStreaming : ChatIntent
}

// Effect - One-time events (navigation, toasts)
sealed interface ChatEffect {
    object ScrollToBottom : ChatEffect
    data class ShowError(val message: String) : ChatEffect
}
```

---

## 2. API Interaction

### 2.1 Authentication Endpoints

| Endpoint | Method | Description | Returns |
|----------|--------|-------------|---------|
| `/auth/login` | POST | Authenticates user with email and password | User data + access token |
| `/auth/register` | POST | Creates new user account | User data + access token |
| `/auth/logout` | POST | Invalidates current session | Success status |
| `/auth/refresh` | POST | Refreshes expired access token | New access token |
| `/auth/me` | GET | Retrieves current user information | User profile data |
| `/auth/me` | DELETE | Deletes user account permanently | Success status |
| `/auth/me/profile-picture` | POST | Uploads user profile picture (multipart) | New profile picture URL |

### 2.2 Agent Endpoints

| Endpoint | Method | Description | Returns |
|----------|--------|-------------|---------|
| `/agents` | GET | Lists all available agents | Array of agents with metadata |
| `/agents/{type}` | GET | Gets specific agent information | Agent details (name, description, capabilities) |

### 2.3 Session Endpoints

| Endpoint | Method | Description | Returns |
|----------|--------|-------------|---------|
| `/agents/{type}/sessions` | GET | Lists user sessions for an agent | Array of sessions with metadata |
| `/agents/{type}/sessions` | POST | Creates new conversation session | Session ID and metadata |
| `/agents/{type}/sessions/{id}` | GET | Gets session with message history | Session details + message array |
| `/agents/{type}/sessions/{id}` | DELETE | Deletes a conversation session | Success status |
| `/agents/{type}/sessions/{id}/message` | POST | Sends message and initiates streaming | Stream ID for SSE connection |

---

## 3. SSE Streaming

### 3.1 Overview

Server-Sent Events (SSE) is used for real-time streaming of AI responses. Unlike WebSockets, SSE provides a simpler unidirectional protocol perfectly suited for the chat use case where only the server needs to push data to the client.

### 3.2 Connection Flow

```
1. Client sends message via POST /agents/{type}/sessions/{id}/message
2. Server returns stream ID
3. Client opens SSE connection to /stream/{streamId}
4. Server pushes events as AI generates response
5. Connection closes on "done" or "close" event
```

### 3.3 Event Types

| Event | Description |
|-------|-------------|
| `delta` | Incremental content token from AI response (streaming mode enabled) |
| `reasoning` | AI reasoning/thinking content (optional display) |
| `tool_call` | Agent initiating a tool execution |
| `tool_response` | Result returned from tool execution |
| `message` | Complete message in one block (streaming mode disabled) |
| `thinking_start` | AI begins reasoning phase |
| `thinking_end` | AI completes reasoning phase |
| `preprocessing` | Request preprocessing in progress |
| `postprocessing` | Response postprocessing in progress |
| `error` | Error occurred during processing |
| `done` | Stream completed successfully |
| `close` | Connection should be closed |
| `heartbeat` | Keep-alive signal |

### 3.4 Implementation

SSE is implemented using OkHttp's EventSource client rather than Ktor, as Ktor does not natively support SSE. The `SSEClient` class manages connection lifecycle, event parsing, and error handling.

```kotlin
class SSEClient(private val tokenManager: TokenManager) {
    fun connect(streamId: String, sessionId: String): Flow<StreamEvent>
    fun close()
}
```

Events are parsed by `SSEEventMapper` and converted to sealed `StreamEvent` classes, enabling type-safe handling in the ViewModel.

---

## 4. Markdown Rendering

### 4.1 Architecture

Markdown rendering combines the Markwon library with custom syntax highlighting. Since Jetpack Compose lacks native Markdown support, rendering is performed in an `AndroidView` wrapper containing a `TextView`.

### 4.2 Components

**MarkdownText** - Composable wrapper that creates and configures Markwon instance, handles theme changes, and bridges Compose with Android Views.

**CodeBlock** - Custom component for displaying code blocks with syntax highlighting, language label, and copy-to-clipboard functionality.

**SyntaxHighlighter** - Custom regex-based highlighter supporting 13 languages. While Prism4j is available in dependencies, a custom implementation was chosen to avoid annotation processing complexity and provide finer control over the Gruvbox color scheme.

### 4.3 Supported Languages

Kotlin, Java, Python, JavaScript, TypeScript, Go, Rust, C, C++, Swift, SQL, JSON, XML/HTML

### 4.4 Color Scheme

The syntax highlighter uses the Gruvbox color palette with full support for both light and dark themes. Colors are defined for keywords, strings, numbers, comments, functions, types, operators, variables, and properties.

---

## 5. Libraries

### 5.1 Ktor Client (3.0.2)

**Purpose:** HTTP client for REST API communication.

**Why:** Kotlin-first design with native coroutine support. Multiplatform compatibility for potential future iOS development. Plugin architecture allows clean separation of concerns (auth, logging, serialization).

**Used in:** `AuthApiService`, `AgentApiService` for all REST endpoints. Configured with OkHttp engine for Android optimization.

### 5.2 OkHttp SSE (4.12.0)

**Purpose:** Server-Sent Events client for real-time streaming.

**Why:** Ktor does not support SSE natively. OkHttp provides a robust, well-tested EventSource implementation with automatic reconnection handling.

**Used in:** `SSEClient` for streaming AI responses.

### 5.3 Hilt (2.58)

**Purpose:** Dependency injection framework.

**Why:** Official Android DI solution with compile-time verification. Seamless integration with ViewModels and Compose navigation. Reduces boilerplate compared to manual DI or Dagger.

**Used in:** All layers - provides repositories, use cases, ViewModels, and infrastructure components.

### 5.4 Kotlinx Serialization (1.7.3)

**Purpose:** JSON serialization/deserialization.

**Why:** Kotlin-native solution with compile-time safety. No reflection required, resulting in faster performance. Integrates directly with Ktor.

**Used in:** All DTOs in data layer, SSE event parsing.

### 5.5 Jetpack Compose (BOM 2025.01.00)

**Purpose:** Declarative UI framework.

**Why:** Modern Android UI toolkit with reactive paradigm. Excellent integration with Kotlin and coroutines. Reduces UI code complexity compared to XML layouts.

**Used in:** All screens and UI components.

### 5.6 DataStore (1.1.1)

**Purpose:** Key-value storage for preferences.

**Why:** Replacement for SharedPreferences with coroutine support. Type-safe API prevents runtime errors. Handles data consistency automatically.

**Used in:** `ThemePreferences`, `LanguagePreferences` for storing user settings.

### 5.7 Security Crypto (1.1.0-alpha06)

**Purpose:** Encrypted storage for sensitive data.

**Why:** Android Keystore-backed encryption. Transparent encryption/decryption without manual key management.

**Used in:** `TokenManager` for secure storage of JWT access tokens.

### 5.8 Coil (3.0.4)

**Purpose:** Image loading library.

**Why:** Kotlin-first with native Compose support. Lightweight compared to Glide/Picasso. Uses Ktor for network requests, sharing connection pool.

**Used in:** Profile pictures, agent avatars throughout the app.

### 5.9 Markwon (4.6.2)

**Purpose:** Markdown rendering in Android Views.

**Why:** Highly extensible with plugin architecture. Supports tables, strikethrough, HTML, and syntax highlighting. Active maintenance.

**Used in:** `MarkdownText` component for rendering AI responses with rich formatting.

### 5.10 Kotlin Coroutines (1.9.0)

**Purpose:** Asynchronous programming framework.

**Why:** Native Kotlin solution for concurrent operations. Structured concurrency prevents resource leaks. Flow API provides reactive streams.

**Used in:** All async operations - network calls, database access, state management.

---

## 6. Testing

### 6.1 Testing Strategy

The application follows a comprehensive testing strategy organized in four tiers based on criticality:

**Tier 1 - Critical:** Security-sensitive components and complex parsing logic (SSE events, session reconstruction, authentication interceptor).

**Tier 2 - High Priority:** Business logic in use cases that orchestrate application behavior.

**Tier 3 - Medium Priority:** Data layer components including mappers and repository implementations.

**Tier 4 - Lower Priority:** Presentation layer ViewModels with UI state management.

### 6.2 Test Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit | 4.13.2 | Test framework |
| MockK | 1.13.10 | Kotlin mocking |
| Truth | 1.4.2 | Fluent assertions |
| Turbine | 1.1.0 | Flow testing |
| Coroutines Test | 1.9.0 | Coroutine testing utilities |

### 6.3 Test Coverage

| Layer | Components | Test Files | Tests |
|-------|-----------|------------|-------|
| Core | Utils, Error Mapping, Network | 4 | 30 |
| Domain | Use Cases, Models | 14 | 72 |
| Data | Mappers, Repository | 5 | 69 |
| Presentation | ViewModels | 3 | 48 |
| **Total** | - | **26** | **219** |

### 6.4 Test Structure

```
app/src/test/java/com/ora/app/
├── core/
│   ├── error/
│   │   └── ErrorMapperTest.kt
│   ├── network/
│   │   └── AuthInterceptorTest.kt
│   └── util/
│       ├── ResultTest.kt
│       └── DateTimeUtilTest.kt
├── data/
│   ├── mapper/
│   │   ├── SSEEventMapperTest.kt
│   │   ├── SessionMapperTest.kt
│   │   ├── UserMapperTest.kt
│   │   └── AgentMapperTest.kt
│   └── repository/
│       └── AuthRepositoryImplTest.kt
├── domain/
│   ├── model/
│   │   └── SessionInteractionsTest.kt
│   └── usecase/
│       ├── auth/
│       │   ├── LoginUseCaseTest.kt
│       │   ├── RegisterUseCaseTest.kt
│       │   ├── GetCurrentUserUseCaseTest.kt
│       │   ├── LogoutUseCaseTest.kt
│       │   ├── DeleteAccountUseCaseTest.kt
│       │   └── UploadProfilePictureUseCaseTest.kt
│       ├── agent/
│       │   └── GetAgentsUseCaseTest.kt
│       └── session/
│           ├── GetSessionsUseCaseTest.kt
│           ├── CreateSessionUseCaseTest.kt
│           ├── DeleteSessionUseCaseTest.kt
│           ├── GetSessionHistoryUseCaseTest.kt
│           ├── SendMessageUseCaseTest.kt
│           └── StreamResponseUseCaseTest.kt
└── presentation/
    └── features/
        ├── auth/
        │   └── AuthViewModelTest.kt
        ├── chat/
        │   └── ChatViewModelTest.kt
        └── profile/
            └── UserProfileViewModelTest.kt
```

### 6.5 Critical Test Descriptions

**SSEEventMapperTest (39 tests):** Validates parsing of all 13+ SSE event types including delta content accumulation, tool call/response structures, error handling with codes, and graceful degradation for malformed JSON.

**SessionInteractionsTest (15 tests):** Tests the complex algorithm that reconstructs user-assistant interaction pairs from raw message history, including tool call matching, pending status detection, and metadata conversion.

**AuthInterceptorTest (12 tests):** Verifies token injection for authenticated requests, public endpoint bypassing, 401 response handling, and concurrent refresh prevention using atomic flags.

### 6.6 Testing Patterns

**Use Case Tests:** Each use case is tested with mocked repository dependencies. Tests verify input validation, successful execution paths, error propagation, and correct repository method invocation.

```kotlin
@Test
fun `returns error when message is blank`() = runTest {
    val result = useCase("assistant", "session_1", "")
    assertThat(result.errorOrNull())
        .isInstanceOf(AppError.Validation.FieldRequired::class.java)
}
```

**ViewModel Tests:** ViewModels are tested using `UnconfinedTestDispatcher` for synchronous execution. Turbine is used to test Flow emissions (effects).

```kotlin
@Test
fun `Login with valid credentials navigates to chat`() = runTest {
    coEvery { loginUseCase(any(), any()) } returns Result.success(mockUser)

    viewModel.effect.test {
        viewModel.dispatch(AuthIntent.Login)
        assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToChat)
    }
}
```

**Mapper Tests:** Data mappers are tested to ensure correct field mapping between DTOs and domain models, with special attention to nullable fields and nested objects.

### 6.7 Running Tests

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific test class
./gradlew testDebugUnitTest --tests "com.ora.app.data.mapper.SSEEventMapperTest"
```

---

## 7. Future Evolution

The Ora platform has been designed with extensibility in mind, and several enhancements are planned for future development phases. This section presents the two major features on the roadmap along with additional improvements.

### 7.1 Multimodal Support

The current architecture already supports multimodal interactions at the backend level, with existing endpoints for attachment uploads that are not yet integrated into the mobile application. The next major release will enable users to enrich their conversations with images, documents, and audio content.

Users will be able to attach images directly to their messages, providing visual context that agents can analyze and reference in their responses. Document support will include PDF, Word, and plain text formats, allowing users to share technical documentation or reference materials during conversations. Voice input through audio recording will offer an alternative interaction mode, with server-side transcription converting speech to text before processing.

From a technical perspective, this feature requires implementing an `AttachmentRepository` to handle file upload and download operations, adding multipart form data support to the Ktor client, and creating new UI components for attachment selection and preview. File compression and validation will ensure optimal performance and security.

### 7.2 Knowledge Base Management

One of the most powerful upcoming features is the exposure of the vector knowledge base to end users. The backend already maintains a dedicated vector database for each agent and user, enabling contextual retrieval during conversations. Future versions will provide a complete interface for users to manage their personal knowledge base.

Users will be able to upload documents that become part of their personal context, viewable and searchable through an in-app browser. When agents reference information from the knowledge base during responses, the interface will display which documents contributed to the answer, providing transparency into the retrieval process. Bulk import and export operations will facilitate migration and backup of knowledge bases.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /knowledge/documents | List all documents in user's knowledge base |
| POST | /knowledge/documents | Upload a new document |
| GET | /knowledge/documents/{id} | Retrieve document content and metadata |
| DELETE | /knowledge/documents/{id} | Remove document from knowledge base |
| GET | /knowledge/search | Search documents by semantic similarity |

The implementation will leverage the existing server-side embedding pipeline for vector generation. Document chunking strategies will need to balance retrieval precision with context coherence. Preview rendering will require format-specific handlers, such as PDF.js for PDF documents and the existing Markwon renderer for markdown files. Storage quotas and document size limits will be defined to ensure fair resource allocation.

### 7.3 Additional Improvements

Beyond these major features, several quality-of-life improvements are planned. Offline mode will cache recent conversations locally using Room database, allowing users to browse their history without network connectivity. Push notifications will alert users when agents complete long-running tasks or when collaborative sessions receive new messages.

Collaborative sessions will enable users to share conversations with colleagues, facilitating team workflows around AI interactions. Agent customization will allow power users to define custom system prompts and adjust model parameters for specific use cases. Finally, an analytics dashboard will provide insights into usage patterns, helping users understand their interaction history and optimize their workflows.
