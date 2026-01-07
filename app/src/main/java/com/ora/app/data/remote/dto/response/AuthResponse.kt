package com.ora.app.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val code: Int,
    val status: String,
    val message: String,
    val user: UserDto,
    val tokens: TokensDto
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    @SerialName("verified_email")
    val verifiedEmail: Boolean,
    @SerialName("profile_picture_url")
    val profilePictureUrl: String? = null
)

@Serializable
data class TokensDto(
    val access: AccessTokenDto
)

@Serializable
data class AccessTokenDto(
    val token: String,
    val expires: String
)

@Serializable
data class UserResponse(
    val code: Int,
    val status: String,
    val message: String,
    val user: UserDto
)

@Serializable
data class ProfilePictureResponse(
    val code: Int,
    val status: String,
    val message: String,
    @SerialName("profile_picture_url")
    val profilePictureUrl: String
)
