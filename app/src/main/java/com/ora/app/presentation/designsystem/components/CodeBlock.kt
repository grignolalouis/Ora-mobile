package com.ora.app.presentation.designsystem.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Theme-adaptive code block colors
data class CodeBlockColors(
    val background: Color,
    val headerBackground: Color,
    val text: Color,
    val secondaryText: Color,
    val successColor: Color
)

@Composable
private fun codeBlockColors(): CodeBlockColors {
    // Detect dark mode by checking the luminance of the app's background color
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDark = backgroundColor.luminance() < 0.5f

    return if (isDark) {
        // Dark theme - Gruvbox dark inspired
        CodeBlockColors(
            background = Color(0xFF1D2021),
            headerBackground = Color(0xFF282828),
            text = Color(0xFFEBDBB2),
            secondaryText = Color(0xFF928374),
            successColor = Color(0xFFB8BB26)
        )
    } else {
        // Light theme - Soft light gray
        CodeBlockColors(
            background = Color(0xFFF5F4F0),
            headerBackground = Color(0xFFEDECE8),
            text = Color(0xFF1A1A18),
            secondaryText = Color(0xFF6B6A68),
            successColor = Color(0xFF16A34A)
        )
    }
}

@Composable
fun CodeBlock(
    code: String,
    language: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }
    val colors = codeBlockColors()

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.background)
    ) {
        // Header with language and copy button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.headerBackground)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language label
            Text(
                text = language?.lowercase() ?: "code",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = colors.secondaryText
            )

            Spacer(modifier = Modifier.weight(1f))

            // Copy button
            CopyButton(
                isCopied = isCopied,
                colors = colors,
                onClick = {
                    copyToClipboard(context, code)
                    isCopied = true
                    scope.launch {
                        delay(2000)
                        isCopied = false
                    }
                }
            )
        }

        // Code content with syntax highlighting
        val backgroundColor = MaterialTheme.colorScheme.background
        val isDark = backgroundColor.luminance() < 0.5f
        val highlightedCode = remember(code, language, isDark) {
            highlightCode(code, language, isDark)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = highlightedCode,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun CopyButton(
    isCopied: Boolean,
    colors: CodeBlockColors,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isCopied) colors.successColor else colors.secondaryText,
        animationSpec = tween(200),
        label = "copyIconColor"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCopied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
            contentDescription = if (isCopied) "Copied" else "Copy code",
            modifier = Modifier.size(14.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isCopied) "Copied!" else "Copy",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp
            ),
            color = iconColor
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("code", text)
    clipboard.setPrimaryClip(clip)
}

// Inline code (single backtick)
@Composable
fun InlineCode(
    code: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = code,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )
}
