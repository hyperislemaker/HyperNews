package com.hypernews.app.presentation.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setProfileImage(it) }
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onSetupComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Oluştur") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profil Resmi Seçici
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.profileImageUri != null) {
                    AsyncImage(
                        model = uiState.profileImageUri,
                        contentDescription = "Profil resmi",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (uiState.avatarText != null) {
                    Text(
                        text = uiState.avatarText!!,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Fotoğraf ekle",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Fotoğraf Seç")
            }

            if (uiState.imageSizeError != null) {
                Text(
                    text = uiState.imageSizeError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kullanıcı Adı
            OutlinedTextField(
                value = uiState.userName,
                onValueChange = { viewModel.setUserName(it) },
                label = { Text("Kullanıcı Adı") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                trailingIcon = {
                    when {
                        uiState.isCheckingUserName -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        uiState.isUserNameAvailable == true -> {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Kullanılabilir",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        uiState.isUserNameAvailable == false -> {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Kullanılamaz",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                singleLine = true,
                isError = uiState.userNameError != null,
                supportingText = {
                    when {
                        uiState.userNameError != null -> Text(uiState.userNameError!!)
                        uiState.isUserNameAvailable == true -> Text("Bu kullanıcı adı kullanılabilir")
                        uiState.isUserNameAvailable == false -> Text("Bu kullanıcı adı zaten alınmış")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Alternatif öneriler
            if (uiState.suggestedUserNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Öneriler:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.suggestedUserNames.take(3).forEach { suggestion ->
                        SuggestionChip(
                            onClick = { viewModel.setUserName(suggestion) },
                            label = { Text(suggestion) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tamamla butonu
            Button(
                onClick = { viewModel.completeSetup() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.canComplete && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Profili Oluştur")
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
