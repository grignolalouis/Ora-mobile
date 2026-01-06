package com.ora.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ora.app.core.session.AuthEvent
import com.ora.app.core.session.AuthEventBus
import com.ora.app.core.storage.TokenManager
import com.ora.app.presentation.features.auth.LoginScreen
import com.ora.app.presentation.features.auth.RegisterScreen
import com.ora.app.presentation.features.chat.ChatScreen
import com.ora.app.presentation.features.profile.UserProfileScreen
import org.koin.compose.koinInject

@Composable
fun NavGraph(
    navController: NavHostController,
    tokenManager: TokenManager = koinInject()
) {
    // LG: Écouter les événements de session expirée
    LaunchedEffect(Unit) {
        AuthEventBus.events.collect { event ->
            when (event) {
                is AuthEvent.SessionExpired, is AuthEvent.LoggedOut -> {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    val isLoggedIn = tokenManager.accessToken != null

    val startDestination = if (isLoggedIn) Routes.Chat.route else Routes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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

        composable(Routes.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.Chat.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Chat.route) {
            ChatScreen(
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

        composable(Routes.Profile.route) {
            UserProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.Chat_WithAgent.route,
            arguments = listOf(navArgument("agentType") { type = NavType.StringType })
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
    }
}
