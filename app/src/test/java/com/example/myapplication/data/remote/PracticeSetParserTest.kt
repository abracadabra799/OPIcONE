package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PracticeSetParserTest {

    private val validJson = """
        [
          {"korean_hint": "나는 좋아한다 / 산책하는 것을", "english_sentence": "I like taking walks."},
          {"korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}
        ]
    """.trimIndent()

    @Test
    fun `parses valid json array into practice questions`() {
        val result = parsePracticeSet(validJson, PracticeCategory.HOBBY)

        assertEquals(2, result.size)
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
    fun `throws PracticeSetParseException on empty array`() {
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("[]", PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `throws PracticeSetParseException when a field is blank`() {
        val jsonWithBlank = """[{"korean_hint": "", "english_sentence": "I like it."}]"""

        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet(jsonWithBlank, PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `tolerates surrounding whitespace`() {
        val paddedJson = "\n  $validJson  \n"

        val result = parsePracticeSet(paddedJson, PracticeCategory.HOUSING)

        assertEquals(2, result.size)
    }
}
