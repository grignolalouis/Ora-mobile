package com.ora.app.core.di

import com.ora.app.core.network.HttpClientFactory
import com.ora.app.core.storage.TokenManager
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        val tokenManager: TokenManager = get()
        HttpClientFactory.create { tokenManager.accessToken }
    }

    single<HttpClient>(named("sse")) {
        val tokenManager: TokenManager = get()
        HttpClientFactory.createForSSE { tokenManager.accessToken }
    }
}
