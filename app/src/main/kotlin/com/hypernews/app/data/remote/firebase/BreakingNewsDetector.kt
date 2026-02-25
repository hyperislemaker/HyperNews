package com.hypernews.app.data.remote.firebase

import com.hypernews.app.domain.model.NewsItem
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreakingNewsDetector @Inject constructor() {
    companion object {
        private val DEFAULT_KEYWORDS = setOf(
            "son dakika", "breaking", "acil", "flaş", "haber", "önemli gelişme",
            "son gelişme", "canlı", "live", "urgent"
        )
        private val BREAKING_WINDOW = Duration.ofMinutes(30)
    }
    
    private val customKeywords = mutableSetOf<String>()
    
    fun isBreakingNews(newsItem: NewsItem): Boolean {
        val allKeywords = DEFAULT_KEYWORDS + customKeywords
        val titleLower = newsItem.title.lowercase()
        val summaryLower = newsItem.summary.lowercase()
        
        val hasKeyword = allKeywords.any { keyword ->
            titleLower.contains(keyword) || summaryLower.contains(keyword)
        }
        
        val isRecent = Duration.between(newsItem.publishedDate, Instant.now()) <= BREAKING_WINDOW
        
        return hasKeyword && isRecent
    }
    
    fun detectBreakingKeywords(newsItem: NewsItem): List<String> {
        val allKeywords = DEFAULT_KEYWORDS + customKeywords
        val titleLower = newsItem.title.lowercase()
        val summaryLower = newsItem.summary.lowercase()
        
        return allKeywords.filter { keyword ->
            titleLower.contains(keyword) || summaryLower.contains(keyword)
        }
    }
    
    fun addCustomKeyword(keyword: String) {
        customKeywords.add(keyword.lowercase())
    }
    
    fun removeCustomKeyword(keyword: String) {
        customKeywords.remove(keyword.lowercase())
    }
    
    fun setCustomKeywords(keywords: Set<String>) {
        customKeywords.clear()
        customKeywords.addAll(keywords.map { it.lowercase() })
    }
    
    fun getCustomKeywords(): Set<String> = customKeywords.toSet()
}
