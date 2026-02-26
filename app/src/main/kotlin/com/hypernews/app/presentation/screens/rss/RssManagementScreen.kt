package com.hypernews.app.presentation.screens.rss

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hypernews.app.domain.model.RssFeed

data class PresetRssSource(
    val name: String,
    val url: String,
    val category: String
)

val presetSources = listOf(
    // Türkiye - Genel
    PresetRssSource("NTV", "https://www.ntv.com.tr/son-dakika.rss", "Türkiye"),
    PresetRssSource("Hürriyet", "https://www.hurriyet.com.tr/rss/gundem", "Türkiye"),
    PresetRssSource("Sözcü", "https://www.sozcu.com.tr/rss/gundem.xml", "Türkiye"),
    PresetRssSource("Milliyet", "https://www.milliyet.com.tr/rss/rssNew/gundemRss.xml", "Türkiye"),
    PresetRssSource("Sabah", "https://www.sabah.com.tr/rss/gundem.xml", "Türkiye"),
    PresetRssSource("Habertürk", "https://www.haberturk.com/rss/gundem.xml", "Türkiye"),
    PresetRssSource("CNN Türk", "https://www.cnnturk.com/feed/rss/turkiye/news", "Türkiye"),
    PresetRssSource("TRT Haber", "https://www.trthaber.com/sondakika.rss", "Türkiye"),
    PresetRssSource("BBC Türkçe", "https://feeds.bbci.co.uk/turkce/rss.xml", "Türkiye"),
    PresetRssSource("DW Türkçe", "https://rss.dw.com/xml/rss-tur-all", "Türkiye"),
    
    // Dünya - İngilizce
    PresetRssSource("BBC News", "https://feeds.bbci.co.uk/news/world/rss.xml", "Dünya"),
    PresetRssSource("CNN", "http://rss.cnn.com/rss/edition_world.rss", "Dünya"),
    PresetRssSource("Reuters", "https://www.reutersagency.com/feed/", "Dünya"),
    PresetRssSource("The Guardian", "https://www.theguardian.com/world/rss", "Dünya"),
    PresetRssSource("Al Jazeera", "https://www.aljazeera.com/xml/rss/all.xml", "Dünya"),
    PresetRssSource("NPR News", "https://feeds.npr.org/1001/rss.xml", "Dünya"),
    PresetRssSource("AP News", "https://rsshub.app/apnews/topics/world-news", "Dünya"),
    PresetRssSource("France 24", "https://www.france24.com/en/rss", "Dünya"),
    
    // Ekonomi
    PresetRssSource("Bloomberg HT", "https://www.bloomberght.com/rss", "Ekonomi"),
    PresetRssSource("Dünya", "https://www.dunya.com/rss", "Ekonomi"),
    PresetRssSource("CNBC", "https://www.cnbc.com/id/100003114/device/rss/rss.html", "Ekonomi"),
    PresetRssSource("Financial Times", "https://www.ft.com/rss/home", "Ekonomi"),
    PresetRssSource("Bloomberg", "https://feeds.bloomberg.com/markets/news.rss", "Ekonomi"),
    PresetRssSource("Wall Street Journal", "https://feeds.a.dj.com/rss/RSSMarketsMain.xml", "Ekonomi"),
    
    // Spor
    PresetRssSource("NTV Spor", "https://www.ntvspor.net/son-dakika.rss", "Spor"),
    PresetRssSource("Fanatik", "https://www.fanatik.com.tr/rss/futbol", "Spor"),
    PresetRssSource("ESPN", "https://www.espn.com/espn/rss/news", "Spor"),
    PresetRssSource("Sky Sports", "https://www.skysports.com/rss/12040", "Spor"),
    PresetRssSource("BBC Sport", "https://feeds.bbci.co.uk/sport/rss.xml", "Spor"),
    
    // Teknoloji
    PresetRssSource("Webtekno", "https://www.webtekno.com/rss.xml", "Teknoloji"),
    PresetRssSource("Shiftdelete", "https://shiftdelete.net/feed", "Teknoloji"),
    PresetRssSource("Technopat", "https://www.technopat.net/feed/", "Teknoloji"),
    PresetRssSource("Donanım Haber", "https://www.donanimhaber.com/rss/tum/", "Teknoloji"),
    PresetRssSource("TechCrunch", "https://techcrunch.com/feed/", "Teknoloji"),
    PresetRssSource("The Verge", "https://www.theverge.com/rss/index.xml", "Teknoloji"),
    PresetRssSource("Ars Technica", "https://feeds.arstechnica.com/arstechnica/index", "Teknoloji"),
    PresetRssSource("Wired", "https://www.wired.com/feed/rss", "Teknoloji"),
    PresetRssSource("Engadget", "https://www.engadget.com/rss.xml", "Teknoloji"),
    
    // Bilim
    PresetRssSource("Nature", "https://www.nature.com/nature.rss", "Bilim"),
    PresetRssSource("Science Daily", "https://www.sciencedaily.com/rss/all.xml", "Bilim"),
    PresetRssSource("New Scientist", "https://www.newscientist.com/feed/home/", "Bilim"),
    PresetRssSource("NASA", "https://www.nasa.gov/rss/dyn/breaking_news.rss", "Bilim"),
    PresetRssSource("Space.com", "https://www.space.com/feeds/all", "Bilim")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: RssManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showPresetDialog by remember { mutableStateOf(false) }
    var newFeedUrl by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RSS Kaynakları") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { showPresetDialog = true }) {
                        Icon(Icons.Default.LibraryAdd, contentDescription = "Hazır Kaynaklar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Kaynak Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Aktif (${uiState.feeds.count { it.isActive }})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tümü (${uiState.feeds.size})") }
                )
            }

            val displayedFeeds = when (selectedTab) {
                0 -> uiState.feeds.filter { it.isActive }
                else -> uiState.feeds
            }

            if (displayedFeeds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RssFeed,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedTab == 0) "Aktif kaynak yok" else "Henüz kaynak eklenmemiş",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showPresetDialog = true }) {
                            Icon(Icons.Default.LibraryAdd, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Hazır Kaynaklardan Ekle")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedFeeds, key = { it.id }) { feed ->
                        RssFeedItem(
                            feed = feed,
                            onToggleActive = { viewModel.toggleActive(feed) },
                            onToggleNotification = { viewModel.toggleNotification(feed) },
                            onDelete = { viewModel.deleteFeed(feed) }
                        )
                    }
                }
            }
        }
    }

    // Manuel Kaynak Ekleme Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                newFeedUrl = ""
            },
            title = { Text("RSS Kaynağı Ekle") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newFeedUrl,
                        onValueChange = { newFeedUrl = it },
                        label = { Text("RSS URL") },
                        placeholder = { Text("https://example.com/rss") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.error != null
                    )
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addFeed(newFeedUrl) {
                            showAddDialog = false
                            newFeedUrl = ""
                        }
                    },
                    enabled = newFeedUrl.isNotBlank() && !uiState.isLoading
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        newFeedUrl = ""
                        viewModel.clearError()
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    // Hazır Kaynaklar Dialog
    if (showPresetDialog) {
        PresetSourcesDialog(
            existingUrls = uiState.feeds.map { it.url },
            onAddSource = { source ->
                viewModel.addPresetFeed(source.name, source.url)
            },
            onDismiss = { showPresetDialog = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetSourcesDialog(
    existingUrls: List<String>,
    onAddSource: (PresetRssSource) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = presetSources.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hazır Kaynaklar") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Kategori filtreleri
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val filteredSources = if (selectedCategory != null) {
                        presetSources.filter { it.category == selectedCategory }
                    } else {
                        presetSources
                    }

                    items(filteredSources) { source ->
                        val isAdded = existingUrls.contains(source.url)
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAdded) { onAddSource(source) },
                            color = if (isAdded) 
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else 
                                MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = source.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isAdded) 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        else 
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = source.category,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (isAdded) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Eklendi",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Ekle",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@Composable
private fun RssFeedItem(
    feed: RssFeed,
    onToggleActive: () -> Unit,
    onToggleNotification: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (feed.isActive) 
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aktif/Pasif Switch
            Switch(
                checked = feed.isActive,
                onCheckedChange = { onToggleActive() },
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feed.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (feed.isActive) 
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = feed.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            
            if (feed.isActive) {
                IconButton(onClick = onToggleNotification) {
                    Icon(
                        imageVector = if (feed.notificationsEnabled) 
                            Icons.Default.Notifications 
                        else 
                            Icons.Default.NotificationsOff,
                        contentDescription = "Bildirim",
                        tint = if (feed.notificationsEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Kaynağı Sil") },
            text = { Text("\"${feed.name}\" kaynağını silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}
