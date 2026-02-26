package com.hypernews.app.data.remote.rss

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Generates summaries from news article content using a hybrid approach:
 * 1. Lead-3 baseline (first 3 sentences) - works well for news due to inverted pyramid structure
 * 2. TextRank algorithm for longer articles - graph-based sentence ranking
 * 
 * Research shows that news articles follow the "inverted pyramid" structure where
 * the most important information (5W1H: Who, What, Where, When, Why, How) appears
 * in the first few sentences. Lead-3 baseline often outperforms complex algorithms
 * for news summarization.
 * 
 * Sources:
 * - Mihalcea & Tarau (2004) "TextRank: Bringing Order into Texts"
 * - News summarization research showing Lead-3 effectiveness
 */
@Singleton
class SummaryGenerator @Inject constructor() {
    
    companion object {
        private const val MAX_SUMMARY_LENGTH = 500
        private const val MIN_SENTENCE_LENGTH = 15
        private const val TEXTRANK_ITERATIONS = 30
        private const val DAMPING_FACTOR = 0.85
        
        // Türkçe stop words (yaygın kelimeler)
        private val TURKISH_STOP_WORDS = setOf(
            "ve", "bir", "bu", "da", "de", "için", "ile", "mi", "mı", "mu", "mü",
            "ne", "o", "olan", "olarak", "onu", "veya", "ya", "çok", "daha", "en",
            "gibi", "her", "kadar", "ki", "var", "yok", "ben", "sen", "biz", "siz",
            "onlar", "ama", "ancak", "fakat", "çünkü", "eğer", "şu", "şey", "olan",
            "oldu", "olmuş", "olan", "ise", "hem", "hep", "hiç", "nasıl", "neden",
            "nerede", "hangi", "kendi", "sonra", "önce", "ayrıca", "böyle", "şöyle",
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "may", "might", "must", "shall", "can", "need", "dare",
            "to", "of", "in", "for", "on", "with", "at", "by", "from", "as",
            "into", "through", "during", "before", "after", "above", "below",
            "between", "under", "again", "further", "then", "once", "here",
            "there", "when", "where", "why", "how", "all", "each", "few", "more",
            "most", "other", "some", "such", "no", "nor", "not", "only", "own",
            "same", "so", "than", "too", "very", "just", "also", "now", "said"
        )
    }
    
