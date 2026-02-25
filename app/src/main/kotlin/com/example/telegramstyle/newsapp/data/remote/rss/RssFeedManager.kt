package com.example.telegramstyle.newsapp.data.remote.rss

import com.example.telegramstyle.newsapp.data.local.dao.RssFeedDao
import com.example.telegramstyle.newsapp.data.mapper.toEntity
import com.example.telegramstyle.newsapp.data.mapper.toModel
import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import com.example.telegramstyle.newsapp.domain.model.RssFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssFeedManager @Inject constructor(
    private val rssFeedDao: RssFeedDao,
    private val rssParser: RssParser
) {
    fun getActiveFeeds(): Flow<List<RssFeed>> = rssFeedDao.getActiveFeeds().map { entities -> entities.map { it.toModel() } }
    
    suspend fun addFeed(name: String, url: String): Result<RssFeed> {
        if (!rssParser.isValidUrl(url)) {
            return Result.Error(AppError.ValidationError("Geçersiz RSS URL formatı"))
        }
        
        val existingFeed = rssFeedDao.getFeedByUrl(url)
        if (existingFeed != null) {
            return Result.Error(AppError.ValidationError("Bu RSS kaynağı zaten ekli"))
        }
        
        val feed = RssFeed(
            id = UUID.randomUUID().toString(),
            name = name,
            url = url,
            isActive = true,
            notificationsEnabled = true,
            lastFetchTime = null
        )
        rssFeedDao.insert(feed.toEntity())
        return Result.Success(feed)
    }
    
    suspend fun removeFeed(feedId: String): Result<Unit> {
        rssFeedDao.deleteById(feedId)
        return Result.Success(Unit)
    }
    
    suspend fun fetchFeed(feed: RssFeed): Result<List<NewsItem>> {
        val result = rssParser.parseFeed(feed.url, feed.id, feed.name)
        if (result is Result.Success) {
            rssFeedDao.updateLastFetchTime(feed.id, Instant.now().toEpochMilli())
        }
        return result
    }
    
    suspend fun fetchAllFeeds(): Result<List<NewsItem>> {
        val feeds = rssFeedDao.getAllActiveFeeds()
        val allNews = mutableListOf<NewsItem>()
        var hasError = false
        
        for (feed in feeds) {
            val result = fetchFeed(feed.toModel())
            when (result) {
                is Result.Success -> allNews.addAll(result.data)
                is Result.Error -> hasError = true
                is Result.Loading -> { /* ignore */ }
            }
        }
        
        return if (allNews.isNotEmpty() || !hasError) Result.Success(allNews)
        else Result.Error(AppError.NetworkError("Tüm RSS kaynakları çekilemedi"))
    }
    
    suspend fun validateFeedUrl(url: String): Result<Boolean> {
        if (!rssParser.isValidUrl(url)) {
            return Result.Error(AppError.ValidationError("Geçersiz URL formatı"))
        }
        val result = rssParser.parseFeed(url, "test", "Test")
        return when (result) {
            is Result.Success -> Result.Success(true)
            is Result.Error -> Result.Error(AppError.ValidationError("RSS beslemesi okunamadı"))
            is Result.Loading -> Result.Error(AppError.ValidationError("Beklenmeyen durum"))
        }
    }
}
