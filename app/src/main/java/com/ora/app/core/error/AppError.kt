package com.ora.app.core.error

sealed class AppError : Exception() {

    // LG: Network
    sealed class Network : AppError() {
        data object NoConnection : Network() {
            override val message: String = "No internet connection"
        }

        data object Timeout : Network() {
            override val message: String = "Request timed out"
        }

        data class Unknown(override val message: String) : Network()
    }

    // LG: Auth
    sealed class Auth : AppError() {
        data object InvalidCredentials : Auth() {
            override val message: String = "Invalid email or password"
        }

        data object TokenExpired : Auth() {
            override val message: String = "Session expired, please login again"
        }

        data object Unauthorized : Auth() {
            override val message: String = "You are not authorized"
        }

        data object EmailNotVerified : Auth() {
            override val message: String = "Please verify your email"
        }

        data class Unknown(override val message: String) : Auth()
    }

    // LG: Validation
    sealed class Validation : AppError() {
        data class InvalidEmail(override val message: String = "Invalid email format") : Validation()
        data class InvalidPassword(override val message: String = "Password must be at least 8 characters") : Validation()
        data class FieldRequired(val field: String) : Validation() {
            override val message: String = "$field is required"
        }
        data class FieldTooLong(val field: String, val maxLength: Int) : Validation() {
            override val message: String = "$field must be at most $maxLength characters"
        }
    }

    // LG: API
    sealed class Api : AppError() {
        data class BadRequest(override val message: String) : Api()
        data class NotFound(override val message: String) : Api()
        data class Conflict(override val message: String) : Api()
        data class ServerError(override val message: String = "Server error, please try again") : Api()
        data class RateLimited(override val message: String = "Too many requests, please wait") : Api()
        data class Unknown(val code: Int, override val message: String) : Api()
    }

    // LG: Stream/SSE
    sealed class Stream : AppError() {
        data class SessionNotFound(val sessionId: String) : Stream() {
            override val message: String = "Session $sessionId not found"
        }
        data class AgentNotFound(val agentType: String) : Stream() {
            override val message: String = "Agent $agentType not found"
        }
        data class StreamingFailed(override val message: String) : Stream()
        data object StreamDisconnected : Stream() {
            override val message: String = "Stream disconnected"
        }
        data class ParseError(override val message: String) : Stream()
    }

    data class Unknown(override val message: String) : AppError()
}

fun AppError.toUserMessage(): String = message ?: "An unexpected error occurred"

fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.Network.NoConnection,
    is AppError.Network.Timeout,
    is AppError.Api.ServerError,
    is AppError.Api.RateLimited,
    is AppError.Stream.StreamDisconnected -> true
    else -> false
}

fun AppError.requiresReAuth(): Boolean = when (this) {
    is AppError.Auth.TokenExpired,
    is AppError.Auth.Unauthorized -> true
    else -> false
}
