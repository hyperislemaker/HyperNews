package com.example.telegramstyle.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing application settings stored as key-value pairs.
 *
 * This entity is used to store various application settings such as:
 * - Theme preference (dark/light/system)
 * - HyperIsle enabled state
 * - Background sync interval
 * - Onboarding completion status
 * - Other user preferences
 *
 * @property key The unique key identifying the setting
 * @property value The value of the setting stored as a string
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "value")
    val value: String
)
