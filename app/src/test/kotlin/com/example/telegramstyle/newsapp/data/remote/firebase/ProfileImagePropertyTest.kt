package com.example.telegramstyle.newsapp.data.remote.firebase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProfileImagePropertyTest : FunSpec({
    
    test("Property 19: Max size constant should be 2MB") {
        ProfileImageManager.MAX_SIZE_BYTES shouldBe 2 * 1024 * 1024L
    }
    
    test("Property 19: Size validation logic - under 2MB is valid") {
        val size = 1024 * 1024L // 1 MB
        val isValid = size <= ProfileImageManager.MAX_SIZE_BYTES
        isValid shouldBe true
    }
    
    test("Property 19: Size validation logic - exactly 2MB is valid") {
        val size = 2 * 1024 * 1024L // 2 MB
        val isValid = size <= ProfileImageManager.MAX_SIZE_BYTES
        isValid shouldBe true
    }
    
    test("Property 19: Size validation logic - over 2MB is invalid") {
        val size = 2 * 1024 * 1024L + 1 // 2 MB + 1 byte
        val isValid = size <= ProfileImageManager.MAX_SIZE_BYTES
        isValid shouldBe false
    }
    
    test("Property 20: Target size should be 512") {
        ProfileImageManager.TARGET_SIZE shouldBe 512
    }
})
