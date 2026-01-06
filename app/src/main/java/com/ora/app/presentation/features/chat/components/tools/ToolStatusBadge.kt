package com.ora.app.presentation.features.chat.components.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ora.app.domain.model.ToolStatus

@Composable
fun ToolStatusBadge(
    status: ToolStatus,
    modifier: Modifier = Modifier
) {
    val (icon, text, backgroundColor, contentColor) = when (status) {
        ToolStatus.PENDING -> Quad(
            Icons.Default.HourglassEmpty,
            "Pending",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        ToolStatus.RUNNING -> Quad(
            Icons.Default.PlayArrow,
            "Running",
            Color(0xFF3B82F6).copy(alpha = 0.2f),
            Color(0xFF3B82F6)
        )
        ToolStatus.SUCCESS -> Quad(
            Icons.Default.Check,
            "Success",
            Color(0xFF22C55E).copy(alpha = 0.2f),
            Color(0xFF22C55E)
        )
        ToolStatus.ERROR -> Quad(
            Icons.Default.Close,
            "Error",
            Color(0xFFEF4444).copy(alpha = 0.2f),
            Color(0xFFEF4444)
        )
    }

    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
