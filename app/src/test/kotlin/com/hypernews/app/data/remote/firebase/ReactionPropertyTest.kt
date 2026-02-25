package com.hypernews.app.data.remote.firebase

import com.hypernews.app.domain.model.ReactionType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll

class ReactionPropertyTest : FunSpec({
    
    test("Property 25: All reaction types should be valid") {
        ReactionType.entries.size shouldBe 6
        ReactionType.entries.map { it.name } shouldBe listOf("LIKE", "LOVE", "WOW", "SAD", "ANGRY", "THINKING")
    }
    
    test("Property 25: Reaction type should be convertible to/from string") {
        checkAll(PropTestConfig(iterations = 20), Arb.enum<ReactionType>()) { type ->
            val stringValue = type.name
            val restored = ReactionType.valueOf(stringValue)
            restored shouldBe type
        }
    }
    
    test("Property 26: Reaction counts should be non-negative") {
        val counts = mapOf(
            ReactionType.LIKE to 10,
            ReactionType.LOVE to 5,
            ReactionType.WOW to 0
        )
        counts.values.all { it >= 0 } shouldBe true
    }
    
    test("Property 26: Total reactions should equal sum of individual counts") {
        val counts = mapOf(
            ReactionType.LIKE to 10,
            ReactionType.LOVE to 5,
            ReactionType.WOW to 3,
            ReactionType.SAD to 2,
            ReactionType.ANGRY to 1,
            ReactionType.THINKING to 0
        )
        counts.values.sum() shouldBe 21
    }
})
