package com.ora.app.presentation.features.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.ora.app.presentation.components.common.OraButton
import com.ora.app.presentation.components.common.OraTextField
import com.ora.app.presentation.theme.Dimensions
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AuthEffect.NavigateToChat -> onRegisterSuccess()
                is AuthEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimensions.paddingScreen)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimensions.spacingXl))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSm))

            Text(
                text = "Join Ora today",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXl))

            OraTextField(
                value = state.name,
                onValueChange = { viewModel.dispatch(AuthIntent.UpdateName(it)) },
                label = "Name",
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = state.nameError != null,
                errorMessage = state.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMd))

            OraTextField(
                value = state.email,
                onValueChange = { viewModel.dispatch(AuthIntent.UpdateEmail(it)) },
                label = "Email",
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                isError = state.emailError != null,
                errorMessage = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMd))

            OraTextField(
                value = state.password,
                onValueChange = { viewModel.dispatch(AuthIntent.UpdatePassword(it)) },
                label = "Password",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(AuthIntent.TogglePasswordVisibility) }
                    ) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMd))

            OraTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.dispatch(AuthIntent.UpdateConfirmPassword(it)) },
                label = "Confirm Password",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.dispatch(AuthIntent.Register)
                    }
                )
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingLg))

            OraButton(
                text = "Create Account",
                onClick = { viewModel.dispatch(AuthIntent.Register) },
                enabled = state.isRegisterValid,
                loading = state.isLoading
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMd))

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Sign In")
            }
        }
    }
}
