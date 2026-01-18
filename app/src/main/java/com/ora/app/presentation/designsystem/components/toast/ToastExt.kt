package com.ora.app.presentation.designsystem.components.toast

import com.ora.app.core.error.AppError
import com.ora.app.core.error.toUserMessage

fun AppError.showAsToast(retryAction: (() -> Unit)? = null) {
    val action = if (isRetryable() && retryAction != null) {
        ToastAction("Retry", retryAction)
    } else {
        null
    }
    ToastManager.error(toUserMessage(), action)
}

fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.Network.NoConnection,
    is AppError.Network.Timeout,
    is AppError.Api.ServerError,
    is AppError.Api.RateLimited,
    is AppError.Stream.StreamDisconnected -> true
    else -> false
}

object Toast {
    fun success(message: String) = ToastManager.success(message)
    fun error(message: String) = ToastManager.error(message)
    fun warning(message: String) = ToastManager.warning(message)
    fun info(message: String) = ToastManager.info(message)

    fun error(error: AppError, retryAction: (() -> Unit)? = null) {
        error.showAsToast(retryAction)
    }
}
