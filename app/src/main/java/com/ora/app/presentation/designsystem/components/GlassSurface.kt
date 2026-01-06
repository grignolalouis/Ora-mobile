package com.ora.app.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraTheme

// ============================================================================
// Glass Surface Component
// Translucent surface with subtle border - Apple glass style
// ============================================================================

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Dimensions.radiusLg),
    backgroundColor: Color = OraTheme.colors.glassSurface,
    borderColor: Color = OraTheme.colors.glassBorder,
    borderWidth: Dp = 0.5.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}

// ============================================================================
// Modifier extensions for glass effect
// ============================================================================

fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(Dimensions.radiusLg),
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp = 0.5.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(
        width = borderWidth,
        color = borderColor,
        shape = shape
    )

// ============================================================================
// Divider Component
// ============================================================================

@Composable
fun OraDivider(
    modifier: Modifier = Modifier,
    color: Color = OraTheme.colors.divider,
    thickness: Dp = 0.5.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
