package com.ora.app.presentation.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraColors

// ============================================================================
// ORA Design System - Button Components
// Clean, minimalist buttons with subtle interactions
// ============================================================================

enum class OraButtonStyle {
    Primary,    // Filled dark/light
    Secondary,  // Outlined
    Ghost       // Text only
}

@Composable
fun OraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: OraButtonStyle = OraButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = RoundedCornerShape(Dimensions.radiusMd),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Dimensions.spacingLg,
        vertical = Dimensions.spacingMd
    )
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            style == OraButtonStyle.Primary && isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            style == OraButtonStyle.Primary -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        animationSpec = tween(150),
        label = "buttonBg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            style == OraButtonStyle.Primary -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(150),
        label = "buttonText"
    )

    Box(
        modifier = modifier
            .height(Dimensions.buttonHeight)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun OraIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(Dimensions.radiusSm))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun OraTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = OraColors.Accent
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor by animateColorAsState(
        targetValue = when {
            !enabled -> color.copy(alpha = 0.38f)
            isPressed -> color.copy(alpha = 0.7f)
            else -> color
        },
        animationSpec = tween(150),
        label = "textButtonColor"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(Dimensions.spacingSm)
    )
}
