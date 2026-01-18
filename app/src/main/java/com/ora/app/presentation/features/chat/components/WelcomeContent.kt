package com.ora.app.presentation.features.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ora.app.R
import com.ora.app.domain.model.Agent
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraTheme
import kotlinx.coroutines.delay

@Composable
fun WelcomeContent(
    agent: Agent?,
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
        delay(200)
        showSubtitle = true
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.spacing32),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(300)) + slideInVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    initialOffsetY = { 15 }
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ora_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacing20))

            // Main greeting
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400)) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Text(
                    text = agent?.greeting ?: stringResource(R.string.how_can_i_help),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                        lineHeight = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacing16))

            // Subtle hint
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(tween(300))
            ) {
                Text(
                    text = stringResource(R.string.ask_anything),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OraTheme.colors.textTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
