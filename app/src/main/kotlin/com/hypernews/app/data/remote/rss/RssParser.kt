package com.hypernews.app.data.remote.rss

import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import com.hypernews.app.domain.model.NewsItem
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssParser @Inject constructor() {
    
    companion object {
        private const val TIMEOUT_MS = 10_000
        private const val MAX_RETRIES = 3
        private val URL_PATTERN = Regex("^https?://[\\w.-]+(?:\\.[a-z]{2,})+(?:/[\\w./-]*)?$", RegexOption.IGNORE_CASE)
    }
    
    suspend fun parseFeed(feedUrl: String, feedId: String, feedName: String): Result<List<NewsItem>> = withContext(Dispatchers.IO) {
        if (!isValidUrl(feedUrl)) {
            return@withContext Result.Error(AppError.ValidationError("Geçersiz RSS URL formatı"))
        }
        
        var lastError: Exception? = null
        repeat(MAX_RETRIES) { attempt ->
            try {
                val connection = URL(feedUrl).openConnection().apply {
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("User-Agent", "TelegramStyleNewsApp/1.0")
                }
                
                val feed = SyndFeedInput().build(XmlReader(connection))
                val newsItems = feed.entries.mapNotNull { entry ->
                    try {
                        val pubDate = entry.publishedDate?.toInstant() ?: Instant.now()
                        NewsItem(
                            id = UUID.nameUUIDFromBytes("${feedId}_${entry.link}".toByteArray()).toString(),
                            title = entry.title?.trim() ?: return@mapNotNull null,
                            summary = extractSummary(entry.description?.value ?: entry.contents.firstOrNull()?.value ?: ""),
                            imageUrl = extractImageUrl(entry),
                            sourceUrl = entry.link ?: return@mapNotNull null,
                            sourceName = feedName,
                            publishedDate = pubDate,
                            isBreakingNews = false,
                            breakingKeywords = emptyList(),
                            isFavorite = false,
                            commentCount = 0,
                            reactionCounts = emptyMap()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                return@withContext Result.Success(newsItems)
            } catch (e: Exception) {
                lastError = e
            }
        }
        Result.Error(AppError.NetworkError("RSS çekme başarısız: ${lastError?.message}"))
    }
    
    fun isValidUrl(url: String): Boolean = URL_PATTERN.matches(url) && (url.startsWith("http://") || url.startsWith("https://"))
    
    private fun extractSummary(content: String): String {
        val cleaned = content.replace(Regex("<[^>]*>"), "")
            .replace(Regex("&[a-zA-Z]+;"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return if (cleaned.length > 300) cleaned.take(297) + "..." else cleaned
    }
    
    private fun extractImageUrl(entry: com.rometools.rome.feed.synd.SyndEntry): String? {
        // 1. Enclosure'dan görsel al
        entry.enclosures.firstOrNull { it.type?.startsWith("image") == true }?.let { return it.url }
        
        // 2. Media namespace'den al (media:content, media:thumbnail)
        entry.foreignMarkup.forEach { element ->
            when (element.name) {
                "thumbnail", "content" -> {
                    element.getAttributeValue("url")?.let { return it }
                }
                "group" -> {
                    element.children.forEach { child ->
                        if (child.name == "content" || child.name == "thumbnail") {
                            (child as? org.jdom2.Element)?.getAttributeValue("url")?.let { return it }
                        }
                    }
                }
            }
        }
        
        // 3. Description içindeki img tag'inden al
        val imgRegex = Regex("<img[^>]+src=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE)
        entry.description?.value?.let { desc ->
            imgRegex.find(desc)?.groupValues?.get(1)?.let { url ->
                if (url.startsWith("http")) return url
            }
        }
        
        // 4. Content içindeki img tag'inden al
        entry.contents.forEach { content ->
            content.value?.let { value ->
                imgRegex.find(value)?.groupValues?.get(1)?.let { url ->
                    if (url.startsWith("http")) return url
                }
            }
        }
        
        // 5. Link'ten Open Graph image çekmeye çalışma (opsiyonel, performans için kapalı)
        return null
    }
}
