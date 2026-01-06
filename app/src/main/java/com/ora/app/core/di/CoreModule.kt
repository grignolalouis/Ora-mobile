package com.ora.app.core.di

import com.ora.app.core.storage.ThemePreferences
import com.ora.app.core.storage.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { TokenManager(androidContext()) }
    single { ThemePreferences(androidContext()) }
}
