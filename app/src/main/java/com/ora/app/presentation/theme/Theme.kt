package com.ora.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ora.app.core.storage.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = OraColors.Accent,
    onPrimary = OraColors.White,
    primaryContainer = OraColors.AccentLight,
    onPrimaryContainer = OraColors.Accent,
    secondary = OraColors.LightTextSecondary,
    onSecondary = OraColors.White,
    secondaryContainer = OraColors.LightSurfaceContainer,
    onSecondaryContainer = OraColors.LightTextPrimary,
    tertiary = OraColors.Accent,
    onTertiary = OraColors.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = OraColors.White,
    surfaceContainerLow = OraColors.LightBackground,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = OraColors.LightSurfaceVariant,
    surfaceContainerHighest = OraColors.LightSurfaceContainer,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = OraColors.Error,
    onError = OraColors.White,
    errorContainer = OraColors.ErrorLight,
    onErrorContainer = OraColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = OraColors.AccentDark,
    onPrimary = OraColors.Black,
    primaryContainer = OraColors.AccentMuted,
    onPrimaryContainer = OraColors.AccentDark,
    secondary = OraColors.DarkTextSecondary,
    onSecondary = OraColors.White,
    secondaryContainer = OraColors.DarkSurfaceContainer,
    onSecondaryContainer = OraColors.DarkTextPrimary,
    tertiary = OraColors.AccentDark,
    onTertiary = OraColors.Black,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = OraColors.DarkBackground,
    surfaceContainerLow = OraColors.DarkSurface,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = OraColors.DarkSurfaceVariant,
    surfaceContainerHighest = OraColors.DarkSurfaceContainer,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = OraColors.Error,
    onError = OraColors.White,
    errorContainer = OraColors.Error.copy(alpha = 0.2f),
    onErrorContainer = OraColors.Error
)

data class OraExtendedColors(
    val accent: Color,
    val accentHover: Color,
    val success: Color,
    val warning: Color,
    val userBubble: Color,
    val userBubbleText: Color,
    val assistantText: Color,
    val textTertiary: Color,
    val borderSubtle: Color
)

val LocalOraColors = staticCompositionLocalOf {
    OraExtendedColors(
        accent = OraColors.Accent,
        accentHover = OraColors.AccentHover,
        success = OraColors.Success,
        warning = OraColors.Warning,
        userBubble = OraColors.UserBubbleLight,
        userBubbleText = OraColors.UserBubbleText,
        assistantText = OraColors.AssistantText,
        textTertiary = OraColors.LightTextTertiary,
        borderSubtle = OraColors.LightBorderSubtle
    )
}

private val LightExtendedColors = OraExtendedColors(
    accent = OraColors.Accent,
    accentHover = OraColors.AccentHover,
    success = OraColors.Success,
    warning = OraColors.Warning,
    userBubble = OraColors.UserBubbleLight,
    userBubbleText = OraColors.LightTextPrimary,
    assistantText = OraColors.AssistantText,
    textTertiary = OraColors.LightTextTertiary,
    borderSubtle = OraColors.LightBorderSubtle
)

private val DarkExtendedColors = OraExtendedColors(
    accent = OraColors.AccentDark,
    accentHover = OraColors.AccentDark,
    success = OraColors.Success,
    warning = OraColors.Warning,
    userBubble = OraColors.UserBubbleDark,
    userBubbleText = OraColors.DarkTextPrimary,
    assistantText = OraColors.AssistantTextDark,
    textTertiary = OraColors.DarkTextTertiary,
    borderSubtle = OraColors.DarkBorderSubtle
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
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalOraColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OraTypography,
            content = content
        )
    }
}

object OraTheme {
    val colors: OraExtendedColors
        @Composable
        get() = LocalOraColors.current
}
