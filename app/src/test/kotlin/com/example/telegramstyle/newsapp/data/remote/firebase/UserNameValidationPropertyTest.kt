package com.example.telegramstyle.newsapp.data.remote.firebase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.mockk

class UserNameValidationPropertyTest : FunSpec({
    val userProfileManager = UserProfileManager(mockk(relaxed = true))
    
    test("Property 17: Valid usernames should be accepted") {
        val validUsernames = listOf("user123", "John_Doe", "abc", "abcdefghijklmnopqrst")
        validUsernames.forEach { username ->
            userProfileManager.isValidUserName(username) shouldBe true
        }
    }
    
    test("Property 17: Usernames starting with number should be rejected") {
        val invalidUsernames = listOf("1user", "123abc", "9test")
        invalidUsernames.forEach { username ->
            userProfileManager.isValidUserName(username) shouldBe false
        }
    }
    
    test("Property 17: Usernames shorter than 3 chars should be rejected") {
        val shortUsernames = listOf("a", "ab", "A1")
        shortUsernames.forEach { username ->
            userProfileManager.isValidUserName(username) shouldBe false
        }
    }
    
    test("Property 17: Usernames longer than 20 chars should be rejected") {
        val longUsername = "a".repeat(21)
        userProfileManager.isValidUserName(longUsername) shouldBe false
    }
    
    test("Property 17: Usernames with special chars should be rejected") {
        val invalidUsernames = listOf("user@name", "user-name", "user.name", "user name", "user#123")
        invalidUsernames.forEach { username ->
            userProfileManager.isValidUserName(username) shouldBe false
        }
    }
    
    test("Property 18: Username suggestions should be valid") {
        checkAll(PropTestConfig(iterations = 20), Arb.string(3..10)) { baseName ->
            val suggestions = userProfileManager.suggestUserNames(baseName)
            suggestions.forEach { suggestion ->
                if (suggestion.isNotEmpty()) {
                    userProfileManager.isValidUserName(suggestion) shouldBe true
                }
            }
        }
    }
})
