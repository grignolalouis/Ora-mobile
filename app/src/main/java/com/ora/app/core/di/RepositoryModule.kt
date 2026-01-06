package com.ora.app.core.di

import com.ora.app.data.remote.api.AgentApiService
import com.ora.app.data.remote.api.AuthApiService
import com.ora.app.data.remote.sse.SSEClient
import com.ora.app.data.repository.AgentRepositoryImpl
import com.ora.app.data.repository.AuthRepositoryImpl
import com.ora.app.data.repository.SessionRepositoryImpl
import com.ora.app.domain.repository.AgentRepository
import com.ora.app.domain.repository.AuthRepository
import com.ora.app.domain.repository.SessionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthApiService(get()) }
    single { AgentApiService(get()) }
    single { SSEClient(get()) } // LG: Injects TokenManager

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AgentRepository> { AgentRepositoryImpl(get()) }
    single<SessionRepository> { SessionRepositoryImpl(get(), get()) }
}
