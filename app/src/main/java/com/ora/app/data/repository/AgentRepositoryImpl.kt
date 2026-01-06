package com.ora.app.data.repository

import com.ora.app.core.error.ErrorMapper
import com.ora.app.core.util.Result
import com.ora.app.data.mapper.toDomain
import com.ora.app.data.remote.api.AgentApiService
import com.ora.app.domain.model.Agent
import com.ora.app.domain.repository.AgentRepository

class AgentRepositoryImpl(private val api: AgentApiService) : AgentRepository {

    override suspend fun getAgents(): Result<List<Agent>> = try {
        val response = api.getAgents()
        Result.success(response.agents.map { it.toDomain() })
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun getAgentInfo(agentType: String): Result<Agent> = try {
        val response = api.getAgentInfo(agentType)
        Result.success(response.agent.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }
}
