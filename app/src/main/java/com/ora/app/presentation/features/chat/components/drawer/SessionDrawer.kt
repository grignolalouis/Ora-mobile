package com.ora.app.presentation.features.chat.components.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.Session
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
        modifier = modifier.width(Dimensions.drawerWidth)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = Dimensions.spacingMd)
        ) {
            // LG: Header
            Text(
                text = agent?.name ?: "Ora",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = Dimensions.spacingMd)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSm))

            // LG: New chat button
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                label = { Text("New Chat") },
                selected = false,
                onClick = onNewChat,
                modifier = Modifier.padding(horizontal = Dimensions.spacingSm)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.spacingSm))

            // LG: Search
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )

            // LG: Sessions list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.spacingSm))

            // LG: Profile
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Profile") },
                selected = false,
                onClick = onProfileClick,
                modifier = Modifier.padding(horizontal = Dimensions.spacingSm)
            )

            // LG: Logout
            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                label = {
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                selected = false,
                onClick = onLogout,
                modifier = Modifier.padding(horizontal = Dimensions.spacingSm)
            )
        }
    }
}
