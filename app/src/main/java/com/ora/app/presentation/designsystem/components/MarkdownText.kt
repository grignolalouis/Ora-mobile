package com.ora.app.presentation.designsystem.components

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.ora.app.R
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.linkify.LinkifyPlugin

// ============================================================================
// ORA Design System - Markdown Renderer
// Clean markdown rendering with Markwon and Urbanist font
// Custom code blocks with Gruvbox theme
// ============================================================================

// Regex to find fenced code blocks
private val codeBlockRegex = Regex("```(\\w*)\\n([\\s\\S]*?)```")

// Data class to represent markdown segments
private sealed class MarkdownSegment {
    data class Text(val content: String) : MarkdownSegment()
    data class Code(val language: String?, val code: String) : MarkdownSegment()
}

// Parse markdown into segments with safety checks
private fun parseMarkdown(markdown: String): List<MarkdownSegment> {
    if (markdown.isBlank()) {
        return emptyList()
    }

    return try {
        val segments = mutableListOf<MarkdownSegment>()
        var lastEnd = 0

        codeBlockRegex.findAll(markdown).forEach { match ->
            // Add text before this code block
            if (match.range.first > lastEnd) {
                val textContent = markdown.substring(lastEnd, match.range.first).trim()
                if (textContent.isNotEmpty()) {
                    segments.add(MarkdownSegment.Text(textContent))
                }
            }

            // Add code block
            val language = match.groupValues.getOrNull(1)?.takeIf { it.isNotEmpty() }
            val code = match.groupValues.getOrNull(2)?.trimEnd() ?: ""
            if (code.isNotEmpty()) {
                segments.add(MarkdownSegment.Code(language, code))
            }

            lastEnd = match.range.last + 1
        }

        // Add remaining text after last code block
        if (lastEnd < markdown.length) {
            val textContent = markdown.substring(lastEnd).trim()
            if (textContent.isNotEmpty()) {
                segments.add(MarkdownSegment.Text(textContent))
            }
        }

        // If no code blocks found, return whole text
        if (segments.isEmpty() && markdown.isNotBlank()) {
            segments.add(MarkdownSegment.Text(markdown))
        }

        segments
    } catch (_: Exception) {
        // Fallback: return entire markdown as text
        listOf(MarkdownSegment.Text(markdown))
    }
}

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val segments = remember(markdown) { parseMarkdown(markdown) }

    // If just one text segment, render directly without Column overhead
    if (segments.size == 1 && segments[0] is MarkdownSegment.Text) {
        MarkdownTextContent(
            markdown = (segments[0] as MarkdownSegment.Text).content,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is MarkdownSegment.Text -> {
                    MarkdownTextContent(
                        markdown = segment.content,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is MarkdownSegment.Code -> {
                    CodeBlock(
                        code = segment.code,
                        language = segment.language,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkdownTextContent(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val density = LocalDensity.current

    // Colors
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val codeBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.toArgb()
    val codeTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    // Load Urbanist font
    val urbanistRegular = remember {
        ResourcesCompat.getFont(context, R.font.urbanist_regular)
    }
    val urbanistBold = remember {
        ResourcesCompat.getFont(context, R.font.urbanist_bold)
    }

    // Text sizes in sp converted to pixels for Markwon
    val codeTextSizePx = with(density) { 13.sp.toPx() }.toInt()

    val markwon = remember(isDarkTheme) {
        Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder
                        // Inline code styling
                        .codeTextColor(codeTextColor)
                        .codeBackgroundColor(codeBackgroundColor)
                        .codeTypeface(Typeface.MONOSPACE)
                        .codeTextSize(codeTextSizePx)
                        // Block code (fallback, should be handled by CodeBlock)
                        .codeBlockTextColor(codeTextColor)
                        .codeBlockBackgroundColor(codeBackgroundColor)
                        .codeBlockTypeface(Typeface.MONOSPACE)
                        .codeBlockTextSize(codeTextSizePx)
                        // Links
                        .linkColor(linkColor)
                        // Headings
                        .headingBreakHeight(0)
                        .headingTypeface(urbanistBold ?: Typeface.DEFAULT_BOLD)
                        .headingTextSizeMultipliers(
                            floatArrayOf(1.5f, 1.25f, 1.125f, 1f, 0.875f, 0.75f)
                        )
                        // Lists
                        .bulletWidth((6 * density.density).toInt())
                        .listItemColor(textColor)
                        // Block quote
                        .blockQuoteColor(codeBackgroundColor)
                        .blockQuoteWidth((4 * density.density).toInt())
                        .blockMargin((16 * density.density).toInt())
                }
            })
            .build()
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(textColor)
                typeface = urbanistRegular
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setLineSpacing(0f, 1.5f)
                letterSpacing = 0.01f
                linksClickable = true
                isClickable = false
                isFocusable = false
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            textView.typeface = urbanistRegular
            markwon.setMarkdown(textView, markdown)
        }
    )
}

// ============================================================================
// Simple text-only variant (for previews/summaries)
// ============================================================================

@Composable
fun MarkdownTextSimple(
    markdown: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val urbanistRegular = remember {
        ResourcesCompat.getFont(context, R.font.urbanist_regular)
    }

    val markwon = remember {
        Markwon.create(context)
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(textColor)
                typeface = urbanistRegular
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                this.maxLines = maxLines
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            textView.typeface = urbanistRegular
            textView.maxLines = maxLines
            markwon.setMarkdown(textView, markdown)
        }
    )
}
