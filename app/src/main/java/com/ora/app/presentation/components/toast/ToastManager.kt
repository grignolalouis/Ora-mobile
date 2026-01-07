package com.ora.app.presentation.components.toast

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Global toast manager for managing toast notifications across the app.
 * Singleton object that maintains a queue of toasts.
 */
object ToastManager {

    private const val MAX_TOASTS = 3

    private val _toasts = MutableStateFlow<List<ToastData>>(emptyList())
    val toasts: StateFlow<List<ToastData>> = _toasts.asStateFlow()

    /**
     * Show a toast notification.
     * If more than MAX_TOASTS, removes the oldest ones to make room.
     */
    fun show(toast: ToastData) {
        _toasts.update { current ->
            // Add new toast and keep only the last MAX_TOASTS
            (current + toast).takeLast(MAX_TOASTS)
        }
    }

    /**
     * Show a success toast
     */
    fun success(message: String, action: ToastAction? = null) {
        show(ToastData.success(message, action))
    }

    /**
     * Show an error toast
     */
    fun error(message: String, action: ToastAction? = null) {
        show(ToastData.error(message, action))
    }

    /**
     * Show a warning toast
     */
    fun warning(message: String, action: ToastAction? = null) {
        show(ToastData.warning(message, action))
    }

    /**
     * Show an info toast
     */
    fun info(message: String, action: ToastAction? = null) {
        show(ToastData.info(message, action))
    }

    /**
     * Dismiss a specific toast by ID
     */
    fun dismiss(id: String) {
        _toasts.update { current ->
            current.filterNot { it.id == id }
        }
    }

    /**
     * Dismiss all toasts
     */
    fun dismissAll() {
        _toasts.value = emptyList()
    }
}
