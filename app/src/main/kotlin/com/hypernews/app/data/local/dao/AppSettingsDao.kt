package com.hypernews.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hypernews.app.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AppSettingsEntity.
 * Provides methods for accessing and manipulating application settings in the local database.
 */
@Dao
interface AppSettingsDao {

    /**
     * Retrieves the value for a specific setting key.
     *
     * @param key The setting key to look up
     * @return The value if found, null otherwise
     */
    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getValue(key: String): String?

    /**
     * Sets a value for a specific setting key.
     * If the key already exists, the value will be replaced.
     *
     * @param key The setting key
     * @param value The value to store
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(setting: AppSettingsEntity)

    /**
     * Retrieves all application settings.
     *
     * @return Flow emitting list of all settings
     */
    @Query("SELECT * FROM app_settings")
    fun getAll(): Flow<List<AppSettingsEntity>>
}
