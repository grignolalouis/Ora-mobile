package com.ora.app.presentation.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A container that adds fade gradients at the top and bottom when content is scrolled.
 * - Top fade appears when scrolled down
 * - Bottom fade appears when there's more content below
 */
@Composable
fun ScrollFadeContainer(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    fadeHeight: Dp = 32.dp,
    fadeColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    // Check if we're scrolled past the first item (show top fade)
    val showTopFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    // Check if we can scroll further down (show bottom fade)
    val showBottomFade by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index < layoutInfo.totalItemsCount - 1
        }
    }

    // Animate the fade opacities
    val topFadeAlpha by animateFloatAsState(
        targetValue = if (showTopFade) 1f else 0f,
        animationSpec = tween(200),
        label = "topFadeAlpha"
    )

    val bottomFadeAlpha by animateFloatAsState(
        targetValue = if (showBottomFade) 1f else 0f,
        animationSpec = tween(200),
        label = "bottomFadeAlpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Content
        content()

        // Top fade gradient overlay
        if (topFadeAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                fadeColor.copy(alpha = topFadeAlpha),
                                fadeColor.copy(alpha = topFadeAlpha * 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Bottom fade gradient overlay
        if (bottomFadeAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadeColor.copy(alpha = bottomFadeAlpha * 0.5f),
                                fadeColor.copy(alpha = bottomFadeAlpha)
                            )
                        )
                    )
            )
        }
    }
}
