package com.ora.app.core.locale

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.ora.app.core.storage.Language
import java.util.Locale

object LocaleHelper {

    fun applyLocale(context: Context, language: Language): Context {
        val locale = when (language) {
            Language.SYSTEM -> getSystemLocale()
            Language.EN -> Locale.ENGLISH
            Language.FR -> Locale.FRENCH
            Language.ES -> Locale("es")
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault()
        }
    }

    fun getLocaleFromLanguage(language: Language): Locale {
        return when (language) {
            Language.SYSTEM -> getSystemLocale()
            Language.EN -> Locale.ENGLISH
            Language.FR -> Locale.FRENCH
            Language.ES -> Locale("es")
        }
    }
}
