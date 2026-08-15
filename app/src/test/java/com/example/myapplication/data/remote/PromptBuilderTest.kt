package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptBuilderTest {

    @Test
    fun `includes category topic and requested count`() {
        val prompt = buildSetPrompt(PracticeCategory.HOBBY, emptyList(), questionCount = 5)

        assertTrue(prompt.contains(PracticeCategory.HOBBY.promptTopic))
        assertTrue(prompt.contains("5"))
        assertTrue(prompt.contains("OPIc AL"))
    }

    @Test
    fun `includes each already asked question to avoid repeats`() {
        val asked = listOf("What do you do on weekends?", "Describe your neighborhood.")

        val prompt = buildSetPrompt(PracticeCategory.HOUSING, asked)

        asked.forEach { question -> assertTrue(prompt.contains(question)) }
    }

    @Test
    fun `handles empty history without listing any question`() {
        val prompt = buildSetPrompt(PracticeCategory.SELF_INTRODUCTION, emptyList())

        assertTrue(prompt.contains("None yet."))
    }

    @Test
    fun `requires literal english-ordered korean hint format`() {
        val prompt = buildSetPrompt(PracticeCategory.COMPARISON, emptyList())

        assertTrue(prompt.contains("word order"))
        assertTrue(prompt.contains("korean_hint"))
        assertTrue(prompt.contains("english_sentence"))
    }

    @Test
    fun `prompt requires exact three-field shape`() {
        val prompt = buildSetPrompt(PracticeCategory.HOUSING, emptyList(), 1)

        assertTrue(prompt.contains("\"opic_question\""))
        assertTrue(prompt.contains("\"korean_hint\""))
        assertTrue(prompt.contains("\"english_sentence\""))
    }
}
