package com.ora.app.data.mapper

import com.ora.app.data.remote.dto.response.AgentDto
import com.ora.app.domain.model.Agent

fun AgentDto.toDomain(): Agent = Agent(
    type = type,
    name = name,
    description = description,
    greeting = greeting ?: "Hello! How can I help you?",
    version = version,
    capabilities = capabilities,
    icon = icon
)
