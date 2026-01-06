package com.ora.app.core.util

import com.ora.app.core.error.AppError

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun errorOrNull(): AppError? = when (this) {
        is Success -> null
        is Error -> error
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw error
    }

    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> defaultValue
    }

    inline fun getOrElse(defaultValue: (AppError) -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> defaultValue(error)
    }

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
    }

    inline fun mapError(transform: (AppError) -> AppError): Result<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(error))
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }

    inline fun fold(onSuccess: (T) -> Unit, onError: (AppError) -> Unit) {
        when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(error)
        }
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(error: AppError): Result<Nothing> = Error(error)
    }
}

inline fun <T> runCatching(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: AppError) {
    Result.Error(e)
} catch (e: Exception) {
    Result.Error(AppError.Unknown(e.message ?: "Unknown error"))
}

suspend inline fun <T> runSuspendCatching(crossinline block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: AppError) {
    Result.Error(e)
} catch (e: Exception) {
    Result.Error(AppError.Unknown(e.message ?: "Unknown error"))
}

fun <A, B> Result<A>.zip(other: Result<B>): Result<Pair<A, B>> = when {
    this is Result.Success && other is Result.Success -> Result.Success(this.data to other.data)
    this is Result.Error -> this
    other is Result.Error -> other
    else -> Result.Error(AppError.Unknown("Unexpected state"))
}
