# Clean Architecture

## Principe

La Clean Architecture organise le code en couches concentriques avec une règle de dépendance stricte : **les couches internes ne connaissent pas les couches externes**.

```
┌──────────────────────────────────────────────────────────────────┐
│                          PRESENTATION                            │
│    ┌────────────────────────────────────────────────────────┐   │
│    │                        DOMAIN                          │   │
│    │    ┌──────────────────────────────────────────────┐   │   │
│    │    │                    DATA                      │   │   │
│    │    │    ┌────────────────────────────────────┐   │   │   │
│    │    │    │              CORE                  │   │   │   │
│    │    │    └────────────────────────────────────┘   │   │   │
│    │    └──────────────────────────────────────────────┘   │   │
│    └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

## Couches

### 1. Core Layer

Contient les utilitaires et configurations partagés par toutes les couches.

| Package | Responsabilité |
|---------|----------------|
| `core/di` | Configuration Hilt (modules) |
| `core/error` | Types d'erreurs (AppError), mapping |
| `core/network` | Configuration HTTP, intercepteurs |
| `core/storage` | Gestion tokens, préférences |
| `core/util` | Result<T>, utilitaires dates |
| `core/validation` | Règles de validation |

**Exemple - Result<T>:**
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()

    fun onSuccess(action: (T) -> Unit): Result<T>
    fun onError(action: (AppError) -> Unit): Result<T>
}
```

### 2. Domain Layer

Contient la logique métier pure, indépendante de toute implémentation.

| Package | Responsabilité |
|---------|----------------|
| `domain/model` | Entités métier (User, Agent, Session) |
| `domain/repository` | Interfaces des repositories |
| `domain/usecase` | Cas d'utilisation |

**Exemple - Use Case:**
```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (!ValidationUtils.isValidEmail(email)) {
            return Result.error(AppError.Validation.InvalidEmail())
        }
        return authRepository.login(email.trim(), password)
    }
}
```

**Exemple - Repository Interface:**
```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
}
```

### 3. Data Layer

Implémente les interfaces du domain et gère les sources de données.

| Package | Responsabilité |
|---------|----------------|
| `data/remote/api` | Services API (Ktor) |
| `data/remote/dto` | DTOs (Request/Response) |
| `data/remote/sse` | Client SSE |
| `data/mapper` | Conversion DTO ↔ Domain |
| `data/repository` | Implémentation repositories |

**Exemple - Repository Implementation:**
```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            saveTokens(response.tokens.access.token, response.tokens.access.expires)
            Result.success(response.user.toDomain())
        } catch (e: Exception) {
            Result.error(ErrorMapper.map(e))
        }
    }
}
```

**Exemple - Mapper:**
```kotlin
fun UserDto.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role,
    verifiedEmail = verifiedEmail,
    profilePictureUrl = profilePictureUrl
)
```

### 4. Presentation Layer

Gère l'UI et les interactions utilisateur via le pattern MVI.

| Package | Responsabilité |
|---------|----------------|
| `presentation/mvi` | Base classes MVI |
| `presentation/features` | Screens + ViewModels |
| `presentation/designsystem` | Composants UI, thème |
| `presentation/navigation` | Navigation |

## Flux de données

```
┌─────────┐    Intent    ┌───────────┐    invoke()   ┌─────────┐
│   UI    │ ──────────▶  │ ViewModel │ ───────────▶  │ UseCase │
│(Compose)│              │   (MVI)   │               │         │
└─────────┘              └───────────┘               └────┬────┘
     ▲                        │                          │
     │                        │                          ▼
     │                   State/Effect            ┌──────────────┐
     │                        │                  │  Repository  │
     │                        ▼                  │  (interface) │
     │                   ┌─────────┐             └──────┬───────┘
     └───────────────────│ collect │                    │
                         └─────────┘                    ▼
                                                ┌──────────────┐
                                                │  Repository  │
                                                │    (impl)    │
                                                └──────┬───────┘
                                                       │
                                                       ▼
                                                ┌──────────────┐
                                                │  API/Local   │
                                                │    Source    │
                                                └──────────────┘
```

## Avantages

| Avantage | Description |
|----------|-------------|
| **Testabilité** | Chaque couche peut être testée isolément |
| **Maintenabilité** | Changements localisés dans une couche |
| **Scalabilité** | Ajout de features sans impact sur l'existant |
| **Indépendance** | Domain ne dépend d'aucun framework |
