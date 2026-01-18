package com.ora.app.presentation.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ora.app.R
import com.ora.app.core.storage.Language
import com.ora.app.core.storage.LanguagePreferences
import com.ora.app.core.storage.ThemeMode
import com.ora.app.core.storage.ThemePreferences
import com.ora.app.domain.model.User
import com.ora.app.presentation.designsystem.components.toast.ToastManager
import com.ora.app.presentation.designsystem.components.LoadingIndicator
import com.ora.app.presentation.designsystem.components.OraButton
import com.ora.app.presentation.designsystem.components.OraButtonStyle
import com.ora.app.presentation.designsystem.theme.Dimensions
import com.ora.app.presentation.designsystem.theme.OraColors
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    onLanguageChanged: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val contentType = context.contentResolver.getType(it) ?: "image/jpeg"
                val fileName = "profile_${System.currentTimeMillis()}.${
                    contentType.substringAfter("/")
                }"
                viewModel.dispatch(
                    UserProfileIntent.UploadProfilePicture(
                        fileName = fileName,
                        contentType = contentType,
                        fileBytes = bytes
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.dispatch(UserProfileIntent.LoadUser)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UserProfileEffect.NavigateToLogin -> onLogout()
                is UserProfileEffect.ShowToast -> {
                    val message = effect.messageResId?.let { context.getString(it) }
                        ?: effect.message
                        ?: return@collect
                    ToastManager.info(message)
                }
            }
        }
    }

    if (state.showDeleteConfirmation) {
        DeleteAccountDialog(
            onConfirm = { viewModel.dispatch(UserProfileIntent.ConfirmDeleteAccount) },
            onDismiss = { viewModel.dispatch(UserProfileIntent.HideDeleteConfirmation) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Minimal top bar
            TopBar(onBackClick = onNavigateBack)

            when {
                state.isLoading || state.isDeleting -> {
                    LoadingIndicator(fullScreen = true)
                }
                state.error != null -> {
                    ErrorState(
                        error = state.error!!,
                        onRetry = { viewModel.dispatch(UserProfileIntent.LoadUser) }
                    )
                }
                state.user != null -> {
                    ProfileContent(
                        user = state.user!!,
                        isUploadingPicture = state.isUploadingPicture,
                        onAvatarClick = { imagePicker.launch("image/*") },
                        onLogout = { viewModel.dispatch(UserProfileIntent.Logout) },
                        onDeleteAccount = { viewModel.dispatch(UserProfileIntent.ShowDeleteConfirmation) },
                        themePreferences = themePreferences,
                        languagePreferences = languagePreferences,
                        onLanguageChanged = onLanguageChanged
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingScreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        OraButton(
            text = stringResource(R.string.retry),
            onClick = onRetry,
            style = OraButtonStyle.Secondary
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isUploadingPicture: Boolean,
    onAvatarClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    onLanguageChanged: () -> Unit
) {
    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val language by languagePreferences.language.collectAsState(initial = Language.SYSTEM)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Avatar section - centered
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Avatar(
                user = user,
                isUploading = isUploadingPicture,
                onClick = onAvatarClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Settings sections
        SettingsSection(title = stringResource(R.string.preferences)) {
            SettingsItem(
                label = stringResource(R.string.theme),
                value = when (themeMode) {
                    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                    ThemeMode.DARK -> stringResource(R.string.theme_dark)
                },
                onClick = {
                    val nextMode = when (themeMode) {
                        ThemeMode.SYSTEM -> ThemeMode.LIGHT
                        ThemeMode.LIGHT -> ThemeMode.DARK
                        ThemeMode.DARK -> ThemeMode.SYSTEM
                    }
                    scope.launch { themePreferences.setThemeMode(nextMode) }
                }
            )
            SettingsDivider()
            SettingsItem(
                label = stringResource(R.string.language),
                value = language.displayName,
                onClick = {
                    val nextLanguage = when (language) {
                        Language.SYSTEM -> Language.EN
                        Language.EN -> Language.FR
                        Language.FR -> Language.ES
                        Language.ES -> Language.SYSTEM
                    }
                    scope.launch {
                        languagePreferences.setLanguage(nextLanguage)
                        onLanguageChanged()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = stringResource(R.string.account)) {
            SettingsItem(
                label = stringResource(R.string.role),
                value = user.role.replaceFirstChar { it.uppercase() }
            )
            SettingsDivider()
            SettingsItem(
                label = stringResource(R.string.email_status),
                value = if (user.verifiedEmail) stringResource(R.string.verified) else stringResource(R.string.not_verified),
                valueColor = if (user.verifiedEmail) OraColors.Success else OraColors.Warning
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection {
            SettingsItem(
                label = stringResource(R.string.sign_out),
                onClick = onLogout
            )
            SettingsDivider()
            SettingsItem(
                label = stringResource(R.string.delete_account),
                labelColor = OraColors.Error,
                onClick = onDeleteAccount
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun Avatar(
    user: User,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    val gradientColors = remember(user.id) {
        generateAvatarGradient(user.name)
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .then(
                if (user.profilePictureUrl == null) {
                    Modifier.background(Brush.linearGradient(gradientColors))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
                }
            )
            .clickable(enabled = !isUploading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            isUploading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            }
            user.profilePictureUrl != null -> {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = stringResource(R.string.profile_picture),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Edit overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = stringResource(R.string.change_photo),
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
            else -> {
                // Initials
                Text(
                    text = getInitials(user.name),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                // Subtle edit hint
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = stringResource(R.string.add_photo),
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        if (title != null) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    label: String,
    value: String? = null,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = labelColor
        )

        if (value != null || onClick != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = valueColor
                    )
                }
                if (onClick != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_account_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_account_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.delete),
                    color = OraColors.Error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

// Utility functions

private fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { name.take(2).uppercase() }
}

private val avatarColors = listOf(
    Color(0xFF6895D2),
    Color(0xFF8EAAB8),
    Color(0xFFC2E38E),
    Color(0xFFFDE767),
    Color(0xFFF8D063),
    Color(0xFFF3B95F),
    Color(0xFFD9654E),
    Color(0xFFD04848)
)

private fun generateAvatarGradient(name: String): List<Color> {
    val hash = name.hashCode().let { if (it < 0) -it else it }
    val index = hash % avatarColors.size
    val nextIndex = (index + 1) % avatarColors.size
    return listOf(avatarColors[index], avatarColors[nextIndex])
}
