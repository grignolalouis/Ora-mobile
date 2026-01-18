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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ora.app.R
import com.ora.app.domain.model.Agent
import com.ora.app.domain.model.Session
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraTheme

private val avatarColors = listOf(
    Color(0xFF6895D2),
    Color(0xFF8EAAB8),
    Color(0xFFC2E38E),
    Color(0xFFFDE767),
    Color(0xFFF8D063),
    Color(0xFFF3B95F),
    Color(0xFFD9654E),
    Color(0xFFD04848)
)

@Composable
fun SessionDrawer(
    userName: String?,
    userProfilePictureUrl: String?,
    agent: Agent?,
    sessions: List<Session>,
    activeSessionId: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSessionClick: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onNewChat: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerShape = RoundedCornerShape(16.dp)

    ModalDrawerSheet(
        modifier = modifier.width(300.dp),
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(12.dp)
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
                        contentDescription = stringResource(R.string.ora_logo),
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacing10))
                    Text(
                        text = stringResource(R.string.app_name),
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
                                            text = stringResource(R.string.no_conversations),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(Dimensions.spacing4))
                                        Text(
                                            text = stringResource(R.string.start_new_chat),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OraTheme.colors.textTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // User profile section at bottom
                UserProfileSection(
                    userName = userName,
                    userProfilePictureUrl = userProfilePictureUrl,
                    onClick = onProfileClick
                )
            }
        }
    }
}

@Composable
private fun UserProfileSection(
    userName: String?,
    userProfilePictureUrl: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        UserAvatar(
            name = userName,
            profilePictureUrl = userProfilePictureUrl,
            size = 36
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name
        Text(
            text = userName ?: stringResource(R.string.user),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserAvatar(
    name: String?,
    profilePictureUrl: String?,
    size: Int
) {
    val gradientColors = remember(name) {
        generateAvatarGradient(name ?: "User")
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .then(
                if (profilePictureUrl == null) {
                    Modifier.background(Brush.linearGradient(gradientColors))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUrl != null) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = stringResource(R.string.profile_picture),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = getInitials(name ?: stringResource(R.string.user)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { name.take(2).uppercase() }
}

private fun generateAvatarGradient(name: String): List<Color> {
    val hash = name.hashCode().let { if (it < 0) -it else it }
    val index = hash % avatarColors.size
    val nextIndex = (index + 1) % avatarColors.size
    return listOf(avatarColors[index], avatarColors[nextIndex])
}
