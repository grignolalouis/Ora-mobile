package com.ora.app.presentation.features.chat.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.Session
import com.ora.app.presentation.designsystem.components.OraDivider
import com.ora.app.presentation.theme.Dimensions

@Composable
fun SessionDrawer(
    agent: Agent?,
    sessions: List<Session>,
    activeSessionId: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSessionClick: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onNewChat: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(Dimensions.drawerWidth),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = Dimensions.spacingMd)
        ) {
            // Header
            Text(
                text = agent?.name ?: "Ora",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = Dimensions.spacingMd,
                    vertical = Dimensions.spacing8
                )
            )

            // New chat button
            DrawerButton(
                icon = Icons.Outlined.Add,
                label = "New Chat",
                onClick = onNewChat,
                modifier = Modifier.padding(
                    horizontal = Dimensions.spacing8,
                    vertical = Dimensions.spacing4
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacing8))

            OraDivider(modifier = Modifier.padding(horizontal = Dimensions.spacingMd))

            // Search
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )

            // Sessions list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing4)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SessionItem(
                        session = session,
                        isSelected = session.id == activeSessionId,
                        onClick = { onSessionClick(session.id) },
                        onDelete = { onDeleteSession(session.id) }
                    )
                }
            }

            OraDivider(modifier = Modifier.padding(horizontal = Dimensions.spacingMd))

            Spacer(modifier = Modifier.height(Dimensions.spacing8))

            // Profile
            DrawerButton(
                icon = Icons.Outlined.Person,
                label = "Profile",
                onClick = onProfileClick,
                modifier = Modifier.padding(horizontal = Dimensions.spacing8)
            )

            // Logout
            DrawerButton(
                icon = Icons.AutoMirrored.Outlined.Logout,
                label = "Logout",
                onClick = onLogout,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = Dimensions.spacing8)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacing8))
        }
    }
}

@Composable
private fun DrawerButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.radiusMd))
            .clickable(onClick = onClick)
            .padding(
                horizontal = Dimensions.spacing12,
                vertical = Dimensions.spacing12
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimensions.iconSizeMd),
            tint = tint
        )
        Spacer(modifier = Modifier.width(Dimensions.spacing12))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = tint
        )
    }
}
