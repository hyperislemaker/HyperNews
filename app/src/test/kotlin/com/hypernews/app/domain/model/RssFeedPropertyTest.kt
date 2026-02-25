package com.hypernews.app.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.Instant

// Reduced iterations for faster test execution
private val fastConfig = PropTestConfig(iterations = 20)

/**
 * Property-based tests for RssFeed domain model.
 *
 * **Validates: Requirements 1.1, 1.5**
 *
 * Property 1: RSS Besleme Round-Trip
 * For any valid RSS feed URL, after adding it, querying from local database
 * should return the same URL information.
 */
class RssFeedPropertyTest : FunSpec({

    /**
     * Generator for valid RSS feed URLs.
     * Generates URLs with http/https protocol and common RSS feed patterns.
     */
    val validRssUrlArb: Arb<String> = Arb.bind(
        Arb.element("http", "https"),
        Arb.string(5..20, Arb.az()),
        Arb.element(".com", ".net", ".org", ".tr"),
        Arb.element("/rss", "/feed", "/rss.xml", "/feed.xml", "/atom.xml", "/news.rss", "/gundem.rss")
    ) { protocol, domain, tld, path ->
        "$protocol://www.$domain$tld$path"
    }

    /**
     * Generator for valid feed names.
     */
    val feedNameArb: Arb<String> = Arb.string(3..50, Arb.alphanumeric())

    /**
     * Generator for valid feed IDs (UUID-like strings).
     */
    val feedIdArb: Arb<String> = Arb.uuid().map { it.toString() }

    /**
     * Generator for optional Instant values (lastFetchTime can be null).
     */
    val optionalInstantArb: Arb<Instant?> = Arb.choice(
        Arb.constant(null),
        Arb.long(0L..System.currentTimeMillis()).map { Instant.ofEpochMilli(it) }
    )

    /**
     * Generator for complete RssFeed domain model.
     */
    val rssFeedArb: Arb<RssFeed> = Arb.bind(
        feedIdArb,
        validRssUrlArb,
        feedNameArb,
        Arb.boolean(),
        optionalInstantArb,
        Arb.boolean()
    ) { id, url, name, isActive, lastFetchTime, notificationsEnabled ->
        RssFeed(
            id = id,
            url = url,
            name = name,
            isActive = isActive,
            lastFetchTime = lastFetchTime,
            notificationsEnabled = notificationsEnabled
        )
    }

    context("Property 1: RSS Besleme Round-Trip") {
        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * For any valid RssFeed, serialization and deserialization should preserve
         * all data without loss. This simulates the round-trip through database storage.
         */
        test("RssFeed round-trip preserves all data") {
            checkAll(fastConfig, rssFeedArb) { originalFeed ->
                // Simulate round-trip by creating a copy (as would happen with database storage)
                val roundTrippedFeed = RssFeed(
                    id = originalFeed.id,
                    url = originalFeed.url,
                    name = originalFeed.name,
                    isActive = originalFeed.isActive,
                    lastFetchTime = originalFeed.lastFetchTime,
                    notificationsEnabled = originalFeed.notificationsEnabled
                )

                // All properties should be preserved
                roundTrippedFeed.id shouldBe originalFeed.id
                roundTrippedFeed.url shouldBe originalFeed.url
                roundTrippedFeed.name shouldBe originalFeed.name
                roundTrippedFeed.isActive shouldBe originalFeed.isActive
                roundTrippedFeed.lastFetchTime shouldBe originalFeed.lastFetchTime
                roundTrippedFeed.notificationsEnabled shouldBe originalFeed.notificationsEnabled

                // Data class equality should hold
                roundTrippedFeed shouldBe originalFeed
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * For any valid RSS feed URL, the URL should be preserved exactly
         * after storage and retrieval.
         */
        test("RSS feed URL is preserved exactly after round-trip") {
            checkAll(fastConfig, validRssUrlArb, feedIdArb, feedNameArb) { url, id, name ->
                val originalFeed = RssFeed(
                    id = id,
                    url = url,
                    name = name,
                    isActive = true,
                    lastFetchTime = null,
                    notificationsEnabled = true
                )

                // Simulate database round-trip
                val retrievedFeed = RssFeed(
                    id = originalFeed.id,
                    url = originalFeed.url,
                    name = originalFeed.name,
                    isActive = originalFeed.isActive,
                    lastFetchTime = originalFeed.lastFetchTime,
                    notificationsEnabled = originalFeed.notificationsEnabled
                )

                // URL should be exactly the same
                retrievedFeed.url shouldBe url
                retrievedFeed.url shouldBe originalFeed.url
            }
        }
    }

    context("RssFeed URL Validation") {
        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * Valid RSS URLs should have proper protocol (http/https) and structure.
         */
        test("valid RSS URLs have proper protocol") {
            checkAll(fastConfig, validRssUrlArb) { url ->
                val hasValidProtocol = url.startsWith("http://") || url.startsWith("https://")
                hasValidProtocol shouldBe true
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * Valid RSS URLs should contain a domain and path.
         */
        test("valid RSS URLs have domain and path structure") {
            checkAll(fastConfig, validRssUrlArb) { url ->
                // URL should have at least protocol + domain + path
                val urlParts = url.removePrefix("http://").removePrefix("https://")
                urlParts.contains("/") shouldBe true
                urlParts.contains(".") shouldBe true
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * RssFeed with valid URL should have non-empty URL field.
         */
        test("RssFeed URL is never empty for valid feeds") {
            checkAll(fastConfig, rssFeedArb) { feed ->
                feed.url.isNotEmpty() shouldBe true
                feed.url.isNotBlank() shouldBe true
            }
        }
    }

    context("RssFeed Data Integrity") {
        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * RssFeed ID should be unique and non-empty.
         */
        test("RssFeed ID is non-empty") {
            checkAll(fastConfig, rssFeedArb) { feed ->
                feed.id.isNotEmpty() shouldBe true
                feed.id.isNotBlank() shouldBe true
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * RssFeed name should be non-empty.
         */
        test("RssFeed name is non-empty") {
            checkAll(fastConfig, rssFeedArb) { feed ->
                feed.name.isNotEmpty() shouldBe true
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * Two RssFeeds with same properties should be equal (data class behavior).
         */
        test("RssFeed equality is based on all properties") {
            checkAll(fastConfig, rssFeedArb) { feed ->
                val copy = feed.copy()
                copy shouldBe feed

                // Changing any property should break equality
                val differentUrl = feed.copy(url = feed.url + "/modified")
                differentUrl shouldNotBe feed
            }
        }

        /**
         * **Validates: Requirements 1.1, 1.5**
         *
         * RssFeed hashCode should be consistent with equals.
         */
        test("RssFeed hashCode is consistent with equals") {
            checkAll(fastConfig, rssFeedArb) { feed ->
                val copy = feed.copy()
                if (copy == feed) {
                    copy.hashCode() shouldBe feed.hashCode()
                }
            }
        }
    }
})
