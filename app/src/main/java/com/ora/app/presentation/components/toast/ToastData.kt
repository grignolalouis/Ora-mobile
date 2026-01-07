package com.ora.app.presentation.components.toast

import java.util.UUID

/**
 * Toast severity types for visual styling
 */
enum class ToastType {
    Success,
    Error,
    Warning,
    Info
}

/**
 * Toast duration presets
 */
enum class ToastDuration(val millis: Long) {
    Short(3000L),
    Medium(4500L),
    Long(6000L)
}

/**
 * Optional action button for toast
 */
data class ToastAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Data class representing a toast notification
 */
data class ToastData(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: ToastType = ToastType.Info,
    val duration: ToastDuration = ToastDuration.Short,
    val action: ToastAction? = null
) {
    companion object {
        fun success(message: String, action: ToastAction? = null) = ToastData(
            message = message,
            type = ToastType.Success,
            action = action
        )

        fun error(message: String, action: ToastAction? = null) = ToastData(
            message = message,
            type = ToastType.Error,
            duration = ToastDuration.Medium,
            action = action
        )

        fun warning(message: String, action: ToastAction? = null) = ToastData(
            message = message,
            type = ToastType.Warning,
            action = action
        )

        fun info(message: String, action: ToastAction? = null) = ToastData(
            message = message,
            type = ToastType.Info,
            action = action
        )
    }
}
