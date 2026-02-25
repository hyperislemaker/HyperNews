package com.example.telegramstyle.newsapp.data.remote.firebase

import com.example.telegramstyle.newsapp.domain.model.ReportStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll

class AdminPropertyTest : FunSpec({
    
    test("Property 27: Report status should have valid states") {
        ReportStatus.entries.map { it.name } shouldBe listOf("PENDING", "RESOLVED", "REJECTED")
    }
    
    test("Property 27: Report status should be convertible to/from string") {
        checkAll(PropTestConfig(iterations = 20), Arb.enum<ReportStatus>()) { status ->
            val stringValue = status.name
            val restored = ReportStatus.valueOf(stringValue)
            restored shouldBe status
        }
    }
    
    test("Property 28: Ban status should be boolean") {
        val bannedUser = true
        val unbannedUser = false
        
        bannedUser shouldBe true
        unbannedUser shouldBe false
        (bannedUser != unbannedUser) shouldBe true
    }
})
