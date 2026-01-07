package com.ora.app.presentation.features.chat.components.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import com.ora.app.presentation.designsystem.components.ScrollFadeContainer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ora.app.R
import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.Session
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraTheme

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
    val drawerShape = RoundedCornerShape(16.dp)

    ModalDrawerSheet(
        modifier = modifier.width(316.dp),
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    start = Dimensions.spacing12,
                    end = Dimensions.spacing12,
                    top = Dimensions.spacing12,
                    bottom = Dimensions.spacing12
                )
                .shadow(
                    elevation = 8.dp,
                    shape = drawerShape,
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(drawerShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = OraTheme.colors.borderSubtle,
                    shape = drawerShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = Dimensions.spacing20)
            ) {
            // Logo and brand
            Row(
                modifier = Modifier.padding(horizontal = Dimensions.spacing20),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ora_logo),
                    contentDescription = "Ora",
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.width(Dimensions.spacing10))
                Text(
                    text = "Ora",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacing16))

            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.padding(horizontal = Dimensions.spacing12)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacing12))

            // Sessions list with fade
            val sessionsListState = rememberLazyListState()

            ScrollFadeContainer(
                listState = sessionsListState,
                modifier = Modifier.weight(1f),
                fadeColor = MaterialTheme.colorScheme.surface
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = sessionsListState,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(
                        items = sessions,
                        key = { _, session -> session.id }
                    ) { index, session ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(150, delayMillis = index * 20)) +
                                    slideInVertically(
                                        animationSpec = tween(150, delayMillis = index * 20),
                                        initialOffsetY = { 8 }
                                    )
                        ) {
                            SessionItem(
                                session = session,
                                isSelected = session.id == activeSessionId,
                                onClick = { onSessionClick(session.id) },
                                onDelete = { onDeleteSession(session.id) }
                            )
                        }
                    }

                // Empty state
                if (sessions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.spacing32),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No conversations yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Dimensions.spacing4))
                                Text(
                                    text = "Start a new chat",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OraTheme.colors.textTertiary
                                )
                            }
                        }
                    }
                }
                }
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimensions.spacing16),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacing12)
            ) {
                DrawerFooterItem(
                    icon = Icons.Outlined.Person,
                    label = "Profile",
                    onClick = onProfileClick
                )

                Spacer(modifier = Modifier.height(Dimensions.spacing4))

                DrawerFooterItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    label = "Sign out",
                    onClick = onLogout
                )
            }
            }
        }
    }
}

@Composable
private fun DrawerFooterItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = Dimensions.spacing12,
                vertical = Dimensions.spacing12
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(Dimensions.spacing12))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
