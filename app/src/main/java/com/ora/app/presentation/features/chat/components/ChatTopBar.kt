package com.ora.app.presentation.features.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ora.app.domain.model.Agent
import com.ora.app.presentation.theme.Dimensions

@Composable
fun ChatTopBar(
    title: String,
    agents: List<Agent>,
    selectedAgent: Agent?,
    onAgentSelected: (String) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAgentMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimensions.spacing8)
    ) {
        // Menu button
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Agent selector (centered)
        Box(modifier = Modifier.align(Alignment.Center)) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimensions.radiusMd))
                    .clickable(
                        enabled = agents.size > 1,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showAgentMenu = true }
                    .padding(
                        horizontal = Dimensions.spacing12,
                        vertical = Dimensions.spacing8
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = selectedAgent?.name ?: "Ora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (agents.size > 1) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Select agent",
                        modifier = Modifier.size(Dimensions.iconSizeSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Agent dropdown menu
            DropdownMenu(
                expanded = showAgentMenu,
                onDismissRequest = { showAgentMenu = false }
            ) {
                agents.forEach { agent ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = agent.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (agent.type == selectedAgent?.type) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (agent.type == selectedAgent?.type) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
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
