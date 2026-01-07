package com.ora.app.presentation.features.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Menu
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ora.app.R
import com.ora.app.domain.model.Agent
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraTheme

@Composable
fun ChatTopBar(
    title: String,
    agents: List<Agent>,
    selectedAgent: Agent?,
    onAgentSelected: (String) -> Unit,
    onMenuClick: () -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAgentMenu by remember { mutableStateOf(false) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (showAgentMenu) 180f else 0f,
        animationSpec = tween(200),
        label = "chevronRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimensions.spacing12)
    ) {
        // Left side: Menu button
        TopBarIconButton(
            icon = Icons.Outlined.Menu,
            contentDescription = "Menu",
            onClick = onMenuClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Center: Agent selector
        Box(modifier = Modifier.align(Alignment.Center)) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        enabled = agents.size > 1,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showAgentMenu = true }
                    .background(
                        if (showAgentMenu) {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MaterialTheme.colorScheme.background
                        }
                    )
                    .padding(
                        horizontal = Dimensions.spacing14,
                        vertical = Dimensions.spacing10
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedAgent?.name ?: "Ora",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (agents.size > 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Agent dropdown menu (custom popup)
            if (showAgentMenu) {
                Popup(
                    alignment = Alignment.TopCenter,
                    onDismissRequest = { showAgentMenu = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    AnimatedVisibility(
                        visible = showAgentMenu,
                        enter = fadeIn(tween(150)) + scaleIn(
                            animationSpec = tween(150),
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        ),
                        exit = fadeOut(tween(100)) + scaleOut(
                            animationSpec = tween(100),
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        )
                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(top = 8.dp)
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
                                    .width(280.dp)
                                    .padding(Dimensions.spacing8)
                            ) {
                                agents.forEach { agent ->
                                    AgentMenuItem(
                                        agent = agent,
                                        isSelected = agent.type == selectedAgent?.type,
                                        onClick = {
                                            showAgentMenu = false
                                            onAgentSelected(agent.type)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Right side: New chat button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNewChat
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "New chat",
                modifier = Modifier.size(26.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AgentMenuItem(
    agent: Agent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(backgroundColor)
            .padding(
                horizontal = Dimensions.spacing14,
                vertical = Dimensions.spacing14
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = agent.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (agent.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = agent.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.width(Dimensions.spacing12))
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(18.dp),
                tint = OraTheme.colors.accent
            )
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "iconScale"
    )

    Box(
        modifier = modifier
            .size(42.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isAccent) {
                    OraTheme.colors.accent
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                }
            )
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
            tint = if (isAccent) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
