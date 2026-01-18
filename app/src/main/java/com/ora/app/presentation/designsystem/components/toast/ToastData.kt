package com.ora.app.presentation.designsystem.components.toast

import java.util.UUID

enum class ToastType {
    Success,
    Error,
    Warning,
    Info
}

enum class ToastDuration(val millis: Long) {
    Short(3000L),
    Medium(4500L),
    Long(6000L)
}

data class ToastAction(
    val label: String,
    val onClick: () -> Unit
)

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
