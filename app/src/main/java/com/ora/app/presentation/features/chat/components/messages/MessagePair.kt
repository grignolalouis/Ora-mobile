package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ora.app.domain.model.Interaction
import com.ora.app.presentation.theme.Dimensions

@Composable
fun MessagePair(
    interaction: Interaction,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // LG: User message
        UserMessage(content = interaction.userMessage)

        Spacer(modifier = Modifier.height(Dimensions.messageSpacing))

        // LG: Assistant response
        AssistantMessage(
            content = interaction.assistantResponse,
            status = interaction.status,
            feedbackState = interaction.feedbackState,
            onThumbsUp = onThumbsUp,
            onThumbsDown = onThumbsDown,
            onCopy = onCopy,
            toolCalls = interaction.toolCalls
        )
    }
}
