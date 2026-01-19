# Navigation

## Vue d'ensemble

Ora utilise **Navigation Compose** pour la navigation entre écrans avec un graphe de navigation déclaratif.

## Routes

```kotlin
sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Chat : Routes("chat")
    data object ChatWithAgent : Routes("chat/{agentType}") {
        fun createRoute(agentType: String) = "chat/$agentType"
    }
    data object Profile : Routes("profile")
}
```

## Graphe de navigation

```
┌─────────────────────────────────────────────────────────────────┐
│                       Navigation Graph                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│    ┌─────────┐         ┌──────────┐         ┌─────────┐       │
│    │  Login  │ ◄─────▶ │ Register │         │ Profile │       │
│    └────┬────┘         └──────────┘         └────▲────┘       │
│         │                                        │             │
│         │ onLoginSuccess                         │             │
│         │                                        │             │
│         ▼                                        │             │
│    ┌─────────────────────────────────────────────┴───────┐    │
│    │                      Chat                           │    │
│    │                                                     │    │
│    │   ┌──────────────┐      ┌──────────────────────┐  │    │
│    │   │ ChatWithAgent│ ◄──▶ │      Chat (home)     │  │    │
│    │   │ /chat/{type} │      │      /chat           │  │    │
│    │   └──────────────┘      └──────────────────────┘  │    │
│    │                                                     │    │
│    └─────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## NavGraph Implementation

```kotlin
@Composable
fun NavGraph(
    navController: NavHostController,
    tokenManager: TokenManager,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    onLanguageChanged: () -> Unit
) {
    // Determine start destination based on auth state
    val startDestination = if (tokenManager.isLoggedIn) {
        Routes.Chat.route
    } else {
        Routes.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Chat.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Routes.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.Chat.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Chat Screen (home)
        composable(Routes.Chat.route) {
            ChatScreen(
                initialAgentType = null,
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.Profile.route)
                }
            )
        }

        // Chat Screen (with agent)
        composable(
            route = Routes.ChatWithAgent.route,
            arguments = listOf(
                navArgument("agentType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val agentType = backStackEntry.arguments?.getString("agentType")
            ChatScreen(
                initialAgentType = agentType,
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.Profile.route)
                }
            )
        }

        // Profile Screen
        composable(Routes.Profile.route) {
            UserProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                themePreferences = themePreferences,
                languagePreferences = languagePreferences,
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}
```

## Patterns de navigation

### Navigation simple

```kotlin
navController.navigate(Routes.Profile.route)
```

### Navigation avec argument

```kotlin
navController.navigate(Routes.ChatWithAgent.createRoute("assistant"))
// Navigates to: chat/assistant
```

### Navigation avec clear backstack

```kotlin
// After login - clear auth screens from backstack
navController.navigate(Routes.Chat.route) {
    popUpTo(Routes.Login.route) { inclusive = true }
}
```

### Navigation logout (clear all)

```kotlin
navController.navigate(Routes.Login.route) {
    popUpTo(0) { inclusive = true }  // Clear entire backstack
}
```

### Back navigation

```kotlin
navController.popBackStack()
```

## Start Destination Logic

```kotlin
val startDestination = if (tokenManager.isLoggedIn) {
    Routes.Chat.route  // User logged in → go to chat
} else {
    Routes.Login.route // Not logged in → go to login
}
```

## Navigation depuis ViewModel (via Effect)

Le ViewModel ne navigue pas directement. Il envoie un **Effect** que le Composable intercepte.

```kotlin
// ViewModel
private fun onLoginSuccess() {
    sendEffect(AuthEffect.NavigateToChat)
}

// Composable
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is AuthEffect.NavigateToChat -> onLoginSuccess() // callback to NavGraph
            // ...
        }
    }
}
```

## Deep Links (optionnel)

```kotlin
composable(
    route = Routes.ChatWithAgent.route,
    deepLinks = listOf(
        navDeepLink { uriPattern = "ora://chat/{agentType}" }
    )
) { /* ... */ }
```

## Bonnes pratiques

| Pratique | Raison |
|----------|--------|
| Routes en sealed class | Type-safe, autocomplétion |
| Navigation via callbacks | Découplage ViewModel/Navigation |
| Clear backstack après auth | Empêche retour vers login |
| Arguments typés | NavType.StringType pour sécurité |
