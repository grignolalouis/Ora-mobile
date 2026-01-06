package com.ora.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// ORA Design System - Minimalist Color Palette
// Inspired by Apple/Perplexity - Clean, monochrome with subtle accents
// ============================================================================

object OraColors {
    // -------------------------------------------------------------------------
    // Neutral Scale (Primary palette)
    // -------------------------------------------------------------------------
    val White = Color(0xFFFFFFFF)
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF5F5F5)
    val Gray150 = Color(0xFFEEEEEE)
    val Gray200 = Color(0xFFE5E5E5)
    val Gray300 = Color(0xFFD4D4D4)
    val Gray400 = Color(0xFFA3A3A3)
    val Gray500 = Color(0xFF737373)
    val Gray600 = Color(0xFF525252)
    val Gray700 = Color(0xFF404040)
    val Gray800 = Color(0xFF262626)
    val Gray850 = Color(0xFF1A1A1A)
    val Gray900 = Color(0xFF171717)
    val Gray950 = Color(0xFF0A0A0A)
    val Black = Color(0xFF000000)

    // -------------------------------------------------------------------------
    // Accent (Subtle blue - used sparingly)
    // -------------------------------------------------------------------------
    val Accent = Color(0xFF0A84FF)          // iOS blue
    val AccentLight = Color(0xFF5AC8FA)
    val AccentSubtle = Color(0xFF007AFF).copy(alpha = 0.1f)

    // -------------------------------------------------------------------------
    // Semantic
    // -------------------------------------------------------------------------
    val Success = Color(0xFF34C759)
    val Error = Color(0xFFFF3B30)
    val Warning = Color(0xFFFF9500)

    // -------------------------------------------------------------------------
    // Glass Effects
    // -------------------------------------------------------------------------
    val GlassLight = Color(0xFFFFFFFF).copy(alpha = 0.7f)
    val GlassDark = Color(0xFF1C1C1E).copy(alpha = 0.8f)
    val GlassBorderLight = Color(0xFFFFFFFF).copy(alpha = 0.2f)
    val GlassBorderDark = Color(0xFFFFFFFF).copy(alpha = 0.1f)

    // -------------------------------------------------------------------------
    // Chat Specific
    // -------------------------------------------------------------------------
    val UserBubbleLight = Gray900
    val UserBubbleDark = Gray100
    val UserBubbleTextLight = White
    val UserBubbleTextDark = Gray900

    val AssistantBubbleLight = Color.Transparent
    val AssistantBubbleDark = Color.Transparent

    val CodeBlockLight = Gray100
    val CodeBlockDark = Gray850
}

// ============================================================================
// Light Theme Colors
// ============================================================================
val LightBackground = OraColors.White
val LightSurface = OraColors.White
val LightSurfaceVariant = OraColors.Gray50
val LightSurfaceContainer = OraColors.Gray100
val LightOnBackground = OraColors.Gray900
val LightOnSurface = OraColors.Gray900
val LightOnSurfaceVariant = OraColors.Gray500
val LightOutline = OraColors.Gray200
val LightOutlineVariant = OraColors.Gray150

// ============================================================================
// Dark Theme Colors
// ============================================================================
val DarkBackground = OraColors.Black
val DarkSurface = OraColors.Gray950
val DarkSurfaceVariant = OraColors.Gray900
val DarkSurfaceContainer = OraColors.Gray850
val DarkOnBackground = OraColors.Gray100
val DarkOnSurface = OraColors.Gray100
val DarkOnSurfaceVariant = OraColors.Gray400
val DarkOutline = OraColors.Gray800
val DarkOutlineVariant = OraColors.Gray850
