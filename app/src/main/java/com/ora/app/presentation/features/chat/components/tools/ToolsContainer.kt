package com.ora.app.presentation.features.chat.components.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ora.app.domain.model.ToolCall
import com.ora.app.domain.model.ToolStatus
import com.ora.app.presentation.theme.OraColors

@Composable
fun ToolsContainer(
    toolCalls: List<ToolCall>,
    modifier: Modifier = Modifier
) {
    if (toolCalls.isEmpty()) return

    var isExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "chevronRotation"
    )

    val hasRunning = toolCalls.any { it.status == ToolStatus.RUNNING }
    val hasError = toolCalls.any { it.status == ToolStatus.ERROR }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(200))
    ) {
        // Header - always visible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { isExpanded = !isExpanded }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            ToolsStatusIndicator(
                isRunning = hasRunning,
                hasError = hasError,
                isComplete = !hasRunning && !hasError
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            Text(
                text = when {
                    hasRunning -> "Working..."
                    hasError -> "Completed with errors"
                    else -> "Worked on ${toolCalls.size} step${if (toolCalls.size > 1) "s" else ""}"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            // Chevron
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(chevronRotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Expandable content - Stepper
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                toolCalls.forEachIndexed { index, toolCall ->
                    ToolStep(
                        toolCall = toolCall,
                        isLast = index == toolCalls.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolsStatusIndicator(
    isRunning: Boolean,
    hasError: Boolean,
    isComplete: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val color = when {
        isRunning -> OraColors.Info
        hasError -> OraColors.Error
        isComplete -> OraColors.Success
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (isRunning) pulseAlpha * 0.2f else 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = if (isRunning) pulseAlpha else 1f))
        )
    }
}

@Composable
private fun ToolStep(
    toolCall: ToolCall,
    isLast: Boolean
) {
    var isDetailExpanded by remember { mutableStateOf(false) }
    val detailChevronRotation by animateFloatAsState(
        targetValue = if (isDetailExpanded) 90f else 0f,
        animationSpec = tween(150),
        label = "detailChevron"
    )

    val hasDetails = toolCall.arguments.isNotEmpty() || toolCall.result != null || toolCall.error != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Stepper column (dot + line)
        Box(
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Vertical line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .width(1.5.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                )
            }
            // Dot on top of line
            Box(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(getStatusColor(toolCall.status))
            )
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 4.dp else 24.dp)
        ) {
            // Tool name row - clickable if has details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (hasDetails) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isDetailExpanded = !isDetailExpanded }
                        } else Modifier
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand arrow (only if has details)
                if (hasDetails) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(detailChevronRotation),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Tool name
                Text(
                    text = formatToolName(toolCall.name),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Status indicator
                StepStatus(status = toolCall.status, durationMs = toolCall.durationMs)
            }

            // Expandable details
            AnimatedVisibility(
                visible = isDetailExpanded && hasDetails,
                enter = expandVertically(animationSpec = tween(150)),
                exit = shrinkVertically(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 22.dp, end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Arguments
                    if (toolCall.arguments.isNotEmpty()) {
                        ParametersSection(
                            title = "Parameters",
                            entries = toolCall.arguments.map { (k, v) -> k to v.toString() }
                        )
                    }

                    // Result
                    toolCall.result?.let { result ->
                        ResultSection(
                            title = "Result",
                            content = result,
                            isError = false
                        )
                    }

                    // Error
                    toolCall.error?.let { error ->
                        ResultSection(
                            title = "Error",
                            content = error,
                            isError = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepStatus(
    status: ToolStatus,
    durationMs: Long?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Duration
        if (status == ToolStatus.SUCCESS && durationMs != null) {
            Text(
                text = formatDuration(durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status icon
        when (status) {
            ToolStatus.SUCCESS -> {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Success",
                    modifier = Modifier.size(16.dp),
                    tint = OraColors.Success
                )
            }
            ToolStatus.ERROR -> {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Error",
                    modifier = Modifier.size(16.dp),
                    tint = OraColors.Error
                )
            }
            ToolStatus.RUNNING -> {
                RunningDots()
            }
            ToolStatus.PENDING -> {}
        }
    }
}

@Composable
private fun RunningDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dotsTransition")
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(dotAlpha1, dotAlpha2, dotAlpha3).forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(OraColors.Info.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ParametersSection(
    title: String,
    entries: List<Pair<String, String>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            entries.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSection(
    title: String,
    content: String,
    isError: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) OraColors.Error else MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isError) {
                        OraColors.Error.copy(alpha = 0.06f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    }
                )
                .padding(12.dp)
        ) {
            Text(
                text = content.take(800) + if (content.length > 800) "\n..." else "",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                ),
                color = if (isError) OraColors.Error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun getStatusColor(status: ToolStatus) = when (status) {
    ToolStatus.PENDING -> MaterialTheme.colorScheme.outlineVariant
    ToolStatus.RUNNING -> OraColors.Info
    ToolStatus.SUCCESS -> OraColors.Success
    ToolStatus.ERROR -> OraColors.Error
}

private fun formatToolName(name: String): String {
    return name
        .replace("_", " ")
        .replace("-", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

private fun formatDuration(ms: Long): String {
    return when {
        ms < 1000 -> "${ms}ms"
        ms < 60000 -> "${ms / 1000}.${(ms % 1000) / 100}s"
        else -> "${ms / 60000}m ${(ms % 60000) / 1000}s"
    }
}
