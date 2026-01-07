package com.ora.app.presentation.components.toast

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ora.app.presentation.theme.Dimensions

/**
 * Toast host composable that displays toasts from the ToastManager.
 * Toasts stack on top of each other with a visual overlay effect.
 *
 * Usage in MainActivity:
 * ```
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // Your app content
 *     NavGraph(navController = navController)
 *
 *     // Toast overlay
 *     ToastHost()
 * }
 * ```
 */
@Composable
fun ToastHost(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter
) {
    val toasts by ToastManager.toasts.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = Dimensions.spacingXl),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Render toasts with stable keys - newest toast has highest z-index
            toasts.forEachIndexed { index, toast ->
                val stackIndex = toasts.size - 1 - index // 0 = top (newest), higher = behind

                // Animation values based on stack position
                val targetOffset = (stackIndex * 8).dp
                val targetScale = 1f - (stackIndex * 0.05f)
                val targetAlpha = 1f - (stackIndex * 0.15f)

                val animatedOffset by animateDpAsState(
                    targetValue = targetOffset,
                    animationSpec = tween(300),
                    label = "offset_${toast.id}"
                )
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale.coerceAtLeast(0.85f),
                    animationSpec = tween(300),
                    label = "scale_${toast.id}"
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha.coerceAtLeast(0.5f),
                    animationSpec = tween(300),
                    label = "alpha_${toast.id}"
                )

                key(toast.id) {
                    OraToast(
                        toast = toast,
                        onDismiss = { ToastManager.dismiss(toast.id) },
                        modifier = Modifier
                            .zIndex((toasts.size - stackIndex).toFloat())
                            .offset(y = -animatedOffset)
                            .scale(animatedScale)
                            .alpha(animatedAlpha)
                    )
                }
            }
        }
    }
}

/**
 * Alternative position options for ToastHost
 */
object ToastPosition {
    val Top = Alignment.TopCenter
    val Bottom = Alignment.BottomCenter
    val TopStart = Alignment.TopStart
    val TopEnd = Alignment.TopEnd
    val BottomStart = Alignment.BottomStart
    val BottomEnd = Alignment.BottomEnd
}
