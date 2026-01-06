package com.ora.app.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Event bus for authentication-related events (session expiration, logout)
 */
object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: AuthEvent) {
        _events.emit(event)
    }
}

sealed class AuthEvent {
    data object SessionExpired : AuthEvent()
    data object LoggedOut : AuthEvent()
}
