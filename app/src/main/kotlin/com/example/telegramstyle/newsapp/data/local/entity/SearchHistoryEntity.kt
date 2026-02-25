package com.example.telegramstyle.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a search history entry stored in the local database.
 *
 * @property id Auto-generated unique identifier for the search history entry
 * @property query The search query text entered by the user
 * @property timestamp When the search was performed (epoch milliseconds)
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "query")
    val query: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
