package com.example.myapplication.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyWorldNewsBankTest {

    @Test
    fun `today news contains exactly 5 major articles`() {
        val news = DailyWorldNewsBank.getNews()
        assertEquals(5, news.size)
    }

    @Test
    fun `every news article contains valid full script, 3 summary points, and key vocabularies`() {
        val news = DailyWorldNewsBank.getNews()
        for (article in news) {
            assertTrue(article.id.isNotBlank())
            assertTrue(article.source.isNotBlank())
            assertTrue(article.headline.isNotBlank())
            assertTrue(article.headlineKorean.isNotBlank())
            assertEquals(3, article.summaryPoints.size)
            assertTrue(article.fullScript.length > 100)
            assertTrue(article.keyVocabularies.size >= 4)
            assertTrue(article.keySentence.isNotBlank())
            assertTrue(article.keySentenceKorean.isNotBlank())

            for (vocab in article.keyVocabularies) {
                assertTrue(vocab.word.isNotBlank())
                assertTrue(vocab.phonetic.startsWith("/"))
                assertTrue(vocab.meaningKorean.isNotBlank())
                assertTrue(vocab.exampleSentence.isNotBlank())
            }
        }
    }

    @Test
    fun `getNewsById returns correct article`() {
        val article = DailyWorldNewsBank.getNewsById("news-1")
        assertNotNull(article)
        assertEquals("The Korea Herald", article?.source)
    }
}
