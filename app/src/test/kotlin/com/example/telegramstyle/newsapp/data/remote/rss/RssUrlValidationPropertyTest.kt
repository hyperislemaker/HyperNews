package com.example.telegramstyle.newsapp.data.remote.rss

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class RssUrlValidationPropertyTest : FunSpec({
    val rssParser = RssParser()
    
    test("Property 2: Valid HTTP/HTTPS URLs should be accepted") {
        val validUrls = listOf(
            "https://www.ntv.com.tr/rss",
            "http://example.com/feed",
            "https://webtekno.com/rss.xml",
            "https://sub.domain.co.uk/path/feed"
        )
        validUrls.forEach { url ->
            rssParser.isValidUrl(url) shouldBe true
        }
    }
    
    test("Property 2: Invalid URLs should be rejected") {
        checkAll(PropTestConfig(iterations = 20), Arb.string(0..50)) { randomString ->
            if (!randomString.startsWith("http://") && !randomString.startsWith("https://")) {
                rssParser.isValidUrl(randomString) shouldBe false
            }
        }
    }
    
    test("Property 2: URLs without proper domain should be rejected") {
        val invalidUrls = listOf(
            "https://",
            "http://localhost",
            "ftp://example.com/feed",
            "javascript:alert(1)",
            "file:///etc/passwd",
            ""
        )
        invalidUrls.forEach { url ->
            rssParser.isValidUrl(url) shouldBe false
        }
    }
})
