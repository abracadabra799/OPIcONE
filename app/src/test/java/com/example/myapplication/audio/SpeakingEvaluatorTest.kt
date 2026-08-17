package com.example.myapplication.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeakingEvaluatorTest {

    @Test
    fun `evaluates exact match to 100 percent`() {
        val target = "I live in a cozy apartment in Seoul."
        val spoken = "I live in a cozy apartment in Seoul"

        val result = SpeakingEvaluator.evaluate(spoken, target)

        assertEquals(100, result.accuracyScore)
        assertTrue(result.evaluatedWords.all { it.isMatched })
        assertTrue(result.feedbackMessage.contains("완벽"))
    }

    @Test
    fun `evaluates partial match correctly`() {
        val target = "I live in a cozy apartment in Seoul."
        val spoken = "I live in Seoul"

        val result = SpeakingEvaluator.evaluate(spoken, target)

        assertTrue(result.accuracyScore in 1..99)
        val matchedTokens = result.evaluatedWords.filter { it.isMatched }.map { it.text }
        assertTrue(matchedTokens.contains("I"))
        assertTrue(matchedTokens.contains("live"))
        assertTrue(matchedTokens.contains("in"))
        assertTrue(matchedTokens.contains("Seoul."))
    }

    @Test
    fun `empty spoken text returns 0 percent`() {
        val target = "I live in Seoul."
        val result = SpeakingEvaluator.evaluate("", target)

        assertEquals(0, result.accuracyScore)
        assertTrue(result.evaluatedWords.none { it.isMatched })
    }
}
