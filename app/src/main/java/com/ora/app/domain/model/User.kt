package com.ora.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val verifiedEmail: Boolean,
    val profilePictureUrl: String?
)
