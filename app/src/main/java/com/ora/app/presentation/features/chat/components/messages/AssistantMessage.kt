package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingScreen)
    ) {
        // Tool calls if present
        if (toolCalls.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacing8)) {
                toolCalls.forEach { toolCall ->
                    ToolCallCard(toolCall = toolCall)
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.spacing12))
        }

        // Thinking indicator when waiting
        if (status == InteractionStatus.THINKING || status == InteractionStatus.PENDING) {
            ThinkingIndicator()
        } else if (content.isNotEmpty()) {
            // Markdown content with streaming cursor
            Row(modifier = Modifier.fillMaxWidth()) {
                MarkdownText(
                    markdown = content,
                    modifier = Modifier.weight(1f)
                )

                // Streaming cursor
                if (status == InteractionStatus.STREAMING) {
                    StreamingCursor()
                }
            }

            // Footer only for completed messages
            if (status == InteractionStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(Dimensions.spacing12))
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
