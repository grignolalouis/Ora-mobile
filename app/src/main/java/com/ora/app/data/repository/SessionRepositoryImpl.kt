package com.ora.app.data.repository

import com.ora.app.core.error.ErrorMapper
import com.ora.app.core.util.Result
import com.ora.app.data.mapper.toDomain
import com.ora.app.data.remote.api.AgentApiService
import com.ora.app.data.remote.dto.request.CreateSessionRequest
import com.ora.app.data.remote.dto.request.RenameSessionRequest
import com.ora.app.data.remote.dto.request.SendMessageRequest
import com.ora.app.data.remote.sse.SSEClient
import com.ora.app.domain.model.Session
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(
    private val api: AgentApiService,
    private val sseClient: SSEClient
) : SessionRepository {

    override suspend fun getSessions(agentType: String): Result<List<Session>> = try {
        val response = api.getSessions(agentType)
        Result.success(response.sessions.map { it.toDomain() })
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun createSession(agentType: String, title: String?): Result<Session> = try {
        val request = title?.let { CreateSessionRequest(title = it) }
        val response = api.createSession(agentType, request)
        Result.success(response.session.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun getSession(agentType: String, sessionId: String): Result<SessionDetail> = try {
        val response = api.getSession(agentType, sessionId)
        Result.success(response.session.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun deleteSession(agentType: String, sessionId: String): Result<Unit> = try {
        api.deleteSession(agentType, sessionId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun renameSession(
        agentType: String,
        sessionId: String,
        title: String
    ): Result<Session> = try {
        val response = api.renameSession(agentType, sessionId, RenameSessionRequest(title))
        Result.success(response.session.toDomain())
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override suspend fun sendMessage(
        agentType: String,
        sessionId: String,
        message: String
    ): Result<String> = try {
        val response = api.sendMessage(agentType, sessionId, SendMessageRequest(message))
        Result.success(response.stream.streamId)
    } catch (e: Exception) {
        Result.error(ErrorMapper.map(e))
    }

    override fun streamResponse(agentType: String, streamId: String): Flow<StreamEvent> {
        return sseClient.connect(agentType, streamId)
            .catch { e ->
                emit(StreamEvent.Error(e.message ?: "Stream error", "stream_error"))
            }
    }
}
