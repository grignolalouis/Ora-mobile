# Feature: Authentification

## Vue d'ensemble

La feature Auth gère l'inscription, la connexion et la gestion des tokens JWT.

## Écrans

### LoginScreen

```
┌─────────────────────────────────────────┐
│                                         │
│              ORA LOGO                   │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Email                       │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Password              👁    │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │         Se connecter        │     │
│    └─────────────────────────────┘     │
│                                         │
│         Pas de compte ? S'inscrire     │
│                                         │
└─────────────────────────────────────────┘
```

### RegisterScreen

```
┌─────────────────────────────────────────┐
│                                         │
│              INSCRIPTION                │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Nom                         │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Email                       │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Mot de passe          👁    │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │ Confirmer mot de passe      │     │
│    └─────────────────────────────┘     │
│                                         │
│    ┌─────────────────────────────┐     │
│    │         S'inscrire          │     │
│    └─────────────────────────────┘     │
│                                         │
│         Déjà un compte ? Se connecter  │
│                                         │
└─────────────────────────────────────────┘
```

## Architecture MVI

### State

```kotlin
data class AuthState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    val isPasswordVisible: Boolean = false
) : UiState {
    val isLoginValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                emailError == null && passwordError == null

    val isRegisterValid: Boolean
        get() = isLoginValid && name.isNotBlank() &&
                password == confirmPassword && nameError == null
}
```

### Intents

```kotlin
sealed interface AuthIntent : UiIntent {
    data class UpdateEmail(val email: String) : AuthIntent
    data class UpdatePassword(val password: String) : AuthIntent
    data class UpdateName(val name: String) : AuthIntent
    data class UpdateConfirmPassword(val confirmPassword: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object Login : AuthIntent
    data object Register : AuthIntent
    data object ClearErrors : AuthIntent
}
```

### Effects

```kotlin
sealed interface AuthEffect : UiEffect {
    data object NavigateToChat : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}
```

## Validation

| Champ | Règle | Erreur |
|-------|-------|--------|
| Email | Format valide | "Email invalide" |
| Email | Non vide | "Email requis" |
| Password | Min 8 caractères | "8 caractères minimum" |
| Password | Non vide | "Mot de passe requis" |
| Name | Non vide | "Nom requis" |
| Name | Max 50 caractères | "Nom trop long" |
| Confirm | = Password | "Mots de passe différents" |

## Flux de connexion

```
┌─────────────────────────────────────────────────────────────────┐
│                         LOGIN FLOW                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│    User                                                         │
│      │                                                          │
│      │ Enter email & password                                   │
│      ▼                                                          │
│    ┌─────────────┐                                             │
│    │ LoginScreen │                                             │
│    └──────┬──────┘                                             │
│           │ dispatch(Login)                                     │
│           ▼                                                     │
│    ┌─────────────┐                                             │
│    │AuthViewModel│                                             │
│    └──────┬──────┘                                             │
│           │ loginUseCase(email, password)                       │
│           ▼                                                     │
│    ┌─────────────┐                                             │
│    │ LoginUseCase│ ── Validate email format                    │
│    └──────┬──────┘                                             │
│           │ authRepository.login()                              │
│           ▼                                                     │
│    ┌──────────────────┐                                        │
│    │AuthRepositoryImpl│                                        │
│    └──────┬───────────┘                                        │
│           │ api.login() + saveTokens()                         │
│           ▼                                                     │
│    ┌─────────────┐    ┌─────────────┐                         │
│    │AuthApiService│──▶│ TokenManager│                         │
│    └─────────────┘    └─────────────┘                         │
│           │                   │                                 │
│           │ Result.Success    │ Save encrypted tokens          │
│           ▼                   │                                 │
│    ┌─────────────┐            │                                 │
│    │AuthViewModel│◀───────────┘                                │
│    └──────┬──────┘                                             │
│           │ sendEffect(NavigateToChat)                          │
│           ▼                                                     │
│    ┌─────────────┐                                             │
│    │  ChatScreen │                                             │
│    └─────────────┘                                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Gestion des tokens

### Stockage sécurisé

```kotlin
class TokenManager(context: Context) {
    // EncryptedSharedPreferences avec AES256-GCM
    private val prefs = EncryptedSharedPreferences.create(...)

    var accessToken: String?
    var refreshToken: String?
    var tokenExpiry: Long

    val isLoggedIn: Boolean
    val isTokenExpired: Boolean
}
```

### Refresh automatique

```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401) {
            if (tryRefreshToken(chain)) {
                // Retry with new token
                return chain.proceed(newRequest)
            } else {
                // Emit session expired event
                AuthEventBus.emit(AuthEvent.SessionExpired)
            }
        }
        return response
    }
}
```

## API Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/auth/login` | POST | Connexion |
| `/auth/register` | POST | Inscription |
| `/auth/logout` | POST | Déconnexion |
| `/auth/refresh` | POST | Refresh token |

## Fichiers

```
presentation/features/auth/
├── AuthViewModel.kt
├── AuthState.kt
├── AuthIntent.kt
├── AuthEffect.kt
├── LoginScreen.kt
└── RegisterScreen.kt

domain/usecase/auth/
├── LoginUseCase.kt
├── RegisterUseCase.kt
├── LogoutUseCase.kt
└── GetCurrentUserUseCase.kt
```
