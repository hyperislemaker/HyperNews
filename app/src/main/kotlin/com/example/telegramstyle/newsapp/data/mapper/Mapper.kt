package com.example.telegramstyle.newsapp.data.mapper

import com.example.telegramstyle.newsapp.data.local.entity.NewsItemEntity
import com.example.telegramstyle.newsapp.data.local.entity.RssFeedEntity
import com.example.telegramstyle.newsapp.domain.model.NewsItem
import com.example.telegramstyle.newsapp.domain.model.RssFeed
import java.time.Instant

interface Mapper<E, D> {
    fun mapToDomain(entity: E): D
    fun mapToEntity(domain: D): E
}

interface EntityToDomainMapper<E, D> {
    fun mapToDomain(entity: E): D
    fun mapToDomainList(entities: List<E>): List<D> = entities.map { mapToDomain(it) }
}

interface DomainToEntityMapper<D, E> {
    fun mapToEntity(domain: D): E
    fun mapToEntityList(domains: List<D>): List<E> = domains.map { mapToEntity(it) }
}

// RssFeed mappers
fun RssFeedEntity.toModel(): RssFeed = RssFeed(
    id = id,
    url = url,
    name = name,
    isActive = isActive,
    lastFetchTime = lastFetchTime?.let { Instant.ofEpochMilli(it) },
    notificationsEnabled = notificationsEnabled
)

fun RssFeedEntity.toDomain(): RssFeed = toModel()

fun RssFeed.toEntity(): RssFeedEntity = RssFeedEntity(
    id = id,
    url = url,
    name = name,
    isActive = isActive,
    lastFetchTime = lastFetchTime?.toEpochMilli(),
    notificationsEnabled = notificationsEnabled
)

// NewsItem mappers
fun NewsItemEntity.toModel(): NewsItem = NewsItem(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    publishedDate = Instant.ofEpochMilli(publishedDate),
    sourceUrl = sourceUrl,
    sourceName = sourceName,
    isBreakingNews = isBreakingNews,
    breakingKeywords = emptyList(),
    isFavorite = isFavorite,
    commentCount = 0,
    reactionCounts = emptyMap()
)

fun NewsItem.toEntity(): NewsItemEntity = NewsItemEntity(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    publishedDate = publishedDate.toEpochMilli(),
    sourceUrl = sourceUrl,
    sourceName = sourceName,
    isBreakingNews = isBreakingNews,
    isFavorite = isFavorite,
    createdAt = System.currentTimeMillis()
)
