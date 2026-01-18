package com.ora.app.data.remote.api

import com.ora.app.data.remote.dto.request.CreateSessionRequest
import com.ora.app.data.remote.dto.request.SendMessageRequest
import com.ora.app.data.remote.dto.response.AgentResponse
import com.ora.app.data.remote.dto.response.AgentsListResponse
import com.ora.app.data.remote.dto.response.BaseResponse
import com.ora.app.data.remote.dto.response.SessionDetailResponse
import com.ora.app.data.remote.dto.response.SessionResponse
import com.ora.app.data.remote.dto.response.SessionsListResponse
import com.ora.app.data.remote.dto.response.StreamResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AgentApiService(private val client: HttpClient) {

    suspend fun getAgents(): AgentsListResponse =
        client.get("agents").body()

    suspend fun getAgentInfo(agentType: String): AgentResponse =
        client.get("agents/$agentType").body()

    suspend fun getSessions(agentType: String): SessionsListResponse =
        client.get("agents/$agentType/sessions").body()

    suspend fun createSession(agentType: String, request: CreateSessionRequest?): SessionResponse =
        client.post("agents/$agentType/sessions") {
            request?.let { setBody(it) }
        }.body()

    suspend fun getSession(agentType: String, sessionId: String): SessionDetailResponse =
        client.get("agents/$agentType/sessions/$sessionId").body()

    suspend fun deleteSession(agentType: String, sessionId: String): BaseResponse =
        client.delete("agents/$agentType/sessions/$sessionId").body()

    suspend fun sendMessage(
        agentType: String,
        sessionId: String,
        request: SendMessageRequest
    ): StreamResponse =
        client.post("agents/$agentType/sessions/$sessionId/messages") { setBody(request) }.body()
}
