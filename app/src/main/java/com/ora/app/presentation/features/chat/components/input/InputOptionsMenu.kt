package com.ora.app.presentation.features.chat.components.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ora.app.R
import androidx.compose.ui.window.PopupProperties
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraTheme

// ============================================================================
// Input Options Menu
// ============================================================================

@Composable
fun InputOptionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAddPill: (PillType) -> Unit,
    modifier: Modifier = Modifier
) {
    if (expanded) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(150)) + scaleIn(
                    animationSpec = tween(150),
                    transformOrigin = TransformOrigin(0f, 1f)
                ),
                exit = fadeOut(tween(100)) + scaleOut(
                    animationSpec = tween(100),
                    transformOrigin = TransformOrigin(0f, 1f)
                )
            ) {
                Surface(
                    modifier = modifier
                        .padding(bottom = 8.dp, start = 16.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        OraTheme.colors.borderSubtle
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(Dimensions.spacing8)
                    ) {
                        MenuOptionItem(
                            icon = Icons.Outlined.Search,
                            label = stringResource(R.string.search_the_web),
                            onClick = {
                                onAddPill(PillType.WEB_SEARCH)
                                onDismiss()
                            }
                        )

                        MenuOptionItem(
                            icon = Icons.Outlined.Code,
                            label = stringResource(R.string.write_code),
                            onClick = {
                                onAddPill(PillType.CODE)
                                onDismiss()
                            }
                        )

                        MenuOptionItem(
                            icon = Icons.Outlined.AutoAwesome,
                            label = stringResource(R.string.be_creative),
                            onClick = {
                                onAddPill(PillType.CREATIVE)
                                onDismiss()
                            }
                        )

                        MenuOptionItem(
                            icon = Icons.Outlined.Analytics,
                            label = stringResource(R.string.analyze),
                            onClick = {
                                onAddPill(PillType.ANALYSIS)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuOptionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(backgroundColor)
            .padding(
                horizontal = Dimensions.spacing12,
                vertical = Dimensions.spacing12
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(Dimensions.spacing12))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
