# Injection de Dépendances (Hilt)

## Vue d'ensemble

Ora utilise **Hilt** (basé sur Dagger) pour l'injection de dépendances. Hilt fournit une injection compile-time avec génération de code.

## Configuration

### Application

```kotlin
@HiltAndroidApp
class OraApplication : Application()
```

### Activity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var themePreferences: ThemePreferences
    @Inject lateinit var languagePreferences: LanguagePreferences
}
```

### ViewModel

```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getAgentsUseCase: GetAgentsUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : MviViewModel<ChatState, ChatIntent, ChatEffect>(ChatState())
```

## Modules

### AppModule

Fournit les dépendances de stockage et préférences.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(@ApplicationContext context: Context): LanguagePreferences {
        return LanguagePreferences(context)
    }
}
```

### NetworkModule

Fournit le client HTTP.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenManager: TokenManager): HttpClient {
        return HttpClientFactory.create(tokenManager)
    }
}
```

### RepositoryModule

Fournit les services API et repositories.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // API Services
    @Provides
    @Singleton
    fun provideAuthApiService(client: HttpClient): AuthApiService {
        return AuthApiService(client)
    }

    @Provides
    @Singleton
    fun provideAgentApiService(client: HttpClient): AgentApiService {
        return AgentApiService(client)
    }

    @Provides
    @Singleton
    fun provideSSEClient(tokenManager: TokenManager): SSEClient {
        return SSEClient(tokenManager)
    }

    // Repositories
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideAgentRepository(api: AgentApiService): AgentRepository {
        return AgentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        api: AgentApiService,
        sseClient: SSEClient
    ): SessionRepository {
        return SessionRepositoryImpl(api, sseClient)
    }
}
```

## Graphe de dépendances

```
┌─────────────────────────────────────────────────────────────────┐
│                      SingletonComponent                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  AppModule                                                      │
│  ├── TokenManager ◄─────────────────────────────────────────┐  │
│  ├── ThemePreferences                                       │  │
│  └── LanguagePreferences                                    │  │
│                                                             │  │
│  NetworkModule                                              │  │
│  └── HttpClient ◄──────────────────────────────────────┐   │  │
│                                                        │   │  │
│  RepositoryModule                                      │   │  │
│  ├── AuthApiService ◄──────────────────────────────────┤   │  │
│  ├── AgentApiService ◄─────────────────────────────────┤   │  │
│  ├── SSEClient ◄───────────────────────────────────────┼───┘  │
│  │                                                     │      │
│  ├── AuthRepository (impl) ◄───┬── AuthApiService      │      │
│  │                             └── TokenManager ◄──────┘      │
│  ├── AgentRepository (impl) ◄──── AgentApiService             │
│  └── SessionRepository (impl) ◄─┬─ AgentApiService            │
│                                 └─ SSEClient                  │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                      ViewModelComponent                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  AuthViewModel                                                  │
│  ├── LoginUseCase ◄──── AuthRepository                         │
│  └── RegisterUseCase ◄── AuthRepository                        │
│                                                                 │
│  ChatViewModel                                                  │
│  ├── GetAgentsUseCase ◄──── AgentRepository                    │
│  ├── GetSessionsUseCase ◄── SessionRepository                  │
│  ├── CreateSessionUseCase ◄── SessionRepository                │
│  ├── SendMessageUseCase ◄── SessionRepository                  │
│  ├── StreamResponseUseCase ◄── SessionRepository               │
│  └── ...                                                       │
│                                                                 │
│  UserProfileViewModel                                           │
│  ├── GetCurrentUserUseCase ◄── AuthRepository                  │
│  ├── DeleteAccountUseCase ◄── AuthRepository                   │
│  └── LogoutUseCase ◄──────────── AuthRepository                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Scopes

| Scope | Durée de vie | Usage |
|-------|--------------|-------|
| `@Singleton` | Application | Repositories, Services, TokenManager |
| `@ViewModelScoped` | ViewModel | (non utilisé, injection via constructeur) |
| `@ActivityScoped` | Activity | (non utilisé) |

## Use Cases

Les Use Cases sont injectés directement via `@Inject constructor` sans module.

```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // ...
    }
}
```

Hilt les crée automatiquement car toutes leurs dépendances sont disponibles.

## Bonnes pratiques

| Pratique | Raison |
|----------|--------|
| Interfaces pour repositories | Facilite les tests (mock) |
| `@Singleton` pour services | Évite recréation coûteuse |
| Constructor injection | Plus explicite, testable |
| Pas de `@Inject` sur fields | Préférer constructor injection |
