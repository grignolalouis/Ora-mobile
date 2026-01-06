package com.ora.app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    val code: Int,
    val status: String,
    val message: String
)
