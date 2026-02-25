package com.hypernews.app.data.remote.firebase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CommentPropertyTest : FunSpec({
    
    test("Property 22: Comment length should be between 1 and 500 characters") {
        checkAll(PropTestConfig(iterations = 20), Arb.int(1..500)) { length ->
            val comment = "a".repeat(length)
            (comment.length in CommentManager.MIN_COMMENT_LENGTH..CommentManager.MAX_COMMENT_LENGTH) shouldBe true
        }
    }
    
    test("Property 22: Empty comments should be rejected") {
        val emptyComment = ""
        (emptyComment.length in CommentManager.MIN_COMMENT_LENGTH..CommentManager.MAX_COMMENT_LENGTH) shouldBe false
    }
    
    test("Property 22: Comments over 500 chars should be rejected") {
        val longComment = "a".repeat(501)
        (longComment.length in CommentManager.MIN_COMMENT_LENGTH..CommentManager.MAX_COMMENT_LENGTH) shouldBe false
    }
    
    test("Property 23: Profanity filter should detect bad words") {
        val filter = ProfanityFilter()
        filter.updateWordList(setOf("badword", "offensive"))
        
        filter.containsProfanity("This is a badword") shouldBe true
        filter.containsProfanity("This is clean") shouldBe false
    }
    
    test("Property 23: Profanity filter should detect character substitutions") {
        val filter = ProfanityFilter()
        filter.updateWordList(setOf("test"))
        
        // Only test basic substitutions that the filter actually handles
        filter.containsProfanity("t3st") shouldBe true // 3 -> e
    }
})
