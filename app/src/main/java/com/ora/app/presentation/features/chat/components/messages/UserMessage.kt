package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.UserBubble
import com.ora.app.presentation.theme.UserBubbleText

@Composable
fun UserMessage(
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(Dimensions.messageBubbleMaxWidth),
            shape = RoundedCornerShape(
                topStart = Dimensions.radiusLg,
                topEnd = Dimensions.radiusLg,
                bottomStart = Dimensions.radiusLg,
                bottomEnd = Dimensions.radiusSm
            ),
            color = UserBubble
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = UserBubbleText,
                modifier = Modifier.padding(Dimensions.paddingCard)
            )
        }
    }
}
