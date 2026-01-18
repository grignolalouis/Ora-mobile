package com.ora.app.presentation.theme

import androidx.compose.ui.graphics.Color

object OraColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    val LightBackground = Color(0xFFFAF9F7)          // Warm off-white
    val LightSurface = Color(0xFFFFFFFF)              // Pure white for cards
    val LightSurfaceVariant = Color(0xFFF5F4F0)       // Slightly warm gray
    val LightSurfaceContainer = Color(0xFFEDECE8)     // Warm gray
    val LightBorder = Color(0x1A000000)               // 10% black
    val LightBorderSubtle = Color(0x0D000000)         // 5% black

    // Light mode text
    val LightTextPrimary = Color(0xFF1A1A18)          // Near black, warm
    val LightTextSecondary = Color(0xFF6B6A68)        // Muted gray
    val LightTextTertiary = Color(0xFF9A9893)         // Light muted

    val DarkBackground = Color(0xFF1A1918)            // Warm dark
    val DarkSurface = Color(0xFF242321)               // Slightly lighter
    val DarkSurfaceVariant = Color(0xFF2E2D2A)        // Card surface
    val DarkSurfaceContainer = Color(0xFF393836)      // Container
    val DarkBorder = Color(0x1AFFFFFF)                // 10% white
    val DarkBorderSubtle = Color(0x0DFFFFFF)          // 5% white

    // Dark mode text
    val DarkTextPrimary = Color(0xFFF5F5F3)           // Off-white
    val DarkTextSecondary = Color(0xFFA8A7A3)         // Muted
    val DarkTextTertiary = Color(0xFF6B6A68)          // Very muted

    val Accent = Color(0xFF1A1A18)                    // Near black
    val AccentDark = Color(0xFFF5F5F3)                // Off-white for dark mode
    val AccentHover = Color(0xFF3A3A38)               // Slightly lighter black
    val AccentLight = Color(0xFFF0F0EE)               // Light gray
    val AccentMuted = Color(0x1A1A1A18)               // 10% black

    val Success = Color(0xFF16A34A)                   // Green-600
    val SuccessLight = Color(0xFFDCFCE7)              // Green-100
    val Error = Color(0xFFDC2626)                     // Red-600
    val ErrorLight = Color(0xFFFEE2E2)                // Red-100
    val Warning = Color(0xFFCA8A04)                   // Yellow-600
    val Info = Color(0xFF3B82F6)                      // Blue-500

    // User message bubble - subtle warm tone
    val UserBubbleLight = Color(0xFFE8E6E1)           // Warm light gray
    val UserBubbleDark = Color(0xFF3A3836)            // Warm dark gray
    val UserBubbleText = Color(0xFF1A1A18)            // Dark text

    // Assistant - no bubble, just text
    val AssistantText = Color(0xFF1A1A18)
    val AssistantTextDark = Color(0xFFF5F5F3)
}

// Light Theme
val LightBackground = OraColors.LightBackground
val LightSurface = OraColors.LightSurface
val LightSurfaceVariant = OraColors.LightSurfaceVariant
val LightSurfaceContainer = OraColors.LightSurfaceContainer
val LightOnBackground = OraColors.LightTextPrimary
val LightOnSurface = OraColors.LightTextPrimary
val LightOnSurfaceVariant = OraColors.LightTextSecondary
val LightOutline = OraColors.LightBorder
val LightOutlineVariant = OraColors.LightBorderSubtle

// Dark Theme
val DarkBackground = OraColors.DarkBackground
val DarkSurface = OraColors.DarkSurface
val DarkSurfaceVariant = OraColors.DarkSurfaceVariant
val DarkSurfaceContainer = OraColors.DarkSurfaceContainer
val DarkOnBackground = OraColors.DarkTextPrimary
val DarkOnSurface = OraColors.DarkTextPrimary
val DarkOnSurfaceVariant = OraColors.DarkTextSecondary
val DarkOutline = OraColors.DarkBorder
val DarkOutlineVariant = OraColors.DarkBorderSubtle
