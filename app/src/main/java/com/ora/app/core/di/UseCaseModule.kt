package com.ora.app.core.di

import com.ora.app.domain.usecase.agent.GetAgentInfoUseCase
import com.ora.app.domain.usecase.agent.GetAgentsUseCase
import com.ora.app.domain.usecase.auth.DeleteAccountUseCase
import com.ora.app.domain.usecase.auth.GetCurrentUserUseCase
import com.ora.app.domain.usecase.auth.LoginUseCase
import com.ora.app.domain.usecase.auth.LogoutUseCase
import com.ora.app.domain.usecase.auth.RefreshTokenUseCase
import com.ora.app.domain.usecase.auth.RegisterUseCase
import com.ora.app.domain.usecase.session.CreateSessionUseCase
import com.ora.app.domain.usecase.session.DeleteSessionUseCase
import com.ora.app.domain.usecase.session.GetSessionHistoryUseCase
import com.ora.app.domain.usecase.session.GetSessionsUseCase
import com.ora.app.domain.usecase.session.SendMessageUseCase
import com.ora.app.domain.usecase.session.StreamResponseUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // LG: Auth
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { RefreshTokenUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { DeleteAccountUseCase(get()) }

    // LG: Agent
    factory { GetAgentsUseCase(get()) }
    factory { GetAgentInfoUseCase(get()) }

    // LG: Session
    factory { GetSessionsUseCase(get()) }
    factory { CreateSessionUseCase(get()) }
    factory { GetSessionHistoryUseCase(get()) }
    factory { DeleteSessionUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { StreamResponseUseCase(get()) }
}
