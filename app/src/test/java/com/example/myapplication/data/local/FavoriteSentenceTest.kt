package com.example.myapplication.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteSentenceTest {

    @Test
    fun `daysRemaining calculates correct 6-day countdown`() {
        val now = 1000000000000L
        val dayMillis = 86400000L

        val brandNew = FavoriteSentence(
            category = "OPIC: HOUSING",
            koreanHint = "힌트",
            englishSentence = "Sentence",
            createdAt = now
        )
        assertEquals(6, brandNew.daysRemaining(now))

        val twoDaysOld = brandNew.copy(createdAt = now - 2 * dayMillis)
        assertEquals(4, twoDaysOld.daysRemaining(now))

        val fiveDaysOld = brandNew.copy(createdAt = now - 5 * dayMillis)
        assertEquals(1, fiveDaysOld.daysRemaining(now))

        val sixDaysOld = brandNew.copy(createdAt = now - 6 * dayMillis)
        assertEquals(1, sixDaysOld.daysRemaining(now))
    }

    @Test
    fun `classification helpers distinguish opic, news vocab, and news sentences`() {
        val opicItem = FavoriteSentence(category = "OPIC: HOUSING", koreanHint = "힌트", englishSentence = "S", createdAt = 0L)
        val vocabItem = FavoriteSentence(category = "NEWS_VOCAB: TECH", koreanHint = "뜻", englishSentence = "initiative", createdAt = 0L)
        val newsSentenceItem = FavoriteSentence(category = "NEWS_SENTENCE: CNN", koreanHint = "뜻", englishSentence = "S", createdAt = 0L)

        assertTrue(opicItem.isOpic())
        assertFalse(opicItem.isNewsVocab())
        assertFalse(opicItem.isNewsSentence())

        assertTrue(vocabItem.isNewsVocab())
        assertFalse(vocabItem.isOpic())

        assertTrue(newsSentenceItem.isNewsSentence())
        assertFalse(newsSentenceItem.isOpic())
    }
}
