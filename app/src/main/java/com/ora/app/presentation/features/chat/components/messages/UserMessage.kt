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
import com.ora.app.presentation.theme.OraTheme

@Composable
fun UserMessage(
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingScreen),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(Dimensions.messageMaxWidth),
            shape = RoundedCornerShape(
                topStart = Dimensions.radiusLg,
                topEnd = Dimensions.radiusLg,
                bottomStart = Dimensions.radiusLg,
                bottomEnd = Dimensions.radiusSm
            ),
            color = OraTheme.colors.userBubble
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = OraTheme.colors.userBubbleText,
                modifier = Modifier.padding(
                    horizontal = Dimensions.spacingMd,
                    vertical = Dimensions.spacing12
                )
            )
        }
    }
}
