package com.ora.app.presentation.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Chat : Routes("chat")
    data object Chat_WithAgent : Routes("chat/{agentType}") {
        fun createRoute(agentType: String) = "chat/$agentType"
    }
    data object Profile : Routes("profile")
}
