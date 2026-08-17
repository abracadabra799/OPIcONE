package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.session.PracticeSessionStore
import com.example.myapplication.data.settings.AiSettingsStore
import java.util.concurrent.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeAiSettingsStore(
    private var selected: AiProvider,
    private val keys: MutableMap<AiProvider, String> = mutableMapOf(),
    private var modelPath: String? = null
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

    override fun getModelPath(): String? = modelPath

    override fun setModelPath(path: String) {
        modelPath = path
    }
}

private class FakeProvider(
    override val provider: AiProvider,
    private val responses: List<Result<String>>
) : PracticeAiProvider {
    var callCount = 0
    var lastKey: String? = null
    val prompts = mutableListOf<String>()

    override suspend fun generate(apiKey: String?, prompt: String): String {
        lastKey = apiKey
        prompts += prompt
        return responses.getOrElse(callCount++) { responses.last() }.getOrThrow()
    }
}

private fun json(vararg questions: String): String = questions.joinToString(
    prefix = "[",
    postfix = "]",
    separator = ","
) { question ->
    """{"opic_question":"$question","korean_hint":"힌트","english_sentence":"Answer for $question"}"""
}

private fun repository(
    store: PracticeSessionStore,
    selectedProvider: PracticeAiProvider
): PracticeRepository = DefaultPracticeRepository(
    providers = setOf(selectedProvider),
    settingsStore = FakeAiSettingsStore(
        selected = selectedProvider.provider,
        keys = mutableMapOf(selectedProvider.provider to "test-key")
    ),
    sessionStore = store
)

class PracticeRepositoryTest {

    @Test
    fun `LOCAL_BANK generates questions without requiring API key`() = runTest {
        val store = PracticeSessionStore()
        val settings = FakeAiSettingsStore(AiProvider.LOCAL_BANK)
        val localBank = FakeProvider(AiProvider.LOCAL_BANK, listOf(Result.success(json("Local Q1"))))
        val repository = DefaultPracticeRepository(setOf(localBank), settings, store)

        val result = repository.generateSet(PracticeCategory.HOUSING, 1)

        assertTrue(result.isSuccess)
        assertEquals(1, localBank.callCount)
        assertEquals("Local Q1", result.getOrThrow().first().opicQuestion)
    }

