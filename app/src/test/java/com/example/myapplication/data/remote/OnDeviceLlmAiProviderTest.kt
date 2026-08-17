package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeOnDeviceLlmEngine(
    private val ready: Boolean,
    private val output: String
) : OnDeviceLlmEngine {
    var callCount = 0
    override fun isModelReady(): Boolean = ready
    override suspend fun generate(prompt: String): String {
        callCount++
        return output
    }
}

class OnDeviceLlmAiProviderTest {

    @Test
    fun `when model is ready and outputs valid JSON, uses model response`() = runTest {
        val validJson = """[{"opic_question":"Llm Q","korean_hint":"힌트","english_sentence":"Sentence"}]"""
        val engine = FakeOnDeviceLlmEngine(ready = true, output = validJson)
        val provider = OnDeviceLlmAiProvider(engine)

        val response = provider.generate(null, "Test prompt")
        val parsed = parsePracticeSet(response, PracticeCategory.HOUSING)

        assertEquals(1, engine.callCount)
        assertEquals("Llm Q", parsed.first().opicQuestion)
    }

    @Test
    fun `when model is not ready, falls back to local curated bank`() = runTest {
        val engine = FakeOnDeviceLlmEngine(ready = false, output = "")
        val provider = OnDeviceLlmAiProvider(engine)

        val response = provider.generate(null, "Prompt for where the speaker lives")
        val parsed = parsePracticeSet(response, PracticeCategory.HOUSING)

        assertEquals(0, engine.callCount)
        assertTrue(parsed.isNotEmpty())
    }
}
