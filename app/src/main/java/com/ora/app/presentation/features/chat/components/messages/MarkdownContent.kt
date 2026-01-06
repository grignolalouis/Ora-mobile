package com.ora.app.presentation.features.chat.components.messages

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// LG: Composant simple pour le contenu - peut être remplacé par une lib markdown plus tard
@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}
