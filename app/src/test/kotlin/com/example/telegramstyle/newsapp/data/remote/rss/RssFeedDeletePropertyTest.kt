package com.example.telegramstyle.newsapp.data.remote.rss

import com.example.telegramstyle.newsapp.data.local.dao.RssFeedDao
import com.example.telegramstyle.newsapp.data.local.entity.RssFeedEntity
import com.example.telegramstyle.newsapp.domain.common.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class RssFeedDeletePropertyTest : FunSpec({
    
    test("Property 3: Deleting a feed should call deleteById with correct ID") {
        checkAll(PropTestConfig(iterations = 20), Arb.uuid()) { uuid ->
            val feedId = uuid.toString()
            val mockDao = mockk<RssFeedDao>(relaxed = true)
            val mockParser = mockk<RssParser>()
            
            coEvery { mockDao.getActiveFeeds() } returns flowOf(emptyList())
            
            val manager = RssFeedManager(mockDao, mockParser)
            val result = manager.removeFeed(feedId)
            
            result.shouldBeInstanceOf<Result.Success<Unit>>()
            coVerify { mockDao.deleteById(feedId) }
        }
    }
    
    test("Property 3: Deleting non-existent feed should still succeed") {
        val mockDao = mockk<RssFeedDao>(relaxed = true)
        val mockParser = mockk<RssParser>()
        
        coEvery { mockDao.getActiveFeeds() } returns flowOf(emptyList())
        coEvery { mockDao.getById(any()) } returns null
        
        val manager = RssFeedManager(mockDao, mockParser)
        val result = manager.removeFeed("non-existent-id")
        
        result.shouldBeInstanceOf<Result.Success<Unit>>()
    }
})