    @Test
    fun `calls only selected OpenAI provider with its key`() = runTest {
        val store = PracticeSessionStore()
        val settings = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(AiProvider.OPENAI to "openai-key")
        )
        val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(json("Claude"))))
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(json("OpenAI"))))
        val repository = DefaultPracticeRepository(setOf(claude, openAi), settings, store)

        val result = repository.generateSet(PracticeCategory.HOUSING, 1)

        assertTrue(result.isSuccess)
        assertEquals(0, claude.callCount)
        assertEquals(1, openAi.callCount)
        assertEquals("openai-key", openAi.lastKey)
    }

    @Test
    fun `missing selected provider key fails without network call`() = runTest {
        val store = PracticeSessionStore()
        val settings = FakeAiSettingsStore(AiProvider.OPENAI)
        val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(json("Question"))))
        val repository = DefaultPracticeRepository(setOf(openAi), settings, store)

        val error = repository.generateSet(PracticeCategory.HOUSING, 1).exceptionOrNull()

        assertTrue(error is MissingApiKey)
        assertEquals(AiProvider.OPENAI, (error as MissingApiKey).provider)
        assertEquals(0, openAi.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `retries malformed JSON once on same provider`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("not json"), Result.success(json("Question")))
        )

        val result = repository(store, provider).generateSet(PracticeCategory.HOUSING, 1)

        assertTrue(result.isSuccess)
        assertEquals(2, provider.callCount)
        assertTrue(store.containsQuestion("Question"))
    }

    @Test
    fun `filters session and response duplicates then refills once`() = runTest {
        val store = PracticeSessionStore().apply {
            addAskedQuestions(listOf("Old question"))
        }
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(
                Result.success(json("Old question", "New question", " new   QUESTION ")),
                Result.success(json("Another question"))
            )
        )

        val result = repository(store, provider).generateSet(PracticeCategory.HOUSING, 2)

        assertEquals(
            listOf("New question", "Another question"),
            result.getOrThrow().map(PracticeQuestion::opicQuestion)
        )
        assertEquals(2, provider.callCount)
        assertTrue(provider.prompts[1].contains("new question", ignoreCase = true))
        assertTrue(store.containsQuestion("Old question"))
        assertTrue(store.containsQuestion("New question"))
        assertTrue(store.containsQuestion("Another question"))
    }

    @Test
    fun `two malformed responses consume only two calls and do not update history`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("bad"), Result.success("still bad"))
        )

        val error = repository(store, provider)
            .generateSet(PracticeCategory.HOUSING, 2)
            .exceptionOrNull()

        assertTrue(error is InvalidPracticeSet)
        assertEquals(2, provider.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `InvalidPracticeSet cause chain excludes malformed response content`() = runTest {
        val sentinel = "remote-secret-sentinel"
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.success("bad"), Result.success("$sentinel not json"))
        )

        val error = repository(store, provider)
            .generateSet(PracticeCategory.HOUSING, 1)
            .exceptionOrNull()
        val causeChain = generateSequence(error) { it.cause }
            .joinToString("\n") { it.message.orEmpty() }

        assertTrue(error is InvalidPracticeSet)
        assertTrue(error?.cause is PracticeSetParseException)
        assertFalse(causeChain.contains(sentinel))
    }

    @Test
    fun `insufficient second response returns typed failure without partial history`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(
                Result.success(json("Only one")),
                Result.success(json(" only   ONE "))
            )
        )

        val error = repository(store, provider)
            .generateSet(PracticeCategory.HOUSING, 2)
            .exceptionOrNull()

        assertTrue(error is InsufficientUniqueQuestions)
        assertEquals(2, provider.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `provider failure is not retried and does not update partial history`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(
                Result.success(json("Only one")),
                Result.failure(RateLimited(AiProvider.OPENAI))
            )
        )

        val error = repository(store, provider)
            .generateSet(PracticeCategory.HOUSING, 2)
            .exceptionOrNull()

        assertTrue(error is RateLimited)
        assertEquals(2, provider.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `first-call provider failure is not retried`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.OPENAI,
            listOf(Result.failure(RateLimited(AiProvider.OPENAI)))
        )

        val error = repository(store, provider)
            .generateSet(PracticeCategory.HOUSING, 1)
            .exceptionOrNull()

        assertTrue(error is RateLimited)
        assertEquals(1, provider.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `cancellation is rethrown`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(
            AiProvider.CLAUDE,
            listOf(Result.failure(CancellationException("cancel")))
        )

        var cancellation: CancellationException? = null
        try {
            repository(store, provider).generateSet(PracticeCategory.HOUSING, 1)
        } catch (error: CancellationException) {
            cancellation = error
        }

        assertTrue(cancellation != null)
        assertEquals(1, provider.callCount)
        assertTrue(store.askedQuestions().isEmpty())
    }

    @Test
    fun `returns parsed questions and updates history only on complete success`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(json("Question"))))

        val result = repository(store, provider).generateSet(PracticeCategory.HOUSING, 1)

        assertTrue(result.isSuccess)
        assertEquals("Answer for Question", result.getOrThrow()[0].englishSentence)
        assertEquals(listOf("question"), store.askedQuestions())
    }

    @Test
    fun `builds a prompt that mentions the requested category`() = runTest {
        val store = PracticeSessionStore()
        val provider = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(json("Question"))))

        repository(store, provider).generateSet(PracticeCategory.HOUSING, 1)

        assertTrue(provider.prompts.single().contains(PracticeCategory.HOUSING.promptTopic))
    }
}