    fun generateSummary(content: String): String {
        val cleaned = cleanHtml(content)
        if (cleaned.isBlank()) return ""
        if (cleaned.length <= MAX_SUMMARY_LENGTH) return cleaned
        
        val sentences = splitIntoSentences(cleaned)
        if (sentences.isEmpty()) return cleaned.take(MAX_SUMMARY_LENGTH - 3) + "..."
        
        // Kısa içerik için Lead-3 kullan (ilk 3 cümle)
        if (sentences.size <= 5) {
            return buildSummaryFromSentences(sentences.take(3), MAX_SUMMARY_LENGTH)
        }
        
        // Uzun içerik için TextRank + Lead bonus kullan
        val rankedSentences = textRankWithLeadBonus(sentences)
        return buildSummaryFromSentences(rankedSentences, MAX_SUMMARY_LENGTH)
    }

    
    /**
     * TextRank algorithm with lead sentence bonus.
     * Combines graph-based ranking with position-based scoring.
     */
    private fun textRankWithLeadBonus(sentences: List<String>): List<String> {
        val n = sentences.size
        if (n == 0) return emptyList()
        
        // 1. Cümleler arası benzerlik matrisi oluştur
        val similarityMatrix = Array(n) { DoubleArray(n) { 0.0 } }
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val similarity = calculateSentenceSimilarity(sentences[i], sentences[j])
                similarityMatrix[i][j] = similarity
                similarityMatrix[j][i] = similarity
            }
        }
        
        // 2. TextRank iterasyonu (PageRank benzeri)
        var scores = DoubleArray(n) { 1.0 / n }
        
        repeat(TEXTRANK_ITERATIONS) {
            val newScores = DoubleArray(n) { 0.0 }
            for (i in 0 until n) {
                var sum = 0.0
                for (j in 0 until n) {
                    if (i != j) {
                        val outSum = similarityMatrix[j].sum()
                        if (outSum > 0) {
                            sum += similarityMatrix[j][i] / outSum * scores[j]
                        }
                    }
                }
                newScores[i] = (1 - DAMPING_FACTOR) / n + DAMPING_FACTOR * sum
            }
            scores = newScores
        }
        
        // 3. Lead bonus ekle (ilk cümleler için ekstra puan)
        for (i in 0 until n) {
            val leadBonus = when (i) {
                0 -> 0.5  // İlk cümle %50 bonus
                1 -> 0.3  // İkinci cümle %30 bonus
                2 -> 0.2  // Üçüncü cümle %20 bonus
                3 -> 0.1  // Dördüncü cümle %10 bonus
                else -> 0.0
            }
            scores[i] = scores[i] * (1 + leadBonus)
        }
        
        // 4. En yüksek puanlı cümleleri seç (orijinal sırayı koru)
        val indexedScores = scores.mapIndexed { index, score -> index to score }
            .sortedByDescending { it.second }
            .take(4) // En iyi 4 cümle
            .map { it.first }
            .sorted() // Orijinal sıraya göre sırala
        
        return indexedScores.map { sentences[it] }
    }
    
    /**
     * Jaccard similarity + word overlap based sentence similarity.
     * Simple but effective for extractive summarization.
     */
    private fun calculateSentenceSimilarity(s1: String, s2: String): Double {
        val words1 = tokenize(s1)
        val words2 = tokenize(s2)
        
        if (words1.isEmpty() || words2.isEmpty()) return 0.0
        
        val intersection = words1.intersect(words2).size
        val union = words1.union(words2).size
        
        if (union == 0) return 0.0
        
        // Jaccard similarity + normalization
        val jaccard = intersection.toDouble() / union
        
        // Cosine-like normalization
        val normalized = intersection.toDouble() / (sqrt(words1.size.toDouble()) * sqrt(words2.size.toDouble()))
        
        return (jaccard + normalized) / 2
    }
    
    /**
     * Tokenize sentence into meaningful words (remove stop words).
     */
    private fun tokenize(text: String): Set<String> {
        return text.lowercase()
            .replace(Regex("[^a-zçğıöşüA-ZÇĞİÖŞÜ0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 2 && it !in TURKISH_STOP_WORDS }
            .toSet()
    }
    
    private fun splitIntoSentences(text: String): List<String> {
        // Kısaltmaları koru (Dr., Prof., vb.)
        val protected = text
            .replace(Regex("(Dr|Prof|Mr|Mrs|Ms|Jr|Sr|vs|etc|Inc|Ltd|Corp)\\."), "$1<DOT>")
            .replace(Regex("(\\d)\\."), "$1<DOT>")
        
        return protected
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.replace("<DOT>", ".").trim() }
            .filter { it.length >= MIN_SENTENCE_LENGTH }
    }
    
    private fun buildSummaryFromSentences(sentences: List<String>, maxLength: Int): String {
        if (sentences.isEmpty()) return ""
        
        val result = StringBuilder()
        for (sentence in sentences) {
            if (result.length + sentence.length + 1 > maxLength) {
                if (result.isEmpty()) {
                    // En az bir cümle ekle
                    return sentence.take(maxLength - 3) + "..."
                }
                break
            }
            if (result.isNotEmpty()) result.append(" ")
            result.append(sentence)
        }
        
        val summary = result.toString()
        return if (!summary.endsWith(".") && !summary.endsWith("!") && !summary.endsWith("?")) {
            summary + "..."
        } else {
            summary
        }
    }
    
    fun cleanHtml(html: String): String = html
        .replace(Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), "")
        .replace(Regex("<style[^>]*>[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), "")
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
