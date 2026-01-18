package com.ora.app.core.di

import android.content.Context
import com.ora.app.core.storage.LanguagePreferences
import com.ora.app.core.storage.ThemePreferences
import com.ora.app.core.storage.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(@ApplicationContext context: Context): LanguagePreferences {
        return LanguagePreferences(context)
    }
}
