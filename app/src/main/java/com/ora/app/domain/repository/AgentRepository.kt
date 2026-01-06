package com.ora.app.domain.repository

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Agent

interface AgentRepository {
    suspend fun getAgents(): Result<List<Agent>>
    suspend fun getAgentInfo(agentType: String): Result<Agent>
}
