package com.hypernews.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hypernews.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SearchHistoryEntity.
 * Provides methods for accessing and manipulating search history in the local database.
 */
@Dao
interface SearchHistoryDao {

    /**
     * Retrieves search history entries ordered by timestamp descending (most recent first).
     *
     * @param limit Maximum number of entries to return
     * @return Flow emitting list of search history entries
     */
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getHistory(limit: Int): Flow<List<SearchHistoryEntity>>

    /**
     * Inserts a new search history entry.
     *
     * @param entry The search history entry to insert
     */
    @Insert
    suspend fun insert(entry: SearchHistoryEntity)

    /**
     * Clears all search history entries from the database.
     */
    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
