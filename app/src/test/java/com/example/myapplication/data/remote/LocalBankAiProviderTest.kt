package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalBankAiProviderTest {

    @Test
    fun `generates valid JSON parseable by PracticeSetParser`() = runTest {
        val provider = LocalBankAiProvider()
        val prompt = "Generate 5 practice questions for a self-introduction."

        val json = provider.generate(null, prompt)
        val questions = parsePracticeSet(json, PracticeCategory.SELF_INTRODUCTION)

        assertEquals(5, questions.size)
        for (q in questions) {
            assertTrue(q.opicQuestion.isNotBlank())
            assertTrue(q.koreanHint.isNotBlank())
            assertTrue(q.englishSentence.isNotBlank())
        }
    }
}
