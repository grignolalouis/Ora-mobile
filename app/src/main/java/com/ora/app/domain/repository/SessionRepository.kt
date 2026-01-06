package com.ora.app.domain.repository

import com.ora.app.core.util.Result
import com.ora.app.domain.model.Session
import com.ora.app.domain.model.SessionDetail
import com.ora.app.domain.model.StreamEvent
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun getSessions(agentType: String): Result<List<Session>>
    suspend fun createSession(agentType: String, title: String? = null): Result<Session>
    suspend fun getSession(agentType: String, sessionId: String): Result<SessionDetail>
    suspend fun deleteSession(agentType: String, sessionId: String): Result<Unit>
    suspend fun renameSession(agentType: String, sessionId: String, title: String): Result<Session>
    suspend fun sendMessage(agentType: String, sessionId: String, message: String): Result<String>
    fun streamResponse(streamId: String): Flow<StreamEvent>
}
