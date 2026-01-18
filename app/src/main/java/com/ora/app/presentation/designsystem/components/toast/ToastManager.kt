package com.ora.app.presentation.designsystem.components.toast

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ToastManager {

    private const val MAX_TOASTS = 3

    private val _toasts = MutableStateFlow<List<ToastData>>(emptyList())
    val toasts: StateFlow<List<ToastData>> = _toasts.asStateFlow()

    fun show(toast: ToastData) {
        _toasts.update { current ->
            (current + toast).takeLast(MAX_TOASTS)
        }
    }

    fun success(message: String, action: ToastAction? = null) {
        show(ToastData.success(message, action))
    }

    fun error(message: String, action: ToastAction? = null) {
        show(ToastData.error(message, action))
    }

    fun warning(message: String, action: ToastAction? = null) {
        show(ToastData.warning(message, action))
    }

    fun info(message: String, action: ToastAction? = null) {
        show(ToastData.info(message, action))
    }

    fun dismiss(id: String) {
        _toasts.update { current ->
            current.filterNot { it.id == id }
        }
    }

    fun dismissAll() {
        _toasts.value = emptyList()
    }
}
