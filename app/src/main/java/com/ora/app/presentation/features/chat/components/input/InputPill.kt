package com.ora.app.presentation.features.chat.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ora.app.R
import java.util.UUID

// ============================================================================
// Pill Types & Colors
// ============================================================================

enum class PillType(@StringRes val labelResId: Int, val color: Color) {
    WEB_SEARCH(R.string.pill_web_search, Color(0xFF3B82F6)),   // Blue
    CODE(R.string.pill_code, Color(0xFF10B981)),                // Green
    CREATIVE(R.string.pill_creative, Color(0xFF8B5CF6)),        // Purple
    ANALYSIS(R.string.pill_analysis, Color(0xFFF59E0B))         // Orange
}

data class InputPill(
    val id: String = UUID.randomUUID().toString(),
    val type: PillType
)

// ============================================================================
// Pill Composable
// ============================================================================

@Composable
fun PillChip(
    pill: InputPill,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "pillScale"
    )

    val backgroundColor = pill.type.color.copy(alpha = 0.15f)
    val contentColor = pill.type.color

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(
                start = 10.dp,
                end = 6.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(pill.type.labelResId),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            color = contentColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(R.string.remove),
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onRemove
                ),
            tint = contentColor.copy(alpha = 0.7f)
        )
    }
}

// ============================================================================
// Pills Row
// ============================================================================

@Composable
fun PillsRow(
    pills: List<InputPill>,
    onRemovePill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pills.isEmpty()) return

    LazyRow(
        modifier = modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = pills,
            key = { it.id }
        ) { pill ->
            PillChip(
                pill = pill,
                onRemove = { onRemovePill(pill.id) }
            )
        }
    }
}
