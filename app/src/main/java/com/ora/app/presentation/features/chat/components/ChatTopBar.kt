package com.ora.app.presentation.features.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ora.app.domain.model.Agent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String,
    agents: List<Agent>,
    selectedAgent: Agent?,
    onAgentSelected: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    var showAgentMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // LG: Agent selector
            Row(
                modifier = Modifier.clickable { if (agents.isNotEmpty()) showAgentMenu = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedAgent?.name ?: title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (agents.size > 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select agent"
                    )
                }

                DropdownMenu(
                    expanded = showAgentMenu,
                    onDismissRequest = { showAgentMenu = false }
                ) {
                    agents.forEach { agent ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = agent.name,
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
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
