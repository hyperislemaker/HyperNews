package com.hypernews.app.data.remote.rss

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class RssParsingPropertyTest : FunSpec({
    val summaryGenerator = SummaryGenerator()
    
    test("Property 4: Summary should never exceed 300 characters") {
        checkAll(PropTestConfig(iterations = 20), Arb.string(0..1000)) { content ->
            val summary = summaryGenerator.generateSummary(content)
            summary.length shouldBe (summary.length.coerceAtMost(303)) // 300 + "..."
        }
    }
    
    test("Property 4: HTML tags should be stripped from content") {
        val htmlContent = "<p>Hello <strong>World</strong></p><script>alert('xss')</script>"
        val cleaned = summaryGenerator.cleanHtml(htmlContent)
        cleaned.contains("<") shouldBe false
        cleaned.contains(">") shouldBe false
        cleaned.contains("script") shouldBe false
    }
    
    test("Property 4: HTML entities should be decoded") {
        val htmlEntities = "&amp; &lt; &gt; &quot; &nbsp;"
        val cleaned = summaryGenerator.cleanHtml(htmlEntities)
        cleaned.contains("&amp;") shouldBe false
        cleaned.contains("&lt;") shouldBe false
    }
    
    test("Property 4: Multiple whitespaces should be normalized") {
        checkAll(PropTestConfig(iterations = 20), Arb.string(0..100)) { content ->
            val cleaned = summaryGenerator.cleanHtml("  $content   with   spaces  ")
            cleaned.contains("  ") shouldBe false
        }
    }
})
