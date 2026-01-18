package com.ora.app.presentation.features.chat.components.input

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraColors
import com.ora.app.presentation.designsystem.theme.OraTheme

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    isSending: Boolean,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val isActive = isStreaming || isSending
    val canSend = value.isNotBlank() && !isSending
    var isFocused by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    // Pills state
    val pills = remember { mutableStateListOf<InputPill>() }

    // Attachments state
    val attachments = remember { mutableStateListOf<Attachment>() }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachments.add(
                Attachment(
                    uri = it,
                    type = AttachmentType.FILE,
                    name = "File"
                )
            )
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachments.add(
                Attachment(
                    uri = it,
                    type = AttachmentType.IMAGE
                )
            )
        }
    }

    // Handle send with reset
    val handleSend: () -> Unit = {
        onSend()
        pills.clear()
        attachments.clear()
    }

    val inputShape = RoundedCornerShape(24.dp)

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.outline
        } else {
            OraTheme.colors.borderSubtle
        },
        animationSpec = tween(150),
        label = "borderColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacingMd)
            .padding(bottom = Dimensions.spacing12)
    ) {
        // Main input container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = inputShape,
                    spotColor = Color.Black.copy(alpha = 0.25f),
                    ambientColor = Color.Black.copy(alpha = 0.15f)
                )
                .clip(inputShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = inputShape
                )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.spacing14)
            ) {
                // Pills row (if any active)
                PillsRow(
                    pills = pills,
                    onRemovePill = { id -> pills.removeAll { it.id == id } }
                )

                // Attachments preview (if any)
                AttachmentsPreviewRow(
                    attachments = attachments,
                    onRemove = { id -> attachments.removeAll { it.id == id } }
                )

                // Text input
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 24.dp, max = 160.dp)
                        .padding(horizontal = Dimensions.spacing4)
                        .onFocusChanged { isFocused = it.isFocused },
                    enabled = !isSending,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    cursorBrush = SolidColor(OraTheme.colors.accent),
                    maxLines = 8,
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.isEmpty()) {
                                Text(
                                    text = "How can I help you today?",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Bottom action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.spacing8),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacing4),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Plus button - opens options menu
                        Box {
                            InputIconButton(
                                icon = Icons.Outlined.Add,
                                contentDescription = "Options",
                                onClick = { showOptionsMenu = true }
                            )

                            // Options Menu
                            InputOptionsMenu(
                                expanded = showOptionsMenu,
                                onDismiss = { showOptionsMenu = false },
                                onAddPill = { pillType ->
                                    // Don't add duplicate pill types
                                    if (pills.none { it.type == pillType }) {
                                        pills.add(InputPill(type = pillType))
                                    }
                                }
                            )
                        }

                        // File attachment button (paperclip)
                        InputIconButton(
                            icon = Icons.Outlined.AttachFile,
                            contentDescription = "Attach file",
                            onClick = { filePickerLauncher.launch("*/*") }
                        )

                        // Image picker button
                        InputIconButton(
                            icon = Icons.Outlined.Image,
                            contentDescription = "Add image",
                            onClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }

                    // Right side: Send/Stop button
                    AnimatedContent(
                        targetState = isActive,
                        label = "actionButton"
                    ) { streaming ->
                        if (streaming) {
                            StopButton(onClick = onCancel)
                        } else {
                            SendButton(
                                enabled = canSend,
                                onClick = { if (canSend) handleSend() }
                            )
                        }
                    }
                }
            }
        }

        // Disclaimer text
        Text(
            text = "Ora can make mistakes. Consider checking important info.",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp
            ),
            color = OraTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.spacing8)
        )
    }
}

@Composable
private fun InputIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "iconScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isPressed) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(100),
        label = "iconColor"
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
    }
}

@Composable
private fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && enabled -> 0.92f
            enabled -> 1f
            else -> 0.95f
        },
        animationSpec = tween(100),
        label = "sendScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) {
            OraTheme.colors.accent
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(150),
        label = "sendBg"
    )

    // Icon color: black on light accent (dark mode), white on dark accent (light mode)
    val iconColor by animateColorAsState(
        targetValue = if (enabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(150),
        label = "sendIconColor"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowUpward,
            contentDescription = "Send",
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "stopScale"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(OraColors.Error)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Stop,
            contentDescription = "Stop",
            modifier = Modifier.size(18.dp),
            tint = OraColors.White
        )
    }
}
