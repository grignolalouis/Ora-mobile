package com.ora.app.domain.usecase.agent

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Agent
import com.ora.app.domain.repository.AgentRepository
import javax.inject.Inject

class GetAgentsUseCase @Inject constructor(private val agentRepository: AgentRepository) {

    suspend operator fun invoke(): Result<List<Agent>> {
        return agentRepository.getAgents()
    }
}
