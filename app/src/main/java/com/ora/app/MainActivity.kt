package com.ora.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ora.app.core.locale.LocaleHelper
import com.ora.app.core.storage.Language
import com.ora.app.core.storage.LanguagePreferences
import com.ora.app.core.storage.ThemeMode
import com.ora.app.core.storage.ThemePreferences
import com.ora.app.core.storage.TokenManager
import com.ora.app.presentation.designsystem.components.toast.ToastHost
import com.ora.app.presentation.navigation.NavGraph
import com.ora.app.presentation.designsystem.theme.OraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var themePreferences: ThemePreferences
    @Inject lateinit var languagePreferences: LanguagePreferences
    @Inject lateinit var tokenManager: TokenManager

    override fun attachBaseContext(newBase: Context) {
        // Apply saved language preference at Activity creation
        val prefs = LanguagePreferences(newBase)
        val language = runBlocking { prefs.language.first() }
        val localizedContext = LocaleHelper.applyLocale(newBase, language)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            OraTheme(themeMode = themeMode) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        tokenManager = tokenManager,
                        themePreferences = themePreferences,
                        languagePreferences = languagePreferences,
                        onLanguageChanged = { recreate() }
                    )
                    ToastHost()
                }
            }
        }
    }
}
