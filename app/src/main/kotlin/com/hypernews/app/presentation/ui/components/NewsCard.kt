package com.hypernews.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hypernews.app.domain.model.NewsItem
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sol taraf - Görsel
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (newsItem.imageUrl != null) {
                    AsyncImage(
                        model = newsItem.imageUrl,
                        contentDescription = newsItem.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
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
                            text = newsItem.sourceName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Son dakika etiketi
                if (newsItem.isBreakingNews) {
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(3.dp),
                        color = Color(0xFFEF5350)
                    ) {
                        Text(
                            text = "🔴",
                            modifier = Modifier.padding(2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            // Sağ taraf - İçerik
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Başlık
                    Text(
                        text = newsItem.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = MaterialTheme.typography.titleSmall.lineHeight
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Özet
                    Text(
                        text = newsItem.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Alt bilgi satırı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Kaynak
                        Text(
                            text = newsItem.sourceName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Tarih
                        Text(
                            text = formatDate(newsItem.publishedDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Favori butonu
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (newsItem.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (newsItem.isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                            tint = if (newsItem.isFavorite) Color(0xFFEF5350) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(instant: Instant): String {
    val now = Instant.now()
    val diff = now.epochSecond - instant.epochSecond
    
    return when {
        diff < 60 -> "Az önce"
        diff < 3600 -> "${diff / 60}dk"
        diff < 86400 -> "${diff / 3600}sa"
        diff < 172800 -> "Dün"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd MMM")
            instant.atZone(ZoneId.systemDefault()).format(formatter)
        }
    }
}
