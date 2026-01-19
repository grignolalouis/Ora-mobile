# Feature: Profil Utilisateur

## Vue d'ensemble

La feature Profile permet à l'utilisateur de gérer son compte : voir ses informations, changer son avatar, modifier les préférences (thème, langue), et supprimer son compte.

## Écran

### UserProfileScreen

```
┌─────────────────────────────────────────┐
│ ←  Mon Profil                           │
├─────────────────────────────────────────┤
│                                         │
│              ┌───────┐                  │
│              │  📷   │                  │
│              │ Avatar│                  │
│              └───────┘                  │
│                                         │
│            John Doe                     │
│         john@example.com                │
│                                         │
├─────────────────────────────────────────┤
│  PRÉFÉRENCES                            │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎨 Thème           Système ▼   │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🌐 Langue          Français ▼  │   │
│  └─────────────────────────────────┘   │
│                                         │
├─────────────────────────────────────────┤
│  COMPTE                                 │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🚪 Déconnexion                  │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🗑️ Supprimer mon compte        │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

### Confirmation Dialog

```
┌─────────────────────────────────────────┐
│                                         │
│    Supprimer votre compte ?             │
│                                         │
│    Cette action est irréversible.       │
│    Toutes vos données seront            │
│    supprimées définitivement.           │
│                                         │
│    ┌───────────┐  ┌───────────────┐    │
│    │  Annuler  │  │   Supprimer   │    │
│    └───────────┘  └───────────────┘    │
│                                         │
└─────────────────────────────────────────┘
```

## Architecture MVI

### State

```kotlin
data class UserProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isUploadingPicture: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val error: String? = null
) : UiState
```

### Intents

```kotlin
sealed interface UserProfileIntent : UiIntent {
    data object LoadUser : UserProfileIntent
    data object Logout : UserProfileIntent
    data object ShowDeleteConfirmation : UserProfileIntent
    data object HideDeleteConfirmation : UserProfileIntent
    data object ConfirmDeleteAccount : UserProfileIntent
    data object DismissError : UserProfileIntent
    data class UploadProfilePicture(
        val fileName: String,
        val contentType: String,
        val fileBytes: ByteArray
    ) : UserProfileIntent
}
```

### Effects

```kotlin
sealed interface UserProfileEffect : UiEffect {
    data object NavigateToLogin : UserProfileEffect
    data class ShowToast(
        val message: String? = null,
        @StringRes val messageResId: Int? = null
    ) : UserProfileEffect
}
```

## Fonctionnalités

### 1. Affichage du profil

```kotlin
private suspend fun loadUser() {
    setState { copy(isLoading = true) }

    getCurrentUserUseCase().onSuccess { user ->
        setState { copy(user = user, isLoading = false) }
    }.onError { error ->
        setState { copy(error = error.toUserMessage(), isLoading = false) }
    }
}
```

### 2. Upload de photo de profil

```kotlin
private suspend fun uploadProfilePicture(
    fileName: String,
    contentType: String,
    fileBytes: ByteArray
) {
    val userId = currentState.user?.id ?: return
    setState { copy(isUploadingPicture = true) }

    uploadProfilePictureUseCase(userId, fileName, contentType, fileBytes)
        .onSuccess { newUrl ->
            setState {
                copy(
                    user = user?.copy(profilePictureUrl = newUrl),
                    isUploadingPicture = false
                )
            }
            sendEffect(UserProfileEffect.ShowToast(messageResId = R.string.profile_picture_updated))
        }
        .onError { error ->
            setState { copy(isUploadingPicture = false) }
            sendEffect(UserProfileEffect.ShowToast(message = error.toUserMessage()))
        }
}
```

### 3. Changement de thème

```kotlin
// Dans UserProfileScreen
var currentTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }

LaunchedEffect(Unit) {
    themePreferences.themeMode.collect { mode ->
        currentTheme = mode
    }
}

SettingsItem(
    icon = Icons.Default.Palette,
    title = stringResource(R.string.theme),
    value = currentTheme.displayName,
    onClick = {
        scope.launch {
            val nextTheme = when (currentTheme) {
                ThemeMode.SYSTEM -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.SYSTEM
            }
            themePreferences.setThemeMode(nextTheme)
        }
    }
)
```

### 4. Changement de langue

```kotlin
var currentLanguage by remember { mutableStateOf(Language.SYSTEM) }

LaunchedEffect(Unit) {
    languagePreferences.language.collect { lang ->
        currentLanguage = lang
    }
}

SettingsItem(
    icon = Icons.Default.Language,
    title = stringResource(R.string.language),
    value = currentLanguage.displayName,
    onClick = {
        scope.launch {
            val nextLanguage = when (currentLanguage) {
                Language.SYSTEM -> Language.EN
                Language.EN -> Language.FR
                Language.FR -> Language.ES
                Language.ES -> Language.SYSTEM
            }
            languagePreferences.setLanguage(nextLanguage)
            onLanguageChanged() // Recreate activity
        }
    }
)
```

### 5. Déconnexion

```kotlin
private suspend fun logout() {
    logoutUseCase().onSuccess {
        sendEffect(UserProfileEffect.NavigateToLogin)
    }.onError { error ->
        // Even on error, clear local data and logout
        sendEffect(UserProfileEffect.NavigateToLogin)
    }
}
```

### 6. Suppression de compte

```kotlin
private suspend fun deleteAccount() {
    setState { copy(isDeleting = true, showDeleteConfirmation = false) }

    deleteAccountUseCase().onSuccess {
        sendEffect(UserProfileEffect.ShowToast(messageResId = R.string.account_deleted))
        sendEffect(UserProfileEffect.NavigateToLogin)
    }.onError { error ->
        setState { copy(isDeleting = false, error = error.toUserMessage()) }
    }
}
```

## Composants UI

### SettingsSection

```kotlin
@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
```

### SettingsItem

```kotlin
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                       else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}
```

## API Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/users/me` | GET | Infos utilisateur |
| `/users/{id}/profile-picture` | POST | Upload avatar |
| `/auth/logout` | POST | Déconnexion |
| `/users/me` | DELETE | Supprimer compte |

## Fichiers

```
presentation/features/profile/
├── UserProfileViewModel.kt
├── UserProfileState.kt
├── UserProfileIntent.kt
├── UserProfileEffect.kt
└── UserProfileScreen.kt

domain/usecase/auth/
├── GetCurrentUserUseCase.kt
├── LogoutUseCase.kt
├── DeleteAccountUseCase.kt
└── UploadProfilePictureUseCase.kt
```

## Préférences stockées

| Préférence | Storage | Valeurs |
|------------|---------|---------|
| Theme | DataStore | SYSTEM, LIGHT, DARK |
| Language | DataStore | SYSTEM, EN, FR, ES |
| Tokens | EncryptedSharedPrefs | JWT tokens |
