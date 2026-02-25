package com.hypernews.app.data.remote.firebase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfanityFilter @Inject constructor() {
    private val profanityWords = mutableSetOf(
        "küfür", "hakaret", "argo1", "argo2" // Placeholder - gerçek liste Firestore'dan yüklenecek
    )
    
    private val charReplacements = mapOf(
        '@' to 'a', '4' to 'a', '3' to 'e', '1' to 'i', '!' to 'i',
        '0' to 'o', '$' to 's', '5' to 's', '7' to 't', '*' to ' ', '.' to ' '
    )
    
    fun containsProfanity(text: String): Boolean {
        val normalized = normalizeText(text)
        return profanityWords.any { word -> normalized.contains(word, ignoreCase = true) }
    }
    
    fun filterText(text: String): String {
        var result = text
        val normalized = normalizeText(text)
        profanityWords.forEach { word ->
            if (normalized.contains(word, ignoreCase = true)) {
                val regex = Regex(word, RegexOption.IGNORE_CASE)
                result = result.replace(regex) { "*".repeat(it.value.length) }
            }
        }
        return result
    }
    
    fun updateWordList(words: Set<String>) {
        profanityWords.clear()
        profanityWords.addAll(words.map { it.lowercase() })
    }
    
    private fun normalizeText(text: String): String = text.map { char ->
        charReplacements[char.lowercaseChar()] ?: char.lowercaseChar()
    }.joinToString("").replace(Regex("\\s+"), " ")
}
