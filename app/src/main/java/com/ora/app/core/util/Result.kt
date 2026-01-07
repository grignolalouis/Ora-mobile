package com.ora.app.core.util

import com.ora.app.core.error.AppError

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success<*>
    val isError: Boolean get() = this is Error

    @Suppress("UNCHECKED_CAST")
    fun getOrNull(): T? {
        return when (this) {
            is Success<*> -> data as T
            is Error -> null
        }
    }

    fun errorOrNull(): AppError? {
        return when (this) {
            is Success<*> -> null
            is Error -> error
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success<*> -> Success(transform(data as T))
            is Error -> this
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> {
        return when (this) {
            is Success<*> -> transform(data as T)
            is Error -> this
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success<*>) action(data as T)
        return this
    }

    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(error: AppError): Result<Nothing> = Error(error)
    }
}
