# Gestion des Erreurs

## Vue d'ensemble

Ora utilise un système d'erreurs typé basé sur des **sealed classes** pour garantir une gestion exhaustive des cas d'erreur.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Exception                               │
│                    (Throwable, HttpException)                   │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        ErrorMapper                              │
│                     map(Throwable): AppError                    │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                          AppError                               │
│              (Network, Auth, Validation, Api, Stream)           │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Result<T>                               │
│                    Success(data) | Error(error)                 │
└─────────────────────────────────────────────────────────────────┘
```

## AppError (Sealed Class)

```kotlin
sealed class AppError {

    sealed class Network : AppError() {
        data object NoConnection : Network()
        data object Timeout : Network()
        data class Unknown(val message: String) : Network()
    }

    sealed class Auth : AppError() {
        data object InvalidCredentials : Auth()
        data object TokenExpired : Auth()
        data object Unauthorized : Auth()
        data object EmailNotVerified : Auth()
        data class Unknown(val message: String) : Auth()
    }

    sealed class Validation : AppError() {
        data class InvalidEmail(val message: String = "Invalid email") : Validation()
        data class InvalidPassword(val message: String = "Password too short") : Validation()
        data class FieldRequired(val field: String) : Validation()
        data class FieldTooLong(val field: String, val maxLength: Int) : Validation()
    }

    sealed class Api : AppError() {
        data class BadRequest(val message: String) : Api()
        data class NotFound(val message: String) : Api()
        data class Conflict(val message: String) : Api()
        data class ServerError(val message: String) : Api()
        data class RateLimited(val message: String) : Api()
        data class Unknown(val code: Int, val message: String) : Api()
    }

    sealed class Stream : AppError() {
        data class SessionNotFound(val sessionId: String) : Stream()
        data class AgentNotFound(val agentType: String) : Stream()
        data class StreamingFailed(val message: String) : Stream()
        data object StreamDisconnected : Stream()
        data class ParseError(val message: String) : Stream()
    }

    data class Unknown(val message: String) : AppError()
}
```

## Extensions AppError

```kotlin
// Message utilisateur (i18n)
fun AppError.toUserMessage(): String = when (this) {
    is AppError.Network.NoConnection -> "No internet connection"
    is AppError.Network.Timeout -> "Request timed out"
    is AppError.Auth.InvalidCredentials -> "Invalid email or password"
    is AppError.Auth.TokenExpired -> "Session expired"
    is AppError.Validation.FieldRequired -> "$field is required"
    // ...
}

// Peut être réessayé ?
fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.Network.NoConnection -> true
    is AppError.Network.Timeout -> true
    is AppError.Api.ServerError -> true
    is AppError.Stream.StreamDisconnected -> true
    else -> false
}

// Nécessite réauthentification ?
fun AppError.requiresReAuth(): Boolean = when (this) {
    is AppError.Auth.TokenExpired -> true
    is AppError.Auth.Unauthorized -> true
    else -> false
}
```

## ErrorMapper

Convertit les exceptions en AppError.

```kotlin
object ErrorMapper {
    fun map(throwable: Throwable): AppError = when (throwable) {
        is UnknownHostException,
        is ConnectException -> AppError.Network.NoConnection

        is SocketTimeoutException -> AppError.Network.Timeout

        is ApiException -> mapHttpError(throwable.statusCode, throwable.errorMessage)

        else -> AppError.Unknown(throwable.message ?: "Unknown error")
    }

    private fun mapHttpError(status: HttpStatusCode, message: String): AppError =
        when (status) {
            HttpStatusCode.BadRequest -> AppError.Api.BadRequest(message)
            HttpStatusCode.Unauthorized -> AppError.Auth.Unauthorized
            HttpStatusCode.Forbidden -> AppError.Auth.Unauthorized
            HttpStatusCode.NotFound -> AppError.Api.NotFound(message)
            HttpStatusCode.Conflict -> AppError.Api.Conflict(message)
            HttpStatusCode.TooManyRequests -> AppError.Api.RateLimited(message)
            in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout ->
                AppError.Api.ServerError(message)
            else -> AppError.Api.Unknown(status.value, message)
        }
}
```

## Result<T>

Encapsule succès ou erreur de manière type-safe.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): AppError? = (this as? Error)?.error

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(error: AppError): Result<Nothing> = Error(error)
    }
}
```

## Usage dans Repository

```kotlin
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            saveTokens(response.tokens)
            Result.success(response.user.toDomain())
        } catch (e: Exception) {
            Result.error(ErrorMapper.map(e))
        }
    }
}
```

## Usage dans ViewModel

```kotlin
private suspend fun login() {
    setState { copy(isLoading = true) }

    loginUseCase(currentState.email, currentState.password)
        .onSuccess { user ->
            setState { copy(isLoading = false) }
            sendEffect(AuthEffect.NavigateToChat)
        }
        .onError { error ->
            setState { copy(isLoading = false) }
            handleError(error)
        }
}

private fun handleError(error: AppError) {
    when (error) {
        is AppError.Validation.InvalidEmail ->
            setState { copy(emailError = R.string.error_invalid_email) }
        is AppError.Validation.InvalidPassword ->
            setState { copy(passwordError = R.string.error_invalid_password) }
        else ->
            sendEffect(AuthEffect.ShowError(error.toUserMessage()))
    }
}
```

## Flux d'erreur complet

```
1. API call throws exception
   api.login() → throws ApiException(401, "Invalid credentials")
                         │
                         ▼
2. Repository catches and maps
   catch (e) → ErrorMapper.map(e) → AppError.Auth.InvalidCredentials
                         │
                         ▼
3. Repository returns Result.Error
   return Result.error(AppError.Auth.InvalidCredentials)
                         │
                         ▼
4. UseCase propagates
   return authRepository.login(...)
                         │
                         ▼
5. ViewModel handles
   .onError { error → handleError(error) }
                         │
                         ▼
6. UI shows error
   setState { copy(error = error.toUserMessage()) }
   or sendEffect(ShowToast(messageResId = ...))
```

## Avantages

| Avantage | Description |
|----------|-------------|
| **Type-safe** | Pas de string pour identifier erreurs |
| **Exhaustif** | `when` oblige à gérer tous les cas |
| **Localisé** | Messages dans resources (i18n) |
| **Testable** | Facile de vérifier le type d'erreur |
| **Retry logic** | `isRetryable()` pour UI |
