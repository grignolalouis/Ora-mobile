package com.ora.app.presentation.features.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ora.app.domain.model.FeedbackState
import com.ora.app.presentation.components.common.ErrorDisplay
import com.ora.app.presentation.components.common.LoadingIndicator
import com.ora.app.presentation.features.chat.components.ChatTopBar
import com.ora.app.presentation.features.chat.components.WelcomeContent
import com.ora.app.presentation.features.chat.components.drawer.SessionDrawer
import com.ora.app.presentation.features.chat.components.input.MessageInput
import com.ora.app.presentation.features.chat.components.messages.MessagesList
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    initialAgentType: String? = null,
    viewModel: ChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val listState = rememberLazyListState()

    // Init
    LaunchedEffect(Unit) {
        viewModel.dispatch(ChatIntent.LoadAgents)
    }

    // Select agent if provided
    LaunchedEffect(initialAgentType, state.agents) {
        if (initialAgentType != null && state.agents.isNotEmpty()) {
            viewModel.dispatch(ChatIntent.SelectAgent(initialAgentType))
        }
    }

    // Sync drawer state
    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Closed && state.isDrawerOpen) {
            viewModel.dispatch(ChatIntent.CloseDrawer)
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ChatEffect.ScrollToBottom -> {
                    if (state.interactions.isNotEmpty()) {
                        listState.animateScrollToItem(state.interactions.lastIndex)
                    }
                }
                is ChatEffect.CopiedToClipboard -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("message", effect.message))
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                }
                is ChatEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                ChatEffect.NavigateToLogin -> onLogout()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SessionDrawer(
                agent = state.selectedAgent,
                sessions = state.filteredSessions,
                activeSessionId = state.activeSessionId,
                searchQuery = state.sessionSearchQuery,
                onSearchQueryChange = { viewModel.dispatch(ChatIntent.UpdateSearchQuery(it)) },
                onSessionClick = { viewModel.dispatch(ChatIntent.SelectSession(it)) },
                onDeleteSession = { viewModel.dispatch(ChatIntent.DeleteSession(it)) },
                onNewChat = { viewModel.dispatch(ChatIntent.NewChat) },
                onProfileClick = onNavigateToProfile,
                onLogout = { viewModel.dispatch(ChatIntent.Logout) }
            )
        },
        gesturesEnabled = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .imePadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                ChatTopBar(
                    title = state.activeSession?.title ?: "Ora",
                    agents = state.agents,
                    selectedAgent = state.selectedAgent,
                    onAgentSelected = { viewModel.dispatch(ChatIntent.SelectAgent(it)) },
                    onMenuClick = { viewModel.dispatch(ChatIntent.ToggleDrawer) }
                )

                // Main content
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.isLoadingAgents -> {
                            LoadingIndicator(fullScreen = true)
                        }
                        state.isLoadingHistory -> {
                            LoadingIndicator(fullScreen = true)
                        }
                        state.error != null && state.interactions.isEmpty() -> {
                            ErrorDisplay(
                                message = state.error!!,
                                onRetry = { viewModel.dispatch(ChatIntent.RetryLastAction) }
                            )
                        }
                        state.isWelcomeScreen -> {
                            WelcomeContent(agent = state.selectedAgent)
                        }
                        else -> {
                            MessagesList(
                                interactions = state.interactions,
                                listState = listState,
                                onThumbsUp = { index ->
                                    viewModel.dispatch(
                                        ChatIntent.SetFeedback(index, FeedbackState.POSITIVE)
                                    )
                                },
                                onThumbsDown = { index ->
                                    viewModel.dispatch(
                                        ChatIntent.SetFeedback(index, FeedbackState.NEGATIVE)
                                    )
                                },
                                onCopy = { content ->
                                    viewModel.dispatch(ChatIntent.CopyMessage(content))
                                }
                            )
                        }
                    }
                }

                // Input
                MessageInput(
                    value = state.inputText,
                    onValueChange = { viewModel.dispatch(ChatIntent.UpdateInput(it)) },
                    onSend = { viewModel.dispatch(ChatIntent.SendMessage) },
                    onCancel = { viewModel.dispatch(ChatIntent.CancelStreaming) },
                    isSending = state.isSending,
                    isStreaming = state.isStreaming
                )
            }
        }
    }
}
