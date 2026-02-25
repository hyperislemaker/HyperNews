package com.example.telegramstyle.newsapp.data.cache

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path.Companion.toOkioPath
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val DISK_CACHE_SIZE = 100L * 1024 * 1024 // 100 MB
        private const val MEMORY_CACHE_PERCENT = 0.25
    }
    
    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache").toOkioPath())
                    .maxSizeBytes(DISK_CACHE_SIZE)
                    .build()
            }
            .crossfade(true)
            .build()
    }
    
    fun clearCache() {
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }
    
    fun getCacheSize(): Long {
        val diskCacheDir = File(context.cacheDir, "image_cache")
        return if (diskCacheDir.exists()) {
            diskCacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } else 0L
    }
    
    fun getFormattedCacheSize(): String {
        val size = getCacheSize()
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
}
