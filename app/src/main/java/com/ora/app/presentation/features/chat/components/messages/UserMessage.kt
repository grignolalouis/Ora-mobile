package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraTheme

@Composable
fun UserMessage(
    content: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "userMessageAlpha"
    )

    // Claude-style bubble shape - fully rounded
    val bubbleShape = RoundedCornerShape(20.dp)

    // Max width based on screen
    val configuration = LocalConfiguration.current
    val maxWidth = (configuration.screenWidthDp * 0.75f).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacingMd)
            .alpha(alpha),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .shadow(
                    elevation = 6.dp,
                    shape = bubbleShape,
                    spotColor = Color.Black.copy(alpha = 0.22f),
                    ambientColor = Color.Black.copy(alpha = 0.12f)
                )
                .clip(bubbleShape)
                .background(OraTheme.colors.userBubble)
                .border(
                    width = 1.dp,
                    color = OraTheme.colors.borderSubtle,
                    shape = bubbleShape
                )
                .padding(
                    horizontal = Dimensions.spacing16,
                    vertical = Dimensions.spacing14
                )
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.1f
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
