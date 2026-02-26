package com.hypernews.app.presentation.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

data class DefaultRssFeed(
    val name: String,
    val url: String,
    val category: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationPermission(isGranted)
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> RssFeedSelectionPage(
                    feeds = uiState.defaultFeeds,
                    selectedFeeds = uiState.selectedFeeds,
                    onToggleFeed = { viewModel.toggleFeed(it) }
                )
                2 -> NotificationPermissionPage(
                    hasPermission = uiState.hasNotificationPermission,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setNotificationPermission(true)
                        }
                    }
                )
            }
        }

        // Page Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val color = if (pagerState.currentPage == index)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
                
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = color
                ) {}
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text("Geri")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (pagerState.currentPage < 2) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text("İleri")
                }
            } else {
                Button(
                    onClick = { viewModel.completeOnboarding() },
                    enabled = uiState.selectedFeeds.isNotEmpty() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Haberler yükleniyor...")
                    } else {
                        Text("Başla")
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Newspaper,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "HyperNews'e\nHoş Geldiniz",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Dünya ve Türkiye'den haberleri takip edin,\nyorum yapın ve tepki verin.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun RssFeedSelectionPage(
    feeds: List<DefaultRssFeed>,
    selectedFeeds: Set<String>,
    onToggleFeed: (DefaultRssFeed) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = feeds.map { it.category }.distinct()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Haber Kaynaklarını Seçin",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "En az bir kaynak seçin. Daha sonra ayarlardan değiştirebilirsiniz.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Kategori filtreleri
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("Tümü", style = MaterialTheme.typography.labelSmall) }
            )
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
        
        // Seçilen kaynak sayısı
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "${selectedFeeds.size} kaynak seçildi",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filteredFeeds = if (selectedCategory != null) {
                feeds.filter { it.category == selectedCategory }
            } else {
                feeds
            }
            
            val groupedFeeds = filteredFeeds.groupBy { it.category }
            
            groupedFeeds.forEach { (category, categoryFeeds) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                items(categoryFeeds) { feed ->
                    FeedSelectionItem(
                        feed = feed,
                        isSelected = selectedFeeds.contains(feed.url),
                        onToggle = { onToggleFeed(feed) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedSelectionItem(
    feed: DefaultRssFeed,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = isSelected,
                onValueChange = { onToggle() },
                role = Role.Checkbox
            ),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = feed.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionPage(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasPermission) Icons.Default.NotificationsActive else Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (hasPermission)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bildirimleri Etkinleştir",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Son dakika haberleri ve güncellemeler için bildirimlere izin verin.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (hasPermission) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bildirimler etkin",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Button(onClick = onRequestPermission) {
                Text("İzin Ver")
            }
        }
    }
}
