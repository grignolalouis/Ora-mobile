package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ora.app.domain.model.FeedbackState
import com.ora.app.presentation.theme.Dimensions
import com.ora.app.presentation.theme.OraColors

@Composable
fun MessageFooter(
    feedbackState: FeedbackState,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // LG: Thumbs up
        IconButton(
            onClick = onThumbsUp,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (feedbackState == FeedbackState.POSITIVE) {
                    Icons.Filled.ThumbUp
                } else {
                    Icons.Outlined.ThumbUp
                },
                contentDescription = "Like",
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                tint = if (feedbackState == FeedbackState.POSITIVE) {
                    OraColors.Success
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Spacer(modifier = Modifier.width(Dimensions.spacingXs))

        // LG: Thumbs down
        IconButton(
            onClick = onThumbsDown,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (feedbackState == FeedbackState.NEGATIVE) {
                    Icons.Filled.ThumbDown
                } else {
                    Icons.Outlined.ThumbDown
                },
                contentDescription = "Dislike",
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                tint = if (feedbackState == FeedbackState.NEGATIVE) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Spacer(modifier = Modifier.width(Dimensions.spacingXs))

        // LG: Copy
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
