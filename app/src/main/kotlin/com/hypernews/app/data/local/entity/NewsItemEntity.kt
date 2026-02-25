package com.hypernews.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a news item stored in the local database.
 *
 * @property id Unique identifier for the news item (typically from source URL hash)
 * @property title The headline/title of the news
 * @property summary Brief summary or description of the news
 * @property imageUrl URL of the news image (nullable if no image available)
 * @property publishedDate When the news was published (epoch milliseconds)
 * @property sourceUrl Original URL of the news article
 * @property sourceName Name of the news source (e.g., NTV, Webtekno)
 * @property isBreakingNews Whether this is a breaking news item
 * @property isFavorite Whether the user has marked this as favorite
 * @property createdAt When this entity was created in the database (epoch milliseconds)
 */
@Entity(tableName = "news_items")
data class NewsItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "published_date")
    val publishedDate: Long,

    @ColumnInfo(name = "source_url")
    val sourceUrl: String,

    @ColumnInfo(name = "source_name")
    val sourceName: String,

    @ColumnInfo(name = "is_breaking_news")
    val isBreakingNews: Boolean,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
