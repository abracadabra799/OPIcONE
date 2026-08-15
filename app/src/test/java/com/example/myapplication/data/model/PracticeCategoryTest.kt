package com.example.myapplication.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeCategoryTest {

    @Test
    fun `has eight categories covering the al-level topic list`() {
        assertEquals(8, PracticeCategory.entries.size)
    }

    @Test
    fun `every category has non-blank labels`() {
        PracticeCategory.entries.forEach { category ->
            assertFalse(category.koreanLabel.isBlank())
            assertFalse(category.promptTopic.isBlank())
        }
    }

    @Test
    fun `includes the roleplay and comparison categories required for AL level`() {
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.SURVEY_ROLEPLAY))
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.COMPARISON))
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.PROBLEM_SOLVING_ROLEPLAY))
    }

    @Test
    fun `practice questions with identical fields are equal`() {
        val a = PracticeQuestion(
            opicQuestion = "Tell me about your hobby.",
            koreanHint = "힌트",
            englishSentence = "sentence",
            category = PracticeCategory.HOBBY
        )
        val b = PracticeQuestion(
            opicQuestion = "Tell me about your hobby.",
            koreanHint = "힌트",
            englishSentence = "sentence",
            category = PracticeCategory.HOBBY
        )

        assertEquals(a, b)
    }
}
