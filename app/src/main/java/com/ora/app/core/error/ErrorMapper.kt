package com.ora.app.core.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {

    fun map(throwable: Throwable): AppError = when (throwable) {
        is AppError -> throwable
        is ApiException -> mapHttpError(throwable.statusCode, throwable.errorMessage)
        is UnknownHostException -> AppError.Network.NoConnection
        is SocketTimeoutException -> AppError.Network.Timeout
        is ClientRequestException -> mapHttpError(throwable.response.status, throwable.message)
        is ServerResponseException -> mapHttpError(throwable.response.status, throwable.message)
        is IOException -> AppError.Network.Unknown(throwable.message ?: "Network error")
        else -> AppError.Unknown(throwable.message ?: "Unknown error")
    }

    private fun mapHttpError(status: HttpStatusCode, message: String): AppError = when (status) {
        HttpStatusCode.BadRequest -> AppError.Api.BadRequest(message)
        HttpStatusCode.Unauthorized -> AppError.Auth.InvalidCredentials
        HttpStatusCode.Forbidden -> AppError.Auth.Unauthorized
        HttpStatusCode.NotFound -> AppError.Api.NotFound(message)
        HttpStatusCode.Conflict -> AppError.Api.Conflict(message)
        HttpStatusCode.TooManyRequests -> AppError.Api.RateLimited()
        in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout -> AppError.Api.ServerError(message)
        else -> AppError.Api.Unknown(status.value, message)
    }
}
