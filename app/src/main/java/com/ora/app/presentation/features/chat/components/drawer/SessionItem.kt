package com.ora.app.presentation.features.chat.components.drawer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ora.app.domain.model.Session
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraColors
import com.ora.app.presentation.theme.OraTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionItem(
    session: Session,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    // Text color animation
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(150),
        label = "sessionTextColor"
    )

    val itemShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacing8, vertical = 2.dp)
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 6.dp,
                        shape = itemShape,
                        spotColor = Color.Black.copy(alpha = 0.20f),
                        ambientColor = Color.Black.copy(alpha = 0.10f)
                    )
                } else Modifier
            )
            .clip(itemShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.surface
                else Color.Transparent
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = OraTheme.colors.borderSubtle,
                        shape = itemShape
                    )
                } else Modifier
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .padding(
                horizontal = Dimensions.spacing14,
                vertical = Dimensions.spacing14
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection indicator - small accent dot
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(OraTheme.colors.accent)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacing10))
            }

            Text(
                text = session.title ?: "New conversation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 15.sp
                ),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Custom context menu
        if (showMenu) {
            SessionContextMenu(
                onDismiss = { showMenu = false },
                onDelete = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
private fun SessionContextMenu(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                OraTheme.colors.borderSubtle
            )
        ) {
            Box(
                modifier = Modifier.padding(Dimensions.spacing8)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDelete
                        )
                        .padding(
                            horizontal = Dimensions.spacing12,
                            vertical = Dimensions.spacing12
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = OraColors.Error
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacing12))
                    Text(
                        text = "Delete",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = OraColors.Error
                    )
                }
            }
        }
    }
}
