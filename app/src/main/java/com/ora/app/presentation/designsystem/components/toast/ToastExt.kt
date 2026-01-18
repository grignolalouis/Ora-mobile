package com.ora.app.presentation.designsystem.components.toast

import com.ora.app.core.error.AppError
import com.ora.app.core.error.isRetryable
import com.ora.app.core.error.toUserMessage

fun AppError.showAsToast(retryAction: (() -> Unit)? = null) {
    val action = if (isRetryable() && retryAction != null) {
        ToastAction("Retry", retryAction)
    } else {
        null
    }
    ToastManager.error(toUserMessage(), action)
}
