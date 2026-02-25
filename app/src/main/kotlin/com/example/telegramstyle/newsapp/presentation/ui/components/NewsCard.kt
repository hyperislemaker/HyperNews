package com.example.telegramstyle.newsapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            Box {
                newsItem.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = newsItem.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (newsItem.isBreakingNews) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEF5350)
                    ) {
                        Text(
                            text = "🔴 SON DAKİKA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = newsItem.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = newsItem.sourceName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " • ${formatDate(newsItem.publishedDate)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (newsItem.commentCount > 0) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Yorumlar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = " ${newsItem.commentCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (newsItem.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (newsItem.isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                                tint = if (newsItem.isFavorite) Color(0xFFEF5350) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (newsItem.reactionCounts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ReactionSummary(reactionCounts = newsItem.reactionCounts)
                }
            }
        }
    }
}

@Composable
private fun ReactionSummary(reactionCounts: Map<com.example.telegramstyle.newsapp.domain.model.ReactionType, Int>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        reactionCounts.filter { it.value > 0 }.forEach { (type, count) ->
            val emoji = when (type) {
                com.example.telegramstyle.newsapp.domain.model.ReactionType.LIKE -> "👍"
                com.example.telegramstyle.newsapp.domain.model.ReactionType.LOVE -> "❤️"
                com.example.telegramstyle.newsapp.domain.model.ReactionType.WOW -> "😮"
                com.example.telegramstyle.newsapp.domain.model.ReactionType.SAD -> "😢"
                com.example.telegramstyle.newsapp.domain.model.ReactionType.ANGRY -> "😠"
                com.example.telegramstyle.newsapp.domain.model.ReactionType.THINKING -> "🤔"
            }
            Text(
                text = "$emoji $count",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDate(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")
    return instant.atZone(ZoneId.systemDefault()).format(formatter)
}
