package com.hypernews.app.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hypernews.app.domain.model.ThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRssManagement: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profil Bölümü
            item {
                SettingsSectionHeader(title = "Profil")
            }

            item {
                if (uiState.isLoggedIn) {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = uiState.userName ?: "Profil",
                        subtitle = uiState.userEmail,
                        onClick = onNavigateToProfile
                    )
                } else {
                    SettingsItem(
                        icon = Icons.Default.Login,
                        title = "Giriş Yap",
                        subtitle = "Yorum yapmak ve tepki vermek için giriş yapın",
                        onClick = onNavigateToLogin
                    )
                }
            }

            // RSS Yönetimi
            item {
                SettingsSectionHeader(title = "RSS Kaynakları")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.RssFeed,
                    title = "RSS Kaynaklarını Yönet",
                    subtitle = "${uiState.feedCount} kaynak aktif",
                    onClick = onNavigateToRssManagement
                )
            }

            // Bildirim Ayarları
            item {
                SettingsSectionHeader(title = "Bildirimler")
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Bildirimler",
                    subtitle = "Yeni haberler için bildirim al",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.NotificationsActive,
                    title = "Son Dakika Bildirimleri",
                    subtitle = "Son dakika haberleri için özel bildirim",
                    checked = uiState.breakingNewsNotificationsEnabled,
                    onCheckedChange = { viewModel.setBreakingNewsNotificationsEnabled(it) }
                )
            }

            // Görünüm Ayarları
            item {
                SettingsSectionHeader(title = "Görünüm")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Tema",
                    subtitle = when (uiState.themePreference) {
                        ThemePreference.LIGHT -> "Açık"
                        ThemePreference.DARK -> "Koyu"
                        ThemePreference.SYSTEM -> "Sistem"
                    },
                    onClick = { showThemeDialog = true }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Announcement,
                    title = "HyperIsle",
                    subtitle = "Son dakika haberleri için overlay göster",
                    checked = uiState.hyperIsleEnabled,
                    onCheckedChange = { viewModel.setHyperIsleEnabled(it) }
                )
            }

            // Güncelleme Ayarları
            item {
                SettingsSectionHeader(title = "Güncelleme")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Update,
                    title = "Güncelleme Aralığı",
                    subtitle = "${uiState.updateIntervalMinutes} dakika",
                    onClick = { showIntervalDialog = true }
                )
            }

            // Veri Kullanımı
            item {
                SettingsSectionHeader(title = "Veri Kullanımı")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Önbellek",
                    subtitle = uiState.cacheSize,
                    onClick = { showClearCacheDialog = true }
                )
            }

            // Hakkında
            item {
                SettingsSectionHeader(title = "Hakkında")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Uygulama Versiyonu",
                    subtitle = uiState.appVersion,
                    onClick = {}
                )
            }
        }
    }

    // Tema Seçim Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Tema Seçin") },
            text = {
                Column {
                    ThemePreference.entries.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemePreference(theme)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.themePreference == theme,
                                onClick = {
                                    viewModel.setThemePreference(theme)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (theme) {
                                    ThemePreference.LIGHT -> "Açık"
                                    ThemePreference.DARK -> "Koyu"
                                    ThemePreference.SYSTEM -> "Sistem"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Güncelleme Aralığı Dialog
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            title = { Text("Güncelleme Aralığı") },
            text = {
                Column {
                    listOf(1, 5, 15, 30, 60).forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setUpdateInterval(minutes)
                                    showIntervalDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.updateIntervalMinutes == minutes,
                                onClick = {
                                    viewModel.setUpdateInterval(minutes)
                                    showIntervalDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (minutes == 1) "1 dakika (test)" else "$minutes dakika")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Önbellek Temizleme Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Önbelleği Temizle") },
            text = { Text("Tüm önbellek verileri silinecek. Devam etmek istiyor musunuz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCache()
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Temizle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
