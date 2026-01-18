package com.ora.app

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
import com.ora.app.core.storage.ThemeMode
import com.ora.app.core.storage.ThemePreferences
import com.ora.app.presentation.components.toast.ToastHost
import com.ora.app.presentation.navigation.NavGraph
import com.ora.app.presentation.theme.OraTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val themePreferences: ThemePreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            OraTheme(themeMode = themeMode) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                    ToastHost()
                }
            }
        }
    }
}
