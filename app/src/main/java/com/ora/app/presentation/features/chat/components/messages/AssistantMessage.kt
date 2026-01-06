package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ora.app.domain.model.FeedbackState
import com.ora.app.domain.model.InteractionStatus
import com.ora.app.domain.model.ToolCall
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
    Column(modifier = modifier.fillMaxWidth()) {
        // LG: Tool calls si présents
        if (toolCalls.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)) {
                toolCalls.forEach { toolCall ->
                    ToolCallCard(toolCall = toolCall)
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.spacingMd))
        }

        // LG: Thinking indicator quand en attente
        if (status == InteractionStatus.THINKING || status == InteractionStatus.PENDING) {
            ThinkingIndicator()
        } else if (content.isNotEmpty()) {
            // LG: Contenu Markdown
            Row {
                MarkdownContent(
                    content = content,
                    modifier = Modifier.weight(1f)
                )

                // LG: Streaming cursor
                if (status == InteractionStatus.STREAMING) {
                    StreamingCursor()
                }
            }

            // LG: Footer seulement pour les messages complets
            if (status == InteractionStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(Dimensions.spacingMd))
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
