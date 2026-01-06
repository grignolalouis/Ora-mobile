package com.ora.app.presentation.designsystem.components

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.Heading
import org.commonmark.node.IndentedCodeBlock

// ============================================================================
// ORA Design System - Markdown Renderer
// Clean markdown rendering with Markwon - Gemini style
// ============================================================================

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColor = MaterialTheme.colorScheme.tertiary.toArgb()
    val codeBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh.toArgb()
    val codeTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val markwon = remember(isDarkTheme) {
        Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder
                        .codeTextColor(codeTextColor)
                        .codeBackgroundColor(codeBackgroundColor)
                        .codeBlockTextColor(codeTextColor)
                        .codeBlockBackgroundColor(codeBackgroundColor)
                        .codeTypeface(Typeface.MONOSPACE)
                        .codeBlockTypeface(Typeface.MONOSPACE)
                        .codeTextSize(14)
                        .codeBlockTextSize(14)
                        .linkColor(linkColor)
                        .headingBreakHeight(0)
                }
            })
            .build()
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(textColor)
                textSize = 16f
                setLineSpacing(0f, 1.4f)
                // Disable link clicking for now (can be enabled if needed)
                linksClickable = true
                isClickable = false
                isFocusable = false
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
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

    val markwon = remember {
        Markwon.create(context)
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(textColor)
                textSize = 15f
                this.maxLines = maxLines
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            textView.maxLines = maxLines
            markwon.setMarkdown(textView, markdown)
        }
    )
}
