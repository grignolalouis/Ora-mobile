package com.ora.app.core.di

import com.ora.app.core.storage.TokenManager
import com.ora.app.data.remote.api.AgentApiService
import com.ora.app.data.remote.api.AuthApiService
import com.ora.app.data.remote.sse.SSEClient
import com.ora.app.data.repository.AgentRepositoryImpl
import com.ora.app.data.repository.AuthRepositoryImpl
import com.ora.app.data.repository.SessionRepositoryImpl
import com.ora.app.domain.repository.AgentRepository
import com.ora.app.domain.repository.AuthRepository
import com.ora.app.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthApiService(client: HttpClient): AuthApiService {
        return AuthApiService(client)
    }

    @Provides
    @Singleton
    fun provideAgentApiService(client: HttpClient): AgentApiService {
        return AgentApiService(client)
    }

    @Provides
    @Singleton
    fun provideSSEClient(tokenManager: TokenManager): SSEClient {
        return SSEClient(tokenManager)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideAgentRepository(api: AgentApiService): AgentRepository {
        return AgentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        api: AgentApiService,
        sseClient: SSEClient
    ): SessionRepository {
        return SessionRepositoryImpl(api, sseClient)
    }
}
