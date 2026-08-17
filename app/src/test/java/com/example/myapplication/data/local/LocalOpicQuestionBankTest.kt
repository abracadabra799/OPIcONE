package com.example.myapplication.data.local

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalOpicQuestionBankTest {

    @Test
    fun `all 8 categories have at least 5 questions in the bank`() {
        for (category in PracticeCategory.entries) {
            val questions = LocalOpicQuestionBank.getQuestions(category, 5)
            assertEquals("Category $category must have 5 questions", 5, questions.size)
            for (q in questions) {
                assertTrue("Question must not be blank", q.opicQuestion.isNotBlank())
                assertTrue("Hint must not be blank", q.koreanHint.isNotBlank())
                assertTrue("Sentence must not be blank", q.englishSentence.isNotBlank())
                assertEquals(category, q.category)
            }
        }
    }

    @Test
    fun `excludes already asked questions when possible`() {
        val category = PracticeCategory.HOUSING
        val allQuestions = LocalOpicQuestionBank.getQuestions(category, 2)
        val excludeFirst = setOf(allQuestions.first().opicQuestion)

        val nextQuestions = LocalOpicQuestionBank.getQuestions(category, 2, excludeQuestions = excludeFirst)

        assertFalse(
            nextQuestions.map { it.opicQuestion }.contains(allQuestions.first().opicQuestion)
        )
    }
}
