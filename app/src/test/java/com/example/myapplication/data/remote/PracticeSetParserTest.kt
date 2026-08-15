package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeSetParserTest {

    private val validJson = """
        [
          {"opic_question": "Tell me about your hobby.", "korean_hint": "나는 좋아한다 / 산책하는 것을", "english_sentence": "I like taking walks."},
          {"opic_question": "Tell me about your home.", "korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}
        ]
    """.trimIndent()

    @Test
    fun `parses valid json array into practice questions`() {
        val result = parsePracticeSet(validJson, PracticeCategory.HOBBY)

        assertEquals(2, result.size)
        assertEquals("Tell me about your hobby.", result[0].opicQuestion)
        assertEquals("I like taking walks.", result[0].englishSentence)
        assertEquals(PracticeCategory.HOBBY, result[0].category)
    }

    @Test
    fun `throws PracticeSetParseException on malformed json`() {
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("not json", PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `malformed remote JSON is not retained in the parse exception cause chain`() {
        val sentinel = "remote-secret-sentinel"
        val error = assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("[{\"opic_question\":\"$sentinel\"", PracticeCategory.HOBBY)
        }

        val causeChain = generateSequence<Throwable>(error) { it.cause }
            .joinToString("\\n") { it.message.orEmpty() }

        assertTrue(error.cause == null)
        assertFalse(causeChain.contains(sentinel))
    }

    @Test
    fun `throws PracticeSetParseException on empty array`() {
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("[]", PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `throws PracticeSetParseException when a field is blank`() {
        val jsonWithBlank = """[{"opic_question": "Tell me about your hobby.", "korean_hint": "", "english_sentence": "I like it."}]"""

        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet(jsonWithBlank, PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `rejects missing or blank OPIc question`() {
        val missing = """[{"korean_hint":"힌트","english_sentence":"Answer."}]"""
        val blank = """[{"opic_question":"  ","korean_hint":"힌트","english_sentence":"Answer."}]"""

        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet(missing, PracticeCategory.HOUSING)
        }
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet(blank, PracticeCategory.HOUSING)
        }
    }

    @Test
    fun `tolerates surrounding whitespace`() {
        val paddedJson = "\n  $validJson  \n"

        val result = parsePracticeSet(paddedJson, PracticeCategory.HOUSING)

        assertEquals(2, result.size)
    }
}
