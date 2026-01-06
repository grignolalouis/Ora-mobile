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

// ============================================================================
// ORA Design System - Theme
// Minimalist, clean theme inspired by Apple/Perplexity
// ============================================================================

private val LightColorScheme = lightColorScheme(
    primary = OraColors.Gray900,
    onPrimary = OraColors.White,
    primaryContainer = OraColors.Gray100,
    onPrimaryContainer = OraColors.Gray900,
    secondary = OraColors.Gray700,
    onSecondary = OraColors.White,
    secondaryContainer = OraColors.Gray100,
    onSecondaryContainer = OraColors.Gray900,
    tertiary = OraColors.Accent,
    onTertiary = OraColors.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = OraColors.White,
    surfaceContainerLow = OraColors.Gray50,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = OraColors.Gray150,
    surfaceContainerHighest = OraColors.Gray200,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = OraColors.Error,
    onError = OraColors.White,
    errorContainer = OraColors.Error.copy(alpha = 0.1f),
    onErrorContainer = OraColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = OraColors.Gray100,
    onPrimary = OraColors.Gray900,
    primaryContainer = OraColors.Gray800,
    onPrimaryContainer = OraColors.Gray100,
    secondary = OraColors.Gray300,
    onSecondary = OraColors.Gray900,
    secondaryContainer = OraColors.Gray800,
    onSecondaryContainer = OraColors.Gray100,
    tertiary = OraColors.Accent,
    onTertiary = OraColors.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = OraColors.Black,
    surfaceContainerLow = OraColors.Gray950,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = OraColors.Gray800,
    surfaceContainerHighest = OraColors.Gray700,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = OraColors.Error,
    onError = OraColors.White,
    errorContainer = OraColors.Error.copy(alpha = 0.2f),
    onErrorContainer = OraColors.Error
)

// ============================================================================
// Extended Colors (accessible via LocalOraColors)
// ============================================================================

data class OraExtendedColors(
    val accent: Color,
    val accentSubtle: Color,
    val success: Color,
    val warning: Color,
    val userBubble: Color,
    val userBubbleText: Color,
    val codeBlock: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val divider: Color
)

val LocalOraColors = staticCompositionLocalOf {
    OraExtendedColors(
        accent = OraColors.Accent,
        accentSubtle = OraColors.AccentSubtle,
        success = OraColors.Success,
        warning = OraColors.Warning,
        userBubble = OraColors.UserBubbleLight,
        userBubbleText = OraColors.UserBubbleTextLight,
        codeBlock = OraColors.CodeBlockLight,
        glassSurface = OraColors.GlassLight,
        glassBorder = OraColors.GlassBorderLight,
        divider = OraColors.Gray200
    )
}

private val LightExtendedColors = OraExtendedColors(
    accent = OraColors.Accent,
    accentSubtle = OraColors.AccentSubtle,
    success = OraColors.Success,
    warning = OraColors.Warning,
    userBubble = OraColors.UserBubbleLight,
    userBubbleText = OraColors.UserBubbleTextLight,
    codeBlock = OraColors.CodeBlockLight,
    glassSurface = OraColors.GlassLight,
    glassBorder = OraColors.GlassBorderLight,
    divider = OraColors.Gray200
)

private val DarkExtendedColors = OraExtendedColors(
    accent = OraColors.Accent,
    accentSubtle = OraColors.Accent.copy(alpha = 0.15f),
    success = OraColors.Success,
    warning = OraColors.Warning,
    userBubble = OraColors.UserBubbleDark,
    userBubbleText = OraColors.UserBubbleTextDark,
    codeBlock = OraColors.CodeBlockDark,
    glassSurface = OraColors.GlassDark,
    glassBorder = OraColors.GlassBorderDark,
    divider = OraColors.Gray800
)

// ============================================================================
// Theme Composable
// ============================================================================

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

// ============================================================================
// Extension for easy access
// ============================================================================

object OraTheme {
    val colors: OraExtendedColors
        @Composable
        get() = LocalOraColors.current
}
