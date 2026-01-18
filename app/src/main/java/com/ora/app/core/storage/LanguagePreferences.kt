package com.ora.app.core.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore(name = "language_settings")

enum class Language(val code: String, val displayName: String) {
    SYSTEM("system", "System"),
    EN("en", "English"),
    FR("fr", "Français"),
    ES("es", "Español")
}

class LanguagePreferences(private val context: Context) {

    private val languageKey = stringPreferencesKey("language")

    val language: Flow<Language> = context.languageDataStore.data.map { preferences ->
        val value = preferences[languageKey] ?: Language.SYSTEM.code
        Language.entries.find { it.code == value } ?: Language.SYSTEM
    }

    suspend fun setLanguage(language: Language) {
        context.languageDataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }
}
