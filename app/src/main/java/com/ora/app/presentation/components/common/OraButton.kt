package com.ora.app.presentation.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ora.app.presentation.theme.Dimensions

@Composable
fun OraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    outlined: Boolean = false
) {
    val buttonModifier = modifier
        .fillMaxWidth()
        .height(Dimensions.buttonHeight)

    val shape = RoundedCornerShape(Dimensions.radiusMd)

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !loading,
            shape = shape
        ) {
            ButtonContent(text = text, loading = loading)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled && !loading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            ButtonContent(text = text, loading = loading)
        }
    }
}

@Composable
private fun ButtonContent(text: String, loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
