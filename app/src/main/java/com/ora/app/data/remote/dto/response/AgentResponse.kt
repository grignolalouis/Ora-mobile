package com.ora.app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AgentsListResponse(
    val code: Int,
    val status: String,
    val message: String,
    val agents: List<AgentDto>
)

@Serializable
data class AgentResponse(
    val code: Int,
    val status: String,
    val message: String,
    val agent: AgentDto
)

@Serializable
data class AgentDto(
    val type: String,
    val name: String,
    val description: String,
    val greeting: String? = null,
    val version: String,
    val capabilities: List<String>,
    val icon: String
)
