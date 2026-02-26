package com.hypernews.app.presentation.screens.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (isNewUser: Boolean) -> Unit,
    onSkip: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && !uiState.needsEmailVerification) {
            onLoginSuccess(uiState.isNewUser)
        }
    }

    // Email verification screen
    if (uiState.needsEmailVerification) {
        EmailVerificationScreen(
            verificationEmailSent = uiState.verificationEmailSent,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onResendEmail = { viewModel.resendVerificationEmail() },
            onCheckVerification = { viewModel.checkEmailVerification() },
            onBack = { viewModel.clearVerificationState() }
        )
        return
    }

    // Forgot password screen
    if (uiState.showForgotPassword) {
        ForgotPasswordScreen(
            isLoading = uiState.isLoading,
            error = uiState.error,
            passwordResetSent = uiState.passwordResetSent,
            onSendResetEmail = { viewModel.sendPasswordResetEmail(it) },
            onBack = { viewModel.hideForgotPassword() }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Başlık
        Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "HyperNews",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (isSignUp) "Hesap Oluştur" else "Giriş Yap",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Google Sign-In
        OutlinedButton(
            onClick = { 
                activity?.let { viewModel.signInWithGoogle(it) }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && activity != null
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Google ile devam et")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  veya  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Şifreyi gizle" else "Şifreyi göster"
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        // Hata mesajı
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Giriş/Kayıt butonu
        Button(
            onClick = {
                if (isSignUp) {
                    viewModel.signUpWithEmail(email, password)
                } else {
                    viewModel.signInWithEmail(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (isSignUp) "Kayıt Ol" else "Giriş Yap")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Kayıt/Giriş geçişi
        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(
                if (isSignUp) "Zaten hesabın var mı? Giriş yap"
                else "Hesabın yok mu? Kayıt ol"
            )
        }

        // Şifremi unuttum
        if (!isSignUp) {
            TextButton(onClick = { viewModel.showForgotPassword() }) {
                Text("Şifremi unuttum")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Atla
        TextButton(onClick = onSkip) {
            Text("Şimdilik atla")
        }
    }
}

@Composable
private fun EmailVerificationScreen(
    verificationEmailSent: Boolean,
    isLoading: Boolean,
    error: String?,
    onResendEmail: () -> Unit,
    onCheckVerification: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MarkEmailRead,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "E-posta Doğrulama",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (verificationEmailSent) 
                "Doğrulama e-postası gönderildi! Lütfen gelen kutunuzu kontrol edin ve e-postadaki bağlantıya tıklayın."
            else 
                "Hesabınızı kullanabilmek için e-posta adresinizi doğrulamanız gerekiyor.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCheckVerification,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Doğrulamayı Kontrol Et")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onResendEmail,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tekrar Gönder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Geri Dön")
        }
    }
}

@Composable
private fun ForgotPasswordScreen(
    isLoading: Boolean,
    error: String?,
    passwordResetSent: Boolean,
    onSendResetEmail: (String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LockReset,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Şifre Sıfırlama",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (passwordResetSent) {
            Text(
                text = "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi. Lütfen gelen kutunuzu kontrol edin.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Giriş Ekranına Dön")
            }
        } else {
            Text(
                text = "E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSendResetEmail(email) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sıfırlama Bağlantısı Gönder")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Geri Dön")
            }
        }
    }
}
