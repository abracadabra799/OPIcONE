package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeClaudePromptSender(
    private val responses: List<Result<String>>
) : ClaudePromptSender {
    constructor(response: Result<String>) : this(listOf(response))

    var lastPrompt: String? = null
    var callCount: Int = 0

    override suspend fun sendPrompt(apiKey: String, prompt: String): String {
        lastPrompt = prompt
        val response = responses.getOrElse(callCount) { responses.last() }
        callCount += 1
        return response.getOrThrow()
    }
}

class PracticeRepositoryTest {

    private val validResponseJson = """
        [{"korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}]
    """.trimIndent()

    @Test
    fun `returns parsed questions on success`() = runTest {
        val sender = FakeClaudePromptSender(Result.success(validResponseJson))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList(),
            questionCount = 1
        )

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("I live in Seoul.", result.getOrThrow()[0].englishSentence)
    }

    @Test
    fun `builds a prompt that mentions the requested category`() = runTest {
        val sender = FakeClaudePromptSender(Result.success(validResponseJson))
        val repository = ClaudePracticeRepository(sender)

        repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList(),
            questionCount = 1
        )

        assertTrue(sender.lastPrompt.orEmpty().contains(PracticeCategory.HOUSING.promptTopic))
    }

    @Test
    fun `wraps network failure into a failed Result`() = runTest {
        val sender = FakeClaudePromptSender(Result.failure(ClaudeApiException("boom")))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `wraps malformed response into a failed Result`() = runTest {
        val sender = FakeClaudePromptSender(Result.success("not json"))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `retries once when the first response cannot be parsed`() = runTest {
        val sender = FakeClaudePromptSender(
            listOf(Result.success("not json"), Result.success(validResponseJson))
        )
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList(),
            questionCount = 1
        )

        assertTrue(result.isSuccess)
        assertEquals(2, sender.callCount)
        assertEquals("I live in Seoul.", result.getOrThrow().single().englishSentence)
    }

    @Test
    fun `stops after the second malformed response`() = runTest {
        val sender = FakeClaudePromptSender(
            listOf(Result.success("not json"), Result.success("still not json"))
        )
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
        assertEquals(2, sender.callCount)
    }

    @Test
    fun `does not retry an api failure`() = runTest {
        val sender = FakeClaudePromptSender(Result.failure(ClaudeApiException("boom")))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
        assertEquals(1, sender.callCount)
    }
}
