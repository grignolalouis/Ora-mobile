package com.ora.app.core.di

import com.ora.app.core.network.HttpClientFactory
import com.ora.app.core.storage.TokenManager
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClientFactory.create(get<TokenManager>())
    }
}
