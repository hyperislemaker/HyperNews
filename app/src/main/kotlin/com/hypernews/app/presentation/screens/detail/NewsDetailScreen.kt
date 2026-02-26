package com.hypernews.app.presentation.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hypernews.app.presentation.ui.components.CommentSection
import com.hypernews.app.presentation.ui.components.ReactionBottomSheet
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    onNavigateBack: () -> Unit,
    viewModel: NewsDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showReactionSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.newsItem?.sourceName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        uiState.newsItem?.let { news ->
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${news.title}\n${news.sourceUrl}")
                            }
                            context.startActivity(Intent.createChooser(intent, "Paylaş"))
                        }
                    }) {
                        Icon(Icons.Default.Share, "Paylaş")
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (uiState.newsItem?.isFavorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            "Favori",
                            tint = if (uiState.newsItem?.isFavorite == true) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "Hata")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    uiState.newsItem?.let { news ->
                        // Görsel alanı - her zaman göster
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        ) {
                            if (news.imageUrl != null) {
                                AsyncImage(
                                    model = news.imageUrl,
                                    contentDescription = news.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Placeholder gradient
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                    MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = news.sourceName.take(2).uppercase(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (news.isBreakingNews) {
                                Surface(
                                    color = Color(0xFFEF5350),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        "🔴 SON DAKİKA",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            Text(news.title, style = MaterialTheme.typography.headlineSmall)
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    news.sourceName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    " • ${news.publishedDate.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Text(news.summary, style = MaterialTheme.typography.bodyLarge)
                            
                            Spacer(Modifier.height(16.dp))
                            
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.sourceUrl)))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.OpenInBrowser, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Kaynağa Git")
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            // Reactions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    uiState.reactionCounts.filter { it.value > 0 }.forEach { (type, count) ->
                                        val emoji = when (type) {
                                            com.hypernews.app.domain.model.ReactionType.LIKE -> "👍"
                                            com.hypernews.app.domain.model.ReactionType.LOVE -> "❤️"
                                            com.hypernews.app.domain.model.ReactionType.WOW -> "😮"
                                            com.hypernews.app.domain.model.ReactionType.SAD -> "😢"
                                            com.hypernews.app.domain.model.ReactionType.ANGRY -> "😠"
                                            com.hypernews.app.domain.model.ReactionType.THINKING -> "🤔"
                                        }
                                        Text("$emoji $count ", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                TextButton(onClick = { showReactionSheet = true }) {
                                    Text(if (uiState.userReaction != null) "Tepkini Değiştir" else "Tepki Ver")
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            // Comments
                            CommentSection(
                                comments = uiState.comments,
                                currentUserId = uiState.currentUserId,
                                isLoading = uiState.isCommentsLoading,
                                onAddComment = { viewModel.addComment(it) },
                                onEditComment = { id, content -> viewModel.editComment(id, content) },
                                onDeleteComment = { viewModel.deleteComment(it) },
                                onReportComment = { viewModel.reportComment(it) }
                            )
                        }
                    }
                }
            }
        }
        
        ReactionBottomSheet(
            isVisible = showReactionSheet,
            selectedReaction = uiState.userReaction,
            reactionCounts = uiState.reactionCounts,
            onReactionSelected = { viewModel.addReaction(it); showReactionSheet = false },
            onDismiss = { showReactionSheet = false }
        )
    }
}
