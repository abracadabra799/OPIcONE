package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.settings.AiSettingsStore
import java.util.concurrent.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeAiSettingsStore(
    private var selected: AiProvider,
    private val keys: MutableMap<AiProvider, String> = mutableMapOf()
) : AiSettingsStore {
    override fun getSelectedProvider() = selected

    override fun setSelectedProvider(provider: AiProvider) {
        selected = provider
    }

    override fun getApiKey(provider: AiProvider) = keys[provider]

    override fun setApiKey(provider: AiProvider, apiKey: String) {
        keys[provider] = apiKey
    }

    override fun clearApiKey(provider: AiProvider) {
        keys.remove(provider)
    }
}

private class FakeProvider(
    override val provider: AiProvider,
    private val responses: List<Result<String>>
) : PracticeAiProvider {
    var callCount = 0
    var lastKey: String? = null
    var lastPrompt: String? = null

    override suspend fun generate(apiKey: String, prompt: String): String {
        lastKey = apiKey
        lastPrompt = prompt
        return responses.getOrElse(callCount++) { responses.last() }.getOrThrow()
    }
}

class PracticeRepositoryTest {

    private val validResponseJson = """
        [{"korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}]
    """.trimIndent()

    private fun repository(
        settings: AiSettingsStore,
        claude: PracticeAiProvider,
        openAi: PracticeAiProvider
    ): PracticeRepository = DefaultPracticeRepository(setOf(claude, openAi), settings)

    @Test
    fun `calls only selected OpenAI provider with its key`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "openai-key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))

        val result = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        )

        assertTrue(result.isSuccess)
        assertEquals(0, claude.callCount)
        assertEquals(1, openAi.callCount)
        assertEquals("openai-key", openAi.lastKey)
    }

    @Test
    fun `missing selected provider key fails without network call`() = runTest {
        val settings = FakeAiSettingsStore(AiProvider.OPENAI)
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))

        val error = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        ).exceptionOrNull()

        assertTrue(error is MissingApiKey)
        assertEquals(AiProvider.OPENAI, (error as MissingApiKey).provider)
        assertEquals(0, claude.callCount)
        assertEquals(0, openAi.callCount)
    }

    @Test
    fun `retries malformed JSON once on same provider`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("not json"), Result.success(validResponseJson))
        )

        assertTrue(repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        ).isSuccess)
        assertEquals(0, claude.callCount)
        assertEquals(2, openAi.callCount)
    }

    @Test
    fun `second malformed JSON becomes InvalidPracticeSet`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("bad"), Result.success("still bad"))
        )

        val error = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        ).exceptionOrNull()

        assertTrue(error is InvalidPracticeSet)
        assertEquals(2, openAi.callCount)
    }

    @Test
    fun `InvalidPracticeSet cause chain excludes malformed response content`() = runTest {
        val sentinel = "remote-secret-sentinel"
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("bad"), Result.success("$sentinel not json"))
        )

        val error = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        ).exceptionOrNull()
        val causeChain = generateSequence(error) { it.cause }
            .joinToString("\\n") { it.message.orEmpty() }

        assertTrue(error is InvalidPracticeSet)
        assertFalse(causeChain.contains(sentinel))
    }

    @Test
    fun `provider failure is not retried`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.failure(RateLimited(AiProvider.OPENAI)))
        )

        val error = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        ).exceptionOrNull()

        assertTrue(error is RateLimited)
        assertEquals(1, openAi.callCount)
    }

    @Test
    fun `cancellation is rethrown`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.CLAUDE to "key")
        )
        val claude = FakeProvider(
            AiProvider.CLAUDE,
            listOf(Result.failure(CancellationException("cancel")))
        )
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))

        var cancellation: CancellationException? = null
        try {
            repository(settings, claude, openAi).generateSet(
                PracticeCategory.HOUSING,
                emptyList(),
                1
            )
        } catch (error: CancellationException) {
            cancellation = error
        }
        assertTrue(cancellation != null)
    }

    @Test
    fun `returns parsed questions on success`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.CLAUDE to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))

        val result = repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        )

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("I live in Seoul.", result.getOrThrow()[0].englishSentence)
    }

    @Test
    fun `builds a prompt that mentions the requested category`() = runTest {
        val settings = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.CLAUDE to "key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))

        repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING,
            emptyList(),
            1
        )

        assertTrue(claude.lastPrompt.orEmpty().contains(PracticeCategory.HOUSING.promptTopic))
    }
}
