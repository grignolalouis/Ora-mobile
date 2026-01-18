package com.ora.app.presentation.designsystem.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraColors
import kotlinx.coroutines.delay

@Composable
fun OraToast(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(toast.id) {
        isVisible = true
        delay(toast.duration.millis)
        isVisible = false
        delay(300)
        onDismiss()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        ToastContent(
            toast = toast,
            onDismiss = { isVisible = false }
        )
    }
}

@Composable
private fun ToastContent(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.background == OraColors.DarkBackground

    val colors = remember(toast.type, isDarkTheme) {
        getToastColors(toast.type, isDarkTheme)
    }

    val icon = remember(toast.type) {
        getToastIcon(toast.type)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingScreen)
            .shadow(
                elevation = Dimensions.elevationMd,
                shape = RoundedCornerShape(Dimensions.radiusMd)
            ),
        shape = RoundedCornerShape(Dimensions.radiusMd),
        color = colors.background,
        border = BorderStroke(1.dp, colors.border),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimensions.spacingMd,
                    end = Dimensions.spacingSm,
                    top = Dimensions.spacingSm,
                    bottom = Dimensions.spacingSm
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.icon,
                modifier = Modifier.size(Dimensions.iconSizeMd)
            )

            Text(
                text = toast.message,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.content,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            toast.action?.let { action ->
                TextButton(
                    onClick = {
                        action.onClick()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.icon,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Dismiss",
                    tint = colors.content.copy(alpha = 0.6f),
                    modifier = Modifier.size(Dimensions.iconSizeSm)
                )
            }
        }
    }
}

private data class ToastColors(
    val background: Color,
    val content: Color,
    val icon: Color,
    val border: Color
)

private fun getToastColors(type: ToastType, isDarkTheme: Boolean): ToastColors {
    return when (type) {
        ToastType.Success -> ToastColors(
            background = if (isDarkTheme) Color(0xFF1B3D2F) else OraColors.SuccessLight,
            content = if (isDarkTheme) Color(0xFFE8F5E9) else Color(0xFF1B5E20),
            icon = OraColors.Success,
            border = if (isDarkTheme) Color(0xFF2E5A45) else Color(0xFFB8E0C8)
        )
        ToastType.Error -> ToastColors(
            background = if (isDarkTheme) Color(0xFF3D1B1B) else OraColors.ErrorLight,
            content = if (isDarkTheme) Color(0xFFFFEBEE) else Color(0xFFB71C1C),
            icon = OraColors.Error,
            border = if (isDarkTheme) Color(0xFF5A2E2E) else Color(0xFFF5C6C6)
        )
        ToastType.Warning -> ToastColors(
            background = if (isDarkTheme) Color(0xFF3D3520) else Color(0xFFFFF8E1),
            content = if (isDarkTheme) Color(0xFFFFF8E1) else Color(0xFF5D4037),
            icon = OraColors.Warning,
            border = if (isDarkTheme) Color(0xFF5A4D2E) else Color(0xFFE8DDB8)
        )
        ToastType.Info -> ToastColors(
            background = if (isDarkTheme) OraColors.DarkSurfaceVariant else OraColors.LightSurfaceContainer,
            content = if (isDarkTheme) OraColors.DarkTextPrimary else OraColors.LightTextPrimary,
            icon = if (isDarkTheme) OraColors.DarkTextSecondary else OraColors.LightTextSecondary,
            border = if (isDarkTheme) OraColors.DarkBorder else OraColors.LightBorder
        )
    }
}

private fun getToastIcon(type: ToastType): ImageVector {
    return when (type) {
        ToastType.Success -> Icons.Rounded.Check
        ToastType.Error -> Icons.Rounded.Close
        ToastType.Warning -> Icons.Rounded.Warning
        ToastType.Info -> Icons.Rounded.Info
    }
}
