package com.example.telegramstyle.newsapp.data.remote.rss

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryGenerator @Inject constructor() {
    
    companion object {
        private const val MAX_SUMMARY_LENGTH = 300
    }
    
    fun generateSummary(content: String): String {
        val cleaned = cleanHtml(content)
        if (cleaned.length <= MAX_SUMMARY_LENGTH) return cleaned
        
        val sentences = cleaned.split(Regex("[.!?]+")).filter { it.isNotBlank() }
        if (sentences.isEmpty()) return cleaned.take(MAX_SUMMARY_LENGTH - 3) + "..."
        
        val summary = StringBuilder()
        for (sentence in sentences) {
            val trimmed = sentence.trim()
            if (summary.length + trimmed.length + 2 <= MAX_SUMMARY_LENGTH) {
                if (summary.isNotEmpty()) summary.append(". ")
                summary.append(trimmed)
            } else break
        }
        
        return if (summary.isEmpty()) cleaned.take(MAX_SUMMARY_LENGTH - 3) + "..."
        else if (summary.length < cleaned.length) summary.append("...").toString()
        else summary.toString()
    }
    
    fun cleanHtml(html: String): String = html
        .replace(Regex("<script[^>]*>[\\s\\S]*?</script>"), "")
        .replace(Regex("<style[^>]*>[\\s\\S]*?</style>"), "")
        .replace(Regex("<[^>]*>"), "")
        .replace(Regex("&nbsp;"), " ")
        .replace(Regex("&amp;"), "&")
        .replace(Regex("&lt;"), "<")
        .replace(Regex("&gt;"), ">")
        .replace(Regex("&quot;"), "\"")
        .replace(Regex("&#\\d+;")) { match ->
            match.value.drop(2).dropLast(1).toIntOrNull()?.toChar()?.toString() ?: ""
        }
        .replace(Regex("&[a-zA-Z]+;"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}
