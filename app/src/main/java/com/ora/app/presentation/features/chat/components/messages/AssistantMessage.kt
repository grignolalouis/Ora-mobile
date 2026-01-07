package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.ora.app.domain.model.FeedbackState
import com.ora.app.domain.model.InteractionStatus
import com.ora.app.domain.model.ToolCall
import com.ora.app.presentation.designsystem.components.MarkdownText
import com.ora.app.presentation.features.chat.components.tools.ToolCallCard
import com.ora.app.presentation.theme.Dimensions

@Composable
fun AssistantMessage(
    content: String,
    status: InteractionStatus,
    feedbackState: FeedbackState,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    toolCalls: List<ToolCall> = emptyList()
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "assistantMessageAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacingMd)
            .alpha(alpha)
    ) {
        // Tool calls if present
        if (toolCalls.isNotEmpty()) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { 10 }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacing8)) {
                    toolCalls.forEach { toolCall ->
                        ToolCallCard(toolCall = toolCall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.spacing12))
        }

        // Thinking indicator when waiting
        if (status == InteractionStatus.THINKING || status == InteractionStatus.PENDING) {
            ThinkingIndicator()
        } else if (content.isNotEmpty()) {
            // Markdown content
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                MarkdownText(
                    markdown = content,
                    modifier = Modifier.weight(1f)
                )

                // Streaming cursor
                if (status == InteractionStatus.STREAMING) {
                    StreamingCursor(
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Footer only for completed messages
            AnimatedVisibility(
                visible = status == InteractionStatus.COMPLETED,
                enter = fadeIn(tween(200))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Dimensions.spacing16))
                    MessageFooter(
                        feedbackState = feedbackState,
                        onThumbsUp = onThumbsUp,
                        onThumbsDown = onThumbsDown,
                        onCopy = onCopy
                    )
                }
            }
        }
    }
}
