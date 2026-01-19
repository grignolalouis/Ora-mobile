# Ora - Class Diagram

```mermaid
classDiagram

    direction TB

    class AppError {
        <<sealed>>
    }
    class AppError_Network {
        <<sealed>>
    }
    class AppError_Network_NoConnection
    class AppError_Network_Timeout
    class AppError_Network_Unknown {
        +message: String
    }
    class AppError_Auth {
        <<sealed>>
    }
    class AppError_Auth_InvalidCredentials
    class AppError_Auth_TokenExpired
    class AppError_Auth_Unauthorized
    class AppError_Auth_EmailNotVerified
    class AppError_Auth_Unknown {
        +message: String
    }
    class AppError_Validation {
        <<sealed>>
    }
    class AppError_Validation_InvalidEmail {
        +message: String
    }
    class AppError_Validation_InvalidPassword {
        +message: String
    }
    class AppError_Validation_FieldRequired {
        +field: String
    }
    class AppError_Validation_FieldTooLong {
        +field: String
        +maxLength: Int
    }
    class AppError_Api {
        <<sealed>>
    }
    class AppError_Api_BadRequest {
        +message: String
    }
    class AppError_Api_NotFound {
        +message: String
    }
    class AppError_Api_Conflict {
        +message: String
    }
    class AppError_Api_ServerError {
        +message: String
    }
    class AppError_Api_RateLimited {
        +message: String
    }
    class AppError_Api_Unknown {
        +code: Int
        +message: String
    }
    class AppError_Stream {
        <<sealed>>
    }
    class AppError_Stream_SessionNotFound {
        +sessionId: String
    }
    class AppError_Stream_AgentNotFound {
        +agentType: String
    }
    class AppError_Stream_StreamingFailed {
        +message: String
    }
    class AppError_Stream_StreamDisconnected
    class AppError_Stream_ParseError {
        +message: String
    }
    class AppError_Unknown {
        +message: String
    }

    AppError <|-- AppError_Network
    AppError <|-- AppError_Auth
    AppError <|-- AppError_Validation
    AppError <|-- AppError_Api
    AppError <|-- AppError_Stream
    AppError <|-- AppError_Unknown
    AppError_Network <|-- AppError_Network_NoConnection
    AppError_Network <|-- AppError_Network_Timeout
    AppError_Network <|-- AppError_Network_Unknown
    AppError_Auth <|-- AppError_Auth_InvalidCredentials
    AppError_Auth <|-- AppError_Auth_TokenExpired
    AppError_Auth <|-- AppError_Auth_Unauthorized
    AppError_Auth <|-- AppError_Auth_EmailNotVerified
    AppError_Auth <|-- AppError_Auth_Unknown
    AppError_Validation <|-- AppError_Validation_InvalidEmail
    AppError_Validation <|-- AppError_Validation_InvalidPassword
    AppError_Validation <|-- AppError_Validation_FieldRequired
    AppError_Validation <|-- AppError_Validation_FieldTooLong
    AppError_Api <|-- AppError_Api_BadRequest
    AppError_Api <|-- AppError_Api_NotFound
    AppError_Api <|-- AppError_Api_Conflict
    AppError_Api <|-- AppError_Api_ServerError
    AppError_Api <|-- AppError_Api_RateLimited
    AppError_Api <|-- AppError_Api_Unknown
    AppError_Stream <|-- AppError_Stream_SessionNotFound
    AppError_Stream <|-- AppError_Stream_AgentNotFound
    AppError_Stream <|-- AppError_Stream_StreamingFailed
    AppError_Stream <|-- AppError_Stream_StreamDisconnected
    AppError_Stream <|-- AppError_Stream_ParseError

    class ApiException {
        +statusCode: HttpStatusCode
        +errorMessage: String
    }

    class ErrorMapper {
        <<object>>
        +map(Throwable) AppError
    }

    ErrorMapper ..> AppError

    class Result~T~ {
        <<sealed>>
        +isSuccess: Boolean
        +isError: Boolean
        +getOrNull() T?
        +errorOrNull() AppError?
        +map(transform) Result~R~
        +flatMap(transform) Result~R~
        +onSuccess(action) Result~T~
        +onError(action) Result~T~
    }
    class Result_Success~T~ {
        +data: T
    }
    class Result_Error {
        +error: AppError
    }

    Result <|-- Result_Success
    Result <|-- Result_Error
    Result_Error --> AppError

    class AuthEvent {
        <<sealed>>
    }
    class AuthEvent_SessionExpired
    class AuthEvent_LoggedOut

    AuthEvent <|-- AuthEvent_SessionExpired
    AuthEvent <|-- AuthEvent_LoggedOut

    class AuthEventBus {
        <<object>>
        +events: SharedFlow~AuthEvent~
        +emit(AuthEvent)
    }

    AuthEventBus --> AuthEvent

    class TokenManager {
        +accessToken: String?
        +refreshToken: String?
        +tokenExpiry: Long
        +isLoggedIn: Boolean
        +isTokenExpired: Boolean
        +clear()
    }

    class ThemeMode {
        <<enum>>
        SYSTEM
        LIGHT
        DARK
    }

    class ThemePreferences {
        +themeMode: Flow~ThemeMode~
        +setThemeMode(ThemeMode)
    }

    ThemePreferences --> ThemeMode

    class Language {
        <<enum>>
        SYSTEM
        EN
        FR
        ES
        +code: String
        +displayName: String
    }

    class LanguagePreferences {
        +language: Flow~Language~
        +setLanguage(Language)
    }

    LanguagePreferences --> Language

    class LocaleHelper {
        <<object>>
        +applyLocale(Context, Language) Context
        +getLocaleFromLanguage(Language) Locale
    }

    LocaleHelper --> Language

    class ValidationConstants {
        <<object>>
        +MAX_NAME_LENGTH: Int
        +MIN_PASSWORD_LENGTH: Int
    }

    class ValidationUtils {
        <<object>>
        +isValidEmail(String) Boolean
        +isValidPassword(String) Boolean
        +isValidName(String) Boolean
    }

    ValidationUtils --> ValidationConstants

    class DateTimeUtil {
        <<object>>
        +formatRelative(String) String
        +formatDate(Instant) String
        +formatTime(String) String
        +parseIsoToMillis(String) Long
    }

    class ApiConfig {
        <<object>>
        +BASE_URL: String
        +API_VERSION: String
        +CONNECT_TIMEOUT: Long
        +READ_TIMEOUT: Long
        +WRITE_TIMEOUT: Long
        +SSE_TIMEOUT: Long
        +fullBaseUrl: String
    }

    class AndroidCookieJar {
        +saveFromResponse(HttpUrl, List~Cookie~)
        +loadForRequest(HttpUrl) List~Cookie~
    }

    class AuthInterceptor {
        +intercept(Chain) Response
    }

    AuthInterceptor --> TokenManager
    AuthInterceptor --> AndroidCookieJar
    AuthInterceptor --> ApiConfig
    AuthInterceptor --> AuthEventBus

    class HttpClientFactory {
        <<object>>
        +create(TokenManager) HttpClient
    }

    HttpClientFactory --> AuthInterceptor
    HttpClientFactory --> AndroidCookieJar

    class User {
        +id: String
        +name: String
        +email: String
        +role: String
        +verifiedEmail: Boolean
        +profilePictureUrl: String?
    }

    class Agent {
        +type: String
        +name: String
        +description: String
        +greeting: String
        +version: String
        +capabilities: List~String~
        +icon: String
    }

    class Session {
        +id: String
        +userId: String
        +agentType: String
        +title: String?
        +createdAt: String
        +updatedAt: String
        +messageCount: Int
        +metadata: Map~String, Any~?
    }

    class Message {
        +role: String
        +content: String
        +timestamp: String
        +metadata: Map~String, Any~?
        +toolCalls: List~ToolCallHistory~?
        +toolId: String?
        +toolName: String?
    }

    class ToolCallHistory {
        +id: String
        +name: String
        +arguments: String
    }

    Message --> ToolCallHistory

    class SessionDetail {
        +session: Session
        +history: List~Message~
        +interactions: List~Interaction~
    }

    SessionDetail --> Session
    SessionDetail --> Message
    SessionDetail --> Interaction

    class ToolStatus {
        <<enum>>
        PENDING
        RUNNING
        SUCCESS
        ERROR
    }

    class ToolCall {
        +id: String
        +name: String
        +arguments: Map~String, Any~
        +status: ToolStatus
        +result: String?
        +error: String?
        +durationMs: Long?
    }

    ToolCall --> ToolStatus

    class InteractionStatus {
        <<enum>>
        PENDING
        THINKING
        STREAMING
        COMPLETED
        ERROR
    }

    class FeedbackState {
        <<enum>>
        NONE
        POSITIVE
        NEGATIVE
    }

    class Interaction {
        +id: String
        +userMessage: String
        +assistantResponse: String
        +assistantReasoning: String?
        +status: InteractionStatus
        +feedbackState: FeedbackState
        +toolCalls: List~ToolCall~
        +timestamp: String
    }

    Interaction --> InteractionStatus
    Interaction --> FeedbackState
    Interaction --> ToolCall

    class StreamEvent {
        <<sealed>>
    }
    class StreamEvent_Delta {
        +content: String
        +accumulated: String?
    }
    class StreamEvent_Reasoning {
        +reasoning: String
        +accumulated: String?
    }
    class StreamEvent_ToolCallEvent {
        +toolCalls: List~ToolCallData~
    }
    class StreamEvent_ToolResponseEvent {
        +responses: List~ToolResponseData~
    }
    class StreamEvent_MessageComplete {
        +id: String
        +content: String
        +usage: Usage?
    }
    class StreamEvent_Error {
        +message: String
        +code: String?
    }
    class StreamEvent_ThinkingStart
    class StreamEvent_ThinkingEnd
    class StreamEvent_MessageStart
    class StreamEvent_MessageEnd
    class StreamEvent_Preprocessing
    class StreamEvent_Postprocessing
    class StreamEvent_Done
    class StreamEvent_Close
    class StreamEvent_Heartbeat
    class StreamEvent_Unknown {
        +type: String
    }

    StreamEvent <|-- StreamEvent_Delta
    StreamEvent <|-- StreamEvent_Reasoning
    StreamEvent <|-- StreamEvent_ToolCallEvent
    StreamEvent <|-- StreamEvent_ToolResponseEvent
    StreamEvent <|-- StreamEvent_MessageComplete
    StreamEvent <|-- StreamEvent_Error
    StreamEvent <|-- StreamEvent_ThinkingStart
    StreamEvent <|-- StreamEvent_ThinkingEnd
    StreamEvent <|-- StreamEvent_MessageStart
    StreamEvent <|-- StreamEvent_MessageEnd
    StreamEvent <|-- StreamEvent_Preprocessing
    StreamEvent <|-- StreamEvent_Postprocessing
    StreamEvent <|-- StreamEvent_Done
    StreamEvent <|-- StreamEvent_Close
    StreamEvent <|-- StreamEvent_Heartbeat
    StreamEvent <|-- StreamEvent_Unknown

    class ToolCallData {
        +id: String
        +type: String
        +functionName: String
        +arguments: String
    }

    class ToolResponseData {
        +toolId: String
        +content: String
        +error: String?
    }

    class Usage {
        +inputTokens: Int
        +outputTokens: Int
    }

    StreamEvent_ToolCallEvent --> ToolCallData
    StreamEvent_ToolResponseEvent --> ToolResponseData
    StreamEvent_MessageComplete --> Usage

    class AuthRepository {
        <<interface>>
        +login(String, String) Result~User~
        +register(String, String, String) Result~User~
        +logout() Result~Unit~
        +refreshTokens() Result~User~
        +getCurrentUser() Result~User~
        +deleteAccount() Result~Unit~
        +uploadProfilePicture(String, String, String, ByteArray) Result~String~
    }

    class AgentRepository {
        <<interface>>
        +getAgents() Result~List~Agent~~
        +getAgentInfo(String) Result~Agent~
    }

    class SessionRepository {
        <<interface>>
        +getSessions(String) Result~List~Session~~
        +createSession(String, String?) Result~Session~
        +getSession(String, String) Result~SessionDetail~
        +deleteSession(String, String) Result~Unit~
        +sendMessage(String, String, String) Result~String~
        +streamResponse(String, String) Flow~StreamEvent~
    }

    class LoginUseCase {
        +invoke(String, String) Result~User~
    }
    class RegisterUseCase {
        +invoke(String, String, String) Result~User~
    }
    class LogoutUseCase {
        +invoke() Result~Unit~
    }
    class GetCurrentUserUseCase {
        +invoke() Result~User~
    }
    class DeleteAccountUseCase {
        +invoke() Result~Unit~
    }
    class UploadProfilePictureUseCase {
        +invoke(String, String, String, ByteArray) Result~String~
    }

    LoginUseCase --> AuthRepository
    RegisterUseCase --> AuthRepository
    LogoutUseCase --> AuthRepository
    LogoutUseCase --> TokenManager
    GetCurrentUserUseCase --> AuthRepository
    DeleteAccountUseCase --> AuthRepository
    UploadProfilePictureUseCase --> AuthRepository

    class GetAgentsUseCase {
        +invoke() Result~List~Agent~~
    }

    GetAgentsUseCase --> AgentRepository

    class GetSessionsUseCase {
        +invoke(String) Result~List~Session~~
    }
    class CreateSessionUseCase {
        +invoke(String, String?) Result~Session~
    }
    class GetSessionHistoryUseCase {
        +invoke(String, String) Result~SessionDetail~
    }
    class DeleteSessionUseCase {
        +invoke(String, String) Result~Unit~
    }
    class SendMessageUseCase {
        +invoke(String, String, String) Result~String~
    }
    class StreamResponseUseCase {
        +invoke(String, String) Flow~StreamEvent~
    }

    GetSessionsUseCase --> SessionRepository
    CreateSessionUseCase --> SessionRepository
    GetSessionHistoryUseCase --> SessionRepository
    DeleteSessionUseCase --> SessionRepository
    SendMessageUseCase --> SessionRepository
    StreamResponseUseCase --> SessionRepository

    class LoginRequest {
        +email: String
        +password: String
    }
    class RegisterRequest {
        +name: String
        +email: String
        +password: String
    }
    class CreateSessionRequest {
        +title: String?
        +metadata: Map~String, String~?
    }
    class SendMessageRequest {
        +message: String
    }

    class BaseResponse {
        +code: Int
        +status: String
        +message: String
    }
    class AuthResponse {
        +code: Int
        +status: String
        +message: String
        +user: UserDto
        +tokens: TokensDto
    }
    class UserResponse {
        +code: Int
        +status: String
        +message: String
        +user: UserDto
    }
    class ProfilePictureResponse {
        +code: Int
        +status: String
        +message: String
        +profilePictureUrl: String
    }
    class AgentsListResponse {
        +code: Int
        +status: String
        +message: String
        +agents: List~AgentDto~
    }
    class AgentResponse {
        +code: Int
        +status: String
        +message: String
        +agent: AgentDto
    }
    class SessionsListResponse {
        +code: Int
        +status: String
        +message: String
        +sessions: List~SessionDto~
    }
    class SessionResponse {
        +code: Int
        +status: String
        +message: String
        +session: SessionDto
    }
    class SessionDetailResponse {
        +code: Int
        +status: String
        +message: String
        +session: SessionDetailDto
    }
    class StreamResponse {
        +code: Int
        +status: String
        +message: String
        +stream: StreamInfoDto
    }

    class UserDto {
        +id: String
        +name: String
        +email: String
        +role: String
        +verifiedEmail: Boolean
        +profilePictureUrl: String?
    }
    class TokensDto {
        +access: AccessTokenDto
    }
    class AccessTokenDto {
        +token: String
        +expires: String
    }
    class AgentDto {
        +type: String
        +name: String
        +description: String
        +greeting: String?
        +version: String
        +capabilities: List~String~
        +icon: String
    }
    class SessionDto {
        +sessionId: String
        +userId: String
        +agentType: String
        +title: String?
        +createdAt: String
        +updatedAt: String
        +messageCount: Int
        +metadata: JsonObject?
    }
    class SessionDetailDto {
        +sessionId: String
        +userId: String
        +agentType: String
        +title: String?
        +createdAt: String
        +updatedAt: String
        +messageCount: Int
        +metadata: JsonObject?
        +history: List~MessageDto~
        +summary: String?
    }
    class MessageDto {
        +role: String
        +content: String
        +timestamp: String
        +metadata: JsonObject?
        +toolCalls: List~ToolCallHistoryDto~?
        +toolId: String?
        +toolName: String?
    }
    class ToolCallHistoryDto {
        +id: String
        +name: String
        +arguments: String
    }
    class StreamInfoDto {
        +streamId: String
        +sessionId: String
        +message: String
    }

    AuthResponse --> UserDto
    AuthResponse --> TokensDto
    TokensDto --> AccessTokenDto
    UserResponse --> UserDto
    AgentsListResponse --> AgentDto
    AgentResponse --> AgentDto
    SessionsListResponse --> SessionDto
    SessionResponse --> SessionDto
    SessionDetailResponse --> SessionDetailDto
    SessionDetailDto --> MessageDto
    MessageDto --> ToolCallHistoryDto
    StreamResponse --> StreamInfoDto

    class AuthApiService {
        +login(LoginRequest) AuthResponse
        +register(RegisterRequest) AuthResponse
        +logout() BaseResponse
        +refreshTokens() AuthResponse
        +getCurrentUser() UserResponse
        +deleteAccount() BaseResponse
        +uploadProfilePicture(String, String, String, ByteArray) ProfilePictureResponse
    }

    AuthApiService --> LoginRequest
    AuthApiService --> RegisterRequest
    AuthApiService --> AuthResponse
    AuthApiService --> BaseResponse
    AuthApiService --> UserResponse
    AuthApiService --> ProfilePictureResponse

    class AgentApiService {
        +getAgents() AgentsListResponse
        +getAgentInfo(String) AgentResponse
        +getSessions(String) SessionsListResponse
        +createSession(String, CreateSessionRequest?) SessionResponse
        +getSession(String, String) SessionDetailResponse
        +deleteSession(String, String) BaseResponse
        +sendMessage(String, String, SendMessageRequest) StreamResponse
    }

    AgentApiService --> AgentsListResponse
    AgentApiService --> AgentResponse
    AgentApiService --> SessionsListResponse
    AgentApiService --> CreateSessionRequest
    AgentApiService --> SessionResponse
    AgentApiService --> SessionDetailResponse
    AgentApiService --> SendMessageRequest
    AgentApiService --> StreamResponse

    class SSEClient {
        +connect(String, String) Flow~StreamEvent~
        +close()
    }

    SSEClient --> TokenManager
    SSEClient --> StreamEvent

    class UserMapper {
        <<object>>
        +toDomain(UserDto) User
    }
    class AgentMapper {
        <<object>>
        +toDomain(AgentDto) Agent
    }
    class SessionMapper {
        <<object>>
        +toDomain(SessionDto) Session
        +toDomain(SessionDetailDto) SessionDetail
        +toDomain(MessageDto) Message
        +toDomain(ToolCallHistoryDto) ToolCallHistory
    }
    class SSEEventMapper {
        <<object>>
        +map(String, String) StreamEvent
    }

    UserMapper --> UserDto
    UserMapper --> User
    AgentMapper --> AgentDto
    AgentMapper --> Agent
    SessionMapper --> SessionDto
    SessionMapper --> Session
    SessionMapper --> SessionDetailDto
    SessionMapper --> SessionDetail
    SessionMapper --> MessageDto
    SessionMapper --> Message
    SSEEventMapper --> StreamEvent

    class AuthRepositoryImpl {
        +login(String, String) Result~User~
        +register(String, String, String) Result~User~
        +logout() Result~Unit~
        +refreshTokens() Result~User~
        +getCurrentUser() Result~User~
        +deleteAccount() Result~Unit~
        +uploadProfilePicture(String, String, String, ByteArray) Result~String~
    }

    AuthRepository <|.. AuthRepositoryImpl
    AuthRepositoryImpl --> AuthApiService
    AuthRepositoryImpl --> TokenManager
    AuthRepositoryImpl --> UserMapper
    AuthRepositoryImpl --> ErrorMapper

    class AgentRepositoryImpl {
        +getAgents() Result~List~Agent~~
        +getAgentInfo(String) Result~Agent~
    }

    AgentRepository <|.. AgentRepositoryImpl
    AgentRepositoryImpl --> AgentApiService
    AgentRepositoryImpl --> AgentMapper
    AgentRepositoryImpl --> ErrorMapper

    class SessionRepositoryImpl {
        +getSessions(String) Result~List~Session~~
        +createSession(String, String?) Result~Session~
        +getSession(String, String) Result~SessionDetail~
        +deleteSession(String, String) Result~Unit~
        +sendMessage(String, String, String) Result~String~
        +streamResponse(String, String) Flow~StreamEvent~
    }

    SessionRepository <|.. SessionRepositoryImpl
    SessionRepositoryImpl --> AgentApiService
    SessionRepositoryImpl --> SSEClient
    SessionRepositoryImpl --> SessionMapper
    SessionRepositoryImpl --> ErrorMapper

    class AppModule {
        <<object>>
        +provideTokenManager(Context) TokenManager
        +provideThemePreferences(Context) ThemePreferences
        +provideLanguagePreferences(Context) LanguagePreferences
    }

    AppModule ..> TokenManager
    AppModule ..> ThemePreferences
    AppModule ..> LanguagePreferences

    class NetworkModule {
        <<object>>
        +provideHttpClient(TokenManager) HttpClient
    }

    NetworkModule --> HttpClientFactory
    NetworkModule --> TokenManager

    class RepositoryModule {
        <<object>>
        +provideAuthApiService(HttpClient) AuthApiService
        +provideAgentApiService(HttpClient) AgentApiService
        +provideSSEClient(TokenManager) SSEClient
        +provideAuthRepository(AuthApiService, TokenManager) AuthRepository
        +provideAgentRepository(AgentApiService) AgentRepository
        +provideSessionRepository(AgentApiService, SSEClient) SessionRepository
    }

    RepositoryModule ..> AuthApiService
    RepositoryModule ..> AgentApiService
    RepositoryModule ..> SSEClient
    RepositoryModule ..> AuthRepositoryImpl
    RepositoryModule ..> AgentRepositoryImpl
    RepositoryModule ..> SessionRepositoryImpl

    class UiState {
        <<interface>>
    }
    class UiIntent {
        <<interface>>
    }
    class UiEffect {
        <<interface>>
    }

    class MviViewModel~S_I_E~ {
        <<abstract>>
        #state: StateFlow~S~
        #effect: Flow~E~
        #currentState: S
        +dispatch(I)
        #handleIntent(I)*
        #setState(reducer)
        #sendEffect(E)
    }

    class AuthState {
        +email: String
        +password: String
        +name: String
        +confirmPassword: String
        +isLoading: Boolean
        +emailError: Int?
        +passwordError: Int?
        +nameError: Int?
        +confirmPasswordError: Int?
        +isPasswordVisible: Boolean
        +isLoginValid: Boolean
        +isRegisterValid: Boolean
    }

    UiState <|.. AuthState

    class AuthIntent {
        <<sealed interface>>
    }
    class AuthIntent_UpdateEmail {
        +email: String
    }
    class AuthIntent_UpdatePassword {
        +password: String
    }
    class AuthIntent_UpdateName {
        +name: String
    }
    class AuthIntent_UpdateConfirmPassword {
        +confirmPassword: String
    }
    class AuthIntent_TogglePasswordVisibility
    class AuthIntent_Login
    class AuthIntent_Register
    class AuthIntent_ClearErrors

    UiIntent <|.. AuthIntent
    AuthIntent <|-- AuthIntent_UpdateEmail
    AuthIntent <|-- AuthIntent_UpdatePassword
    AuthIntent <|-- AuthIntent_UpdateName
    AuthIntent <|-- AuthIntent_UpdateConfirmPassword
    AuthIntent <|-- AuthIntent_TogglePasswordVisibility
    AuthIntent <|-- AuthIntent_Login
    AuthIntent <|-- AuthIntent_Register
    AuthIntent <|-- AuthIntent_ClearErrors

    class AuthEffect {
        <<sealed interface>>
    }
    class AuthEffect_NavigateToChat
    class AuthEffect_ShowError {
        +message: String
    }

    UiEffect <|.. AuthEffect
    AuthEffect <|-- AuthEffect_NavigateToChat
    AuthEffect <|-- AuthEffect_ShowError

    class AuthViewModel {
        +handleIntent(AuthIntent)
    }

    MviViewModel <|-- AuthViewModel
    AuthViewModel --> LoginUseCase
    AuthViewModel --> RegisterUseCase
    AuthViewModel --> AuthState
    AuthViewModel --> AuthIntent
    AuthViewModel --> AuthEffect

    class ChatState {
        +user: User?
        +agents: List~Agent~
        +selectedAgent: Agent?
        +isLoadingAgents: Boolean
        +sessions: List~Session~
        +activeSessionId: String?
        +isLoadingSessions: Boolean
        +interactions: List~Interaction~
        +isLoadingHistory: Boolean
        +inputText: String
        +isSending: Boolean
        +isStreaming: Boolean
        +streamingContent: String
        +currentStreamId: String?
        +isDrawerOpen: Boolean
        +sessionSearchQuery: String
        +error: String?
        +isWelcomeScreen: Boolean
        +filteredSessions: List~Session~
        +canSend: Boolean
        +activeSession: Session?
    }

    UiState <|.. ChatState
    ChatState --> User
    ChatState --> Agent
    ChatState --> Session
    ChatState --> Interaction

    class ChatIntent {
        <<sealed interface>>
    }
    class ChatIntent_LoadUser
    class ChatIntent_LoadAgents
    class ChatIntent_SelectAgent {
        +agentType: String
    }
    class ChatIntent_LoadSessions
    class ChatIntent_SelectSession {
        +sessionId: String
    }
    class ChatIntent_NewChat
    class ChatIntent_DeleteSession {
        +sessionId: String
    }
    class ChatIntent_UpdateInput {
        +text: String
    }
    class ChatIntent_SendMessage
    class ChatIntent_CancelStreaming
    class ChatIntent_SetFeedback {
        +interactionIndex: Int
        +feedback: FeedbackState
    }
    class ChatIntent_CopyMessage {
        +content: String
    }
    class ChatIntent_ToggleDrawer
    class ChatIntent_CloseDrawer
    class ChatIntent_UpdateSearchQuery {
        +query: String
    }
    class ChatIntent_DismissError
    class ChatIntent_RetryLastAction
    class ChatIntent_Logout

    UiIntent <|.. ChatIntent
    ChatIntent <|-- ChatIntent_LoadUser
    ChatIntent <|-- ChatIntent_LoadAgents
    ChatIntent <|-- ChatIntent_SelectAgent
    ChatIntent <|-- ChatIntent_LoadSessions
    ChatIntent <|-- ChatIntent_SelectSession
    ChatIntent <|-- ChatIntent_NewChat
    ChatIntent <|-- ChatIntent_DeleteSession
    ChatIntent <|-- ChatIntent_UpdateInput
    ChatIntent <|-- ChatIntent_SendMessage
    ChatIntent <|-- ChatIntent_CancelStreaming
    ChatIntent <|-- ChatIntent_SetFeedback
    ChatIntent <|-- ChatIntent_CopyMessage
    ChatIntent <|-- ChatIntent_ToggleDrawer
    ChatIntent <|-- ChatIntent_CloseDrawer
    ChatIntent <|-- ChatIntent_UpdateSearchQuery
    ChatIntent <|-- ChatIntent_DismissError
    ChatIntent <|-- ChatIntent_RetryLastAction
    ChatIntent <|-- ChatIntent_Logout

    class ChatEffect {
        <<sealed interface>>
    }
    class ChatEffect_ScrollToBottom
    class ChatEffect_CopiedToClipboard {
        +message: String
    }
    class ChatEffect_ShowToast {
        +message: String?
        +messageResId: Int?
        +type: ToastType
    }
    class ChatEffect_NavigateToLogin

    UiEffect <|.. ChatEffect
    ChatEffect <|-- ChatEffect_ScrollToBottom
    ChatEffect <|-- ChatEffect_CopiedToClipboard
    ChatEffect <|-- ChatEffect_ShowToast
    ChatEffect <|-- ChatEffect_NavigateToLogin

    class ChatViewModel {
        +handleIntent(ChatIntent)
    }

    MviViewModel <|-- ChatViewModel
    ChatViewModel --> GetAgentsUseCase
    ChatViewModel --> GetSessionsUseCase
    ChatViewModel --> CreateSessionUseCase
    ChatViewModel --> GetSessionHistoryUseCase
    ChatViewModel --> DeleteSessionUseCase
    ChatViewModel --> SendMessageUseCase
    ChatViewModel --> StreamResponseUseCase
    ChatViewModel --> LogoutUseCase
    ChatViewModel --> GetCurrentUserUseCase
    ChatViewModel --> ChatState
    ChatViewModel --> ChatIntent
    ChatViewModel --> ChatEffect

    class UserProfileState {
        +user: User?
        +isLoading: Boolean
        +isDeleting: Boolean
        +isUploadingPicture: Boolean
        +showDeleteConfirmation: Boolean
        +error: String?
    }

    UiState <|.. UserProfileState
    UserProfileState --> User

    class UserProfileIntent {
        <<sealed interface>>
    }
    class UserProfileIntent_LoadUser
    class UserProfileIntent_Logout
    class UserProfileIntent_ShowDeleteConfirmation
    class UserProfileIntent_HideDeleteConfirmation
    class UserProfileIntent_ConfirmDeleteAccount
    class UserProfileIntent_DismissError
    class UserProfileIntent_UploadProfilePicture {
        +fileName: String
        +contentType: String
        +fileBytes: ByteArray
    }

    UiIntent <|.. UserProfileIntent
    UserProfileIntent <|-- UserProfileIntent_LoadUser
    UserProfileIntent <|-- UserProfileIntent_Logout
    UserProfileIntent <|-- UserProfileIntent_ShowDeleteConfirmation
    UserProfileIntent <|-- UserProfileIntent_HideDeleteConfirmation
    UserProfileIntent <|-- UserProfileIntent_ConfirmDeleteAccount
    UserProfileIntent <|-- UserProfileIntent_DismissError
    UserProfileIntent <|-- UserProfileIntent_UploadProfilePicture

    class UserProfileEffect {
        <<sealed interface>>
    }
    class UserProfileEffect_NavigateToLogin
    class UserProfileEffect_ShowToast {
        +message: String?
        +messageResId: Int?
    }

    UiEffect <|.. UserProfileEffect
    UserProfileEffect <|-- UserProfileEffect_NavigateToLogin
    UserProfileEffect <|-- UserProfileEffect_ShowToast

    class UserProfileViewModel {
        +handleIntent(UserProfileIntent)
    }

    MviViewModel <|-- UserProfileViewModel
    UserProfileViewModel --> GetCurrentUserUseCase
    UserProfileViewModel --> DeleteAccountUseCase
    UserProfileViewModel --> LogoutUseCase
    UserProfileViewModel --> UploadProfilePictureUseCase
    UserProfileViewModel --> UserProfileState
    UserProfileViewModel --> UserProfileIntent
    UserProfileViewModel --> UserProfileEffect

    class ToastType {
        <<enum>>
        Success
        Error
        Warning
        Info
    }

    class ToastDuration {
        <<enum>>
        Short
        Medium
        Long
        +millis: Long
    }

    class ToastAction {
        +label: String
        +onClick: () Unit
    }

    class ToastData {
        +id: String
        +message: String
        +type: ToastType
        +duration: ToastDuration
        +action: ToastAction?
    }

    ToastData --> ToastType
    ToastData --> ToastDuration
    ToastData --> ToastAction

    class ToastManager {
        <<object>>
        +toasts: StateFlow~List~ToastData~~
        +show(ToastData)
        +success(String, ToastAction?)
        +error(String, ToastAction?)
        +warning(String, ToastAction?)
        +info(String, ToastAction?)
        +dismiss(String)
        +dismissAll()
    }

    ToastManager --> ToastData

    class Routes {
        <<sealed>>
    }
    class Routes_Login
    class Routes_Register
    class Routes_Chat
    class Routes_ChatWithAgent {
        +createRoute(String) String
    }
    class Routes_Profile

    Routes <|-- Routes_Login
    Routes <|-- Routes_Register
    Routes <|-- Routes_Chat
    Routes <|-- Routes_ChatWithAgent
    Routes <|-- Routes_Profile

    class OraApplication {
        <<HiltAndroidApp>>
    }

    class MainActivity {
        +themePreferences: ThemePreferences
        +languagePreferences: LanguagePreferences
        +tokenManager: TokenManager
        +attachBaseContext(Context)
        +onCreate(Bundle?)
    }

    MainActivity --> ThemePreferences
    MainActivity --> LanguagePreferences
    MainActivity --> TokenManager
    MainActivity --> LocaleHelper

```
