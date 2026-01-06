package com.ora.app.domain.usecase.agent

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Agent
import com.ora.app.domain.repository.AgentRepository

class GetAgentInfoUseCase(private val agentRepository: AgentRepository) {

    suspend operator fun invoke(agentType: String): Result<Agent> {
        return agentRepository.getAgentInfo(agentType)
    }
}
