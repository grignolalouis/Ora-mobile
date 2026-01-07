package com.ora.app.core.error

import io.ktor.http.HttpStatusCode

/**
 * Custom exception for API errors that preserves the server error message.
 */
class ApiException(
    val statusCode: HttpStatusCode,
    val errorMessage: String
) : Exception(errorMessage)
