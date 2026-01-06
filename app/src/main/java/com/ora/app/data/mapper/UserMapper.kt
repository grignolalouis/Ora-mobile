package com.ora.app.data.mapper

import com.ora.app.data.remote.dto.response.UserDto
import com.ora.app.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = role,
    verifiedEmail = verifiedEmail,
    profilePictureUrl = profilePictureUrl
)
