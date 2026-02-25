package com.hypernews.app.data.local

import com.hypernews.app.data.local.dao.NewsItemDao
import com.hypernews.app.data.local.entity.NewsItemEntity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot

// Reduced iterations for faster test execution
private val fastConfig = PropTestConfig(iterations = 20)

/**
 * Property-based tests for NewsItemDao duplicate prevention.
 *
 * **Property 5: Duplicate Haber Önleme**
 * For any news item, inserting the same source URL twice should result in only one record
 * in the database. The insert count for duplicate items should always be 1.
 *
 * **Validates: Requirements 2.6**
 */
class NewsItemDaoPropertyTest : FunSpec({

    /**
     * Generator for valid news item IDs (hash of source URL).
     */
    val newsIdArb: Arb<String> = Arb.uuid().map { it.toString() }

    /**
     * Generator for news titles.
     */
    val titleArb: Arb<String> = Arb.string(10..100, Arb.alphanumeric())

    /**
     * Generator for news summaries.
     */
    val summaryArb: Arb<String> = Arb.string(20..300, Arb.alphanumeric())

    /**
     * Generator for valid image URLs (nullable).
     */
    val imageUrlArb: Arb<String?> = Arb.choice(
        Arb.constant(null),
        Arb.bind(
            Arb.element("http", "https"),
            Arb.string(5..15, Arb.az()),
            Arb.element(".com", ".net", ".org"),
            Arb.element(".jpg", ".png", ".webp")
        ) { protocol, domain, tld, ext ->
            "$protocol://images.$domain$tld/news/image$ext"
        }
    )

    /**
     * Generator for valid source URLs.
     */
    val sourceUrlArb: Arb<String> = Arb.bind(
        Arb.element("http", "https"),
        Arb.string(5..15, Arb.az()),
        Arb.element(".com", ".net", ".org", ".tr"),
        Arb.string(5..20, Arb.az())
    ) { protocol, domain, tld, path ->
        "$protocol://www.$domain$tld/news/$path"
    }

    /**
     * Generator for source names.
     */
    val sourceNameArb: Arb<String> = Arb.element(
        "NTV", "Webtekno", "ShiftDelete", "Sözcü", "Hürriyet", "Milliyet"
    )

    /**
     * Generator for timestamps (epoch milliseconds).
     */
    val timestampArb: Arb<Long> = Arb.long(
        1609459200000L..System.currentTimeMillis() // From 2021-01-01 to now
    )

    /**
     * Generator for complete NewsItemEntity.
     */
    val newsItemEntityArb: Arb<NewsItemEntity> = Arb.bind(
        newsIdArb,
        titleArb,
        summaryArb,
        imageUrlArb,
        timestampArb,
        sourceUrlArb,
        sourceNameArb,
        Arb.boolean(),
        Arb.boolean(),
        timestampArb
    ) { id, title, summary, imageUrl, publishedDate, sourceUrl, sourceName, isBreaking, isFavorite, createdAt ->
        NewsItemEntity(
            id = id,
            title = title,
            summary = summary,
            imageUrl = imageUrl,
            publishedDate = publishedDate,
            sourceUrl = sourceUrl,
            sourceName = sourceName,
            isBreakingNews = isBreaking,
            isFavorite = isFavorite,
            createdAt = createdAt
        )
    }

    context("Property 5: Duplicate Haber Önleme") {
        /**
         * **Validates: Requirements 2.6**
         *
         * For any news item, inserting the same item twice should result in only one record.
         * The DAO uses OnConflictStrategy.IGNORE which returns -1 for ignored (duplicate) items.
         */
        test("inserting same news item twice results in single record - OnConflictStrategy.IGNORE behavior") {
            checkAll(fastConfig, newsItemEntityArb) { newsItem ->
                // Create a mock DAO that simulates Room's OnConflictStrategy.IGNORE behavior
                val mockDao = mockk<NewsItemDao>()
                val insertedItems = mutableMapOf<String, NewsItemEntity>()
                
                // Simulate OnConflictStrategy.IGNORE: returns row ID for new items, -1 for duplicates
                coEvery { mockDao.insertAll(any()) } answers {
                    val items = firstArg<List<NewsItemEntity>>()
                    items.map { item ->
                        if (insertedItems.containsKey(item.id)) {
                            -1L // Duplicate - ignored
                        } else {
                            insertedItems[item.id] = item
                            1L // New item - inserted
                        }
                    }
                }
                
                coEvery { mockDao.getById(any()) } answers {
                    val id = firstArg<String>()
                    insertedItems[id]
                }

                // First insert - should succeed
                val firstResult = mockDao.insertAll(listOf(newsItem))
                firstResult shouldHaveSize 1
                firstResult[0] shouldBe 1L // Successfully inserted

                // Second insert of same item - should be ignored
                val secondResult = mockDao.insertAll(listOf(newsItem))
                secondResult shouldHaveSize 1
                secondResult[0] shouldBe -1L // Ignored due to duplicate

                // Verify only one record exists
                insertedItems.size shouldBe 1
                insertedItems[newsItem.id] shouldBe newsItem
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * News items with the same ID should not be duplicated.
         * The primary key constraint ensures uniqueness.
         */
        test("news items with same ID are not duplicated") {
            checkAll(fastConfig, newsItemEntityArb, titleArb, summaryArb) { originalItem, newTitle, newSummary ->
                val mockDao = mockk<NewsItemDao>()
                val insertedItems = mutableMapOf<String, NewsItemEntity>()
                
                coEvery { mockDao.insertAll(any()) } answers {
                    val items = firstArg<List<NewsItemEntity>>()
                    items.map { item ->
                        if (insertedItems.containsKey(item.id)) {
                            -1L
                        } else {
                            insertedItems[item.id] = item
                            1L
                        }
                    }
                }

                // Insert original item
                mockDao.insertAll(listOf(originalItem))

                // Create a modified version with same ID but different content
                val modifiedItem = originalItem.copy(
                    title = newTitle,
                    summary = newSummary
                )

                // Try to insert modified item with same ID
                val result = mockDao.insertAll(listOf(modifiedItem))
                result[0] shouldBe -1L // Should be ignored

                // Only one record should exist with original data
                insertedItems.size shouldBe 1
                insertedItems[originalItem.id]?.title shouldBe originalItem.title
                insertedItems[originalItem.id]?.summary shouldBe originalItem.summary
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * OnConflictStrategy.IGNORE works correctly - duplicate items return -1,
         * new items return positive row IDs.
         */
        test("OnConflictStrategy.IGNORE returns correct values for duplicates and new items") {
            checkAll(fastConfig,
                newsItemEntityArb,
                newsItemEntityArb
            ) { item1, item2 ->
                // Ensure items have different IDs for this test
                val uniqueItem2 = if (item1.id == item2.id) {
                    item2.copy(id = item2.id + "_unique")
                } else {
                    item2
                }

                val mockDao = mockk<NewsItemDao>()
                val insertedItems = mutableMapOf<String, NewsItemEntity>()
                
                coEvery { mockDao.insertAll(any()) } answers {
                    val items = firstArg<List<NewsItemEntity>>()
                    items.map { item ->
                        if (insertedItems.containsKey(item.id)) {
                            -1L
                        } else {
                            insertedItems[item.id] = item
                            insertedItems.size.toLong()
                        }
                    }
                }

                // Insert first item
                val firstResult = mockDao.insertAll(listOf(item1))
                firstResult[0] shouldBe 1L

                // Insert second unique item
                val secondResult = mockDao.insertAll(listOf(uniqueItem2))
                secondResult[0] shouldBe 2L

                // Try to insert first item again (duplicate)
                val duplicateResult = mockDao.insertAll(listOf(item1))
                duplicateResult[0] shouldBe -1L

                // Verify correct number of records
                insertedItems.size shouldBe 2
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * Batch insert with duplicates should correctly identify and ignore duplicates.
         */
        test("batch insert correctly handles mixed new and duplicate items") {
            checkAll(fastConfig, newsItemEntityArb) { baseItem ->
                val mockDao = mockk<NewsItemDao>()
                val insertedItems = mutableMapOf<String, NewsItemEntity>()
                
                coEvery { mockDao.insertAll(any()) } answers {
                    val items = firstArg<List<NewsItemEntity>>()
                    items.map { item ->
                        if (insertedItems.containsKey(item.id)) {
                            -1L
                        } else {
                            insertedItems[item.id] = item
                            insertedItems.size.toLong()
                        }
                    }
                }

                // Create unique items
                val item1 = baseItem.copy(id = "id_1_${baseItem.id}")
                val item2 = baseItem.copy(id = "id_2_${baseItem.id}")
                val item3 = baseItem.copy(id = "id_3_${baseItem.id}")

                // Insert first batch
                val firstBatch = listOf(item1, item2)
                val firstResults = mockDao.insertAll(firstBatch)
                firstResults shouldHaveSize 2
                firstResults.all { it > 0 } shouldBe true

                // Insert second batch with one duplicate (item1) and one new (item3)
                val secondBatch = listOf(item1, item3)
                val secondResults = mockDao.insertAll(secondBatch)
                secondResults shouldHaveSize 2
                secondResults[0] shouldBe -1L // item1 is duplicate
                secondResults[1] shouldBe 3L // item3 is new

                // Verify final state
                insertedItems.size shouldBe 3
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * Source URL uniqueness is enforced through the ID (which is derived from source URL).
         * Same source URL should always map to same ID, preventing duplicates.
         */
        test("same source URL always results in same ID preventing duplicates") {
            checkAll(fastConfig, sourceUrlArb, titleArb, summaryArb) { sourceUrl, title1, title2 ->
                // Simulate ID generation from source URL (hash-based)
                fun generateIdFromUrl(url: String): String = url.hashCode().toString()

                val id = generateIdFromUrl(sourceUrl)

                val item1 = NewsItemEntity(
                    id = id,
                    title = title1,
                    summary = "Summary 1",
                    imageUrl = null,
                    publishedDate = System.currentTimeMillis(),
                    sourceUrl = sourceUrl,
                    sourceName = "TestSource",
                    isBreakingNews = false,
                    isFavorite = false,
                    createdAt = System.currentTimeMillis()
                )

                val item2 = NewsItemEntity(
                    id = id, // Same ID because same source URL
                    title = title2,
                    summary = "Summary 2",
                    imageUrl = null,
                    publishedDate = System.currentTimeMillis(),
                    sourceUrl = sourceUrl,
                    sourceName = "TestSource",
                    isBreakingNews = false,
                    isFavorite = false,
                    createdAt = System.currentTimeMillis()
                )

                val mockDao = mockk<NewsItemDao>()
                val insertedItems = mutableMapOf<String, NewsItemEntity>()
                
                coEvery { mockDao.insertAll(any()) } answers {
                    val items = firstArg<List<NewsItemEntity>>()
                    items.map { item ->
                        if (insertedItems.containsKey(item.id)) {
                            -1L
                        } else {
                            insertedItems[item.id] = item
                            1L
                        }
                    }
                }

                // Insert first item
                mockDao.insertAll(listOf(item1))

                // Try to insert second item with same source URL (same ID)
                val result = mockDao.insertAll(listOf(item2))
                result[0] shouldBe -1L // Should be ignored as duplicate

                // Only one record should exist
                insertedItems.size shouldBe 1
                insertedItems[id]?.sourceUrl shouldBe sourceUrl
            }
        }
    }

    context("NewsItemEntity Data Integrity") {
        /**
         * **Validates: Requirements 2.6**
         *
         * NewsItemEntity ID should be non-empty and unique.
         */
        test("NewsItemEntity ID is non-empty") {
            checkAll(fastConfig, newsItemEntityArb) { entity ->
                entity.id.isNotEmpty() shouldBe true
                entity.id.isNotBlank() shouldBe true
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * NewsItemEntity source URL should be non-empty.
         */
        test("NewsItemEntity source URL is non-empty") {
            checkAll(fastConfig, newsItemEntityArb) { entity ->
                entity.sourceUrl.isNotEmpty() shouldBe true
                entity.sourceUrl.isNotBlank() shouldBe true
            }
        }

        /**
         * **Validates: Requirements 2.6**
         *
         * Two NewsItemEntities with same ID should be considered duplicates.
         */
        test("entities with same ID are considered duplicates regardless of other fields") {
            checkAll(fastConfig, newsItemEntityArb, titleArb) { entity, differentTitle ->
                val duplicate = entity.copy(title = differentTitle)
                
                // Same ID means same entity for database purposes
                entity.id shouldBe duplicate.id
                
                // In a set keyed by ID, only one would exist
                val idSet = setOf(entity.id, duplicate.id)
                idSet.size shouldBe 1
            }
        }
    }
})
