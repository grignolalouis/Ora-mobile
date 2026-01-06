package com.ora.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ora.app.core.storage.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = OraBlue,
    onPrimary = UserBubbleText,
    primaryContainer = OraBlueLight,
    secondary = OraBlueLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = AssistantBubbleLight,
    onSurfaceVariant = TextSecondaryLight,
    error = Error,
    onError = UserBubbleText
)

private val DarkColorScheme = darkColorScheme(
    primary = OraBlueLight,
    onPrimary = TextPrimaryLight,
    primaryContainer = OraBlueDark,
    secondary = OraBlue,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AssistantBubbleDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Error,
    onError = UserBubbleText
)

@Composable
fun OraTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OraTypography,
        content = content
    )
}
