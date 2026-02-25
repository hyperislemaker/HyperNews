package com.hypernews.app.data.remote.rss

import com.hypernews.app.domain.common.AppError
import com.hypernews.app.domain.common.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking

class RssErrorTolerancePropertyTest : FunSpec({
    val rssParser = RssParser()
    
    test("Property 6: Invalid URLs should return validation error") {
        checkAll(PropTestConfig(iterations = 20), Arb.string(0..50)) { randomString ->
            if (!randomString.startsWith("http://") && !randomString.startsWith("https://")) {
                runBlocking {
                    val result = rssParser.parseFeed(randomString, "test", "Test")
                    result.shouldBeInstanceOf<Result.Error>()
                    (result as Result.Error).error.shouldBeInstanceOf<AppError.ValidationError>()
                }
            }
        }
    }
    
    test("Property 6: Unreachable URLs should return network error after retries") {
        runBlocking {
            val result = rssParser.parseFeed("https://nonexistent.invalid.domain.xyz/rss", "test", "Test")
            result.shouldBeInstanceOf<Result.Error>()
            (result as Result.Error).error.shouldBeInstanceOf<AppError.NetworkError>()
        }
    }
    
    test("Property 6: Empty URL should return validation error") {
        runBlocking {
            val result = rssParser.parseFeed("", "test", "Test")
            result.shouldBeInstanceOf<Result.Error>()
        }
    }
})
