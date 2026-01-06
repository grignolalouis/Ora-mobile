package com.ora.app.domain.model

data class Agent(
    val type: String,
    val name: String,
    val description: String,
    val version: String,
    val capabilities: List<String>,
    val icon: String
)
