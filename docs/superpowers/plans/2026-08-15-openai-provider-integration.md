# OpenAI Provider Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let users select Claude or OpenAI, securely store a separate API key for each, and generate OPIc practice sets through only the selected provider.

**Architecture:** Replace the Claude-specific repository boundary with a provider-neutral `PracticeAiProvider` and inject both providers plus `AiSettingsStore` into `DefaultPracticeRepository`. Providers own HTTP request/response details and normalize failures; the repository owns provider selection, prompt construction, shared practice-set parsing, and the single parse retry. Settings and practice UI expose provider-aware state without ever reading a stored plaintext key back into a text field.

**Tech Stack:** Kotlin 2.2.10, Android/Jetpack Compose Material 3, coroutines, kotlinx.serialization, OkHttp 5.4.0, EncryptedSharedPreferences, JUnit 4, MockWebServer 5.4.0, AndroidX Compose testing.

## Global Constraints

- OpenAI uses `POST https://api.openai.com/v1/responses` with model `gpt-5.6-terra`.
- Claude keeps its existing model and Messages API behavior.
- Provider selection is manual; never fall back from one provider to the other.
- Retry exactly once only when practice-set JSON parsing fails, and retry the same provider with the same prompt.
- Do not retry authentication, rate-limit, network, or other provider failures.
- Keep the legacy `anthropic_api_key` preference readable so existing users remain on Claude without reconfiguration.
- Never put an API key or raw remote response body in logs, displayed messages, or exception messages.
- Do not add a DI framework or modify the Room schema.

---

## File Structure

- Create `data/remote/AiProvider.kt`: provider identity, provider contract, and normalized exception hierarchy.
- Modify `data/remote/ClaudeApiClient.kt`: implement the common contract and sanitize/status-map failures.
- Create `data/remote/OpenAiApiClient.kt`: Responses API serialization, HTTP call, and output-text extraction.
- Replace `data/settings/ApiKeyStore.kt` with `AiSettingsStore.kt`: provider selection and provider-scoped key contract.
- Rename `data/settings/EncryptedApiKeyStore.kt` to `EncryptedAiSettingsStore.kt`: encrypted implementation with legacy Claude-key compatibility.
- Modify `data/remote/PracticeRepository.kt`: selected-provider routing, key lookup, shared parsing, and one retry.
- Modify `ui/settings/SettingsUiState.kt`, `SettingsViewModel.kt`, and `SettingsScreen.kt`: provider-aware settings without plaintext key hydration.
- Modify `ui/set/PracticeSetUiState.kt`, `PracticeSetViewModel.kt`, and `PracticeScreen.kt`: typed user-safe errors and settings navigation action.
- Modify `di/AppContainer.kt` and `ui/navigation/AppNavGraph.kt`: manual dependency wiring and navigation callbacks.
- Update matching unit and instrumentation tests beside each production change.

---

### Task 1: Common Provider and Error Contract

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/AiProvider.kt`
- Test: `app/src/test/java/com/example/myapplication/data/remote/AiProviderTest.kt`

**Interfaces:**
- Consumes: no new project interfaces.
- Produces: `AiProvider`, `PracticeAiProvider.generate(apiKey: String, prompt: String): String`, and the normalized `PracticeGenerationException` subclasses used by all later tasks.

- [ ] **Step 1: Write the failing contract tests**

```kotlin
package com.example.myapplication.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AiProviderTest {
    @Test
    fun `provider labels are stable Korean UI copy`() {
        assertEquals("Claude", AiProvider.CLAUDE.displayName)
        assertEquals("OpenAI", AiProvider.OPENAI.displayName)
    }

    @Test
    fun `provider failure message cannot contain a remote body`() {
        val error = ProviderFailure(AiProvider.OPENAI, 500)
        assertEquals("OpenAI 요청에 실패했습니다. (HTTP 500)", error.message)
        assertFalse(error.message.orEmpty().contains("responseBody"))
    }
}
```

- [ ] **Step 2: Run the tests and confirm they fail because the types do not exist**

Run: `./gradlew testDebugUnitTest --tests '*AiProviderTest'`

Expected: compilation failure for unresolved `AiProvider` and `ProviderFailure`.

- [ ] **Step 3: Add the common contract and sanitized error hierarchy**

```kotlin
package com.example.myapplication.data.remote

enum class AiProvider(val displayName: String) {
    CLAUDE("Claude"),
    OPENAI("OpenAI")
}

interface PracticeAiProvider {
    val provider: AiProvider
    suspend fun generate(apiKey: String, prompt: String): String
}

sealed class PracticeGenerationException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

class MissingApiKey(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} API 키가 없습니다."
)

class AuthenticationFailed(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} API 키를 확인해주세요."
)

class RateLimited(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요."
)

class NetworkFailure(val provider: AiProvider, cause: Throwable) : PracticeGenerationException(
    "네트워크 연결을 확인해주세요.", cause
)

class ProviderFailure(val provider: AiProvider, val statusCode: Int) : PracticeGenerationException(
    "${provider.displayName} 요청에 실패했습니다. (HTTP $statusCode)"
)

class InvalidProviderResponse(val provider: AiProvider, cause: Throwable? = null) :
    PracticeGenerationException("${provider.displayName} 응답을 읽지 못했습니다.", cause)

class InvalidPracticeSet(cause: Throwable) : PracticeGenerationException(
    "생성된 문제 형식이 올바르지 않습니다.", cause
)
```

- [ ] **Step 4: Run the focused tests**

Run: `./gradlew testDebugUnitTest --tests '*AiProviderTest'`

Expected: PASS.

- [ ] **Step 5: Commit the provider contract**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/AiProvider.kt app/src/test/java/com/example/myapplication/data/remote/AiProviderTest.kt
git commit -m "Add common AI provider contract"
```

---

### Task 2: Claude Provider Normalization

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/data/remote/ClaudeApiClient.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/remote/ClaudeApiClientTest.kt`

**Interfaces:**
- Consumes: `PracticeAiProvider`, `AiProvider.CLAUDE`, and normalized exceptions from Task 1.
- Produces: `ClaudeApiClient : PracticeAiProvider`, preserving `generate(apiKey, prompt)` as the only public network operation.

- [ ] **Step 1: Replace Claude-specific assertions with normalized behavior tests**

Add tests that assert the common method and safe error mapping:

```kotlin
@Test
fun `identifies itself as Claude`() {
    assertEquals(AiProvider.CLAUDE, client.provider)
}

@Test
fun `maps 401 without exposing response body`() = runTest {
    server.enqueue(MockResponse(code = 401, body = "secret remote details"))

    val error = runCatching { client.generate("bad-key", "prompt") }.exceptionOrNull()

    assertTrue(error is AuthenticationFailed)
    assertFalse(error?.message.orEmpty().contains("secret remote details"))
}

@Test
fun `maps 429 to rate limited`() = runTest {
    server.enqueue(MockResponse(code = 429, body = "do not expose"))
    assertTrue(runCatching { client.generate("key", "prompt") }.exceptionOrNull() is RateLimited)
}

@Test
fun `maps malformed success body to invalid provider response`() = runTest {
    server.enqueue(MockResponse(body = "not-json"))
    assertTrue(
        runCatching { client.generate("key", "prompt") }.exceptionOrNull() is InvalidProviderResponse
    )
}
```

Update existing success/header tests to call `client.generate(...)`.

- [ ] **Step 2: Run the Claude client tests and confirm compilation or assertion failures**

Run: `./gradlew testDebugUnitTest --tests '*ClaudeApiClientTest'`

Expected: FAIL because `ClaudeApiClient` does not implement `PracticeAiProvider` and still exposes raw bodies.

- [ ] **Step 3: Refactor ClaudeApiClient to the common provider interface**

Keep the existing serializable request/response DTOs and replace the Claude-specific interface/exception with:

```kotlin
class ClaudeApiClient(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://api.anthropic.com/v1/messages"
) : PracticeAiProvider {
    override val provider = AiProvider.CLAUDE
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generate(apiKey: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val body = json.encodeToString(
                ClaudeRequestBody(messages = listOf(ClaudeMessage("user", prompt)))
            )
            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()
            val response = try {
                httpClient.newCall(request).execute()
            } catch (e: IOException) {
                throw NetworkFailure(provider, e)
            }
            response.use {
                val bodyText = it.body?.string().orEmpty()
                when (it.code) {
                    401 -> throw AuthenticationFailed(provider)
                    429 -> throw RateLimited(provider)
                }
                if (!it.isSuccessful) throw ProviderFailure(provider, it.code)
                val parsed = try {
                    json.decodeFromString<ClaudeResponseBody>(bodyText)
                } catch (e: SerializationException) {
                    throw InvalidProviderResponse(provider, e)
                }
                parsed.content.firstOrNull { block -> block.type == "text" }?.text
                    ?.takeIf(String::isNotBlank)
                    ?: throw InvalidProviderResponse(provider)
            }
        }
}
```

Import `java.io.IOException` and `kotlinx.serialization.SerializationException`. Do not catch `CancellationException` or broad `Exception` around the coroutine.

- [ ] **Step 4: Run Claude tests and the existing repository tests**

Before running, keep the intermediate tree compilable by changing `ClaudePracticeRepository`'s constructor from `ClaudePromptSender` to `PracticeAiProvider`, changing both `sender.sendPrompt(...)` calls to `sender.generate(...)`, and changing `FakeClaudePromptSender` in `PracticeRepositoryTest` to implement this contract:

```kotlin
private class FakeClaudeProvider(
    private val responses: List<Result<String>>
) : PracticeAiProvider {
    constructor(response: Result<String>) : this(listOf(response))
    override val provider = AiProvider.CLAUDE
    var lastPrompt: String? = null
    var callCount = 0

    override suspend fun generate(apiKey: String, prompt: String): String {
        lastPrompt = prompt
        val result = responses.getOrElse(callCount) { responses.last() }
        callCount += 1
        return result.getOrThrow()
    }
}
```

Rename construction sites from `FakeClaudePromptSender(...)` to `FakeClaudeProvider(...)` and delete `ClaudePromptSender`.

Run: `./gradlew testDebugUnitTest --tests '*ClaudeApiClientTest' --tests '*PracticeRepositoryTest'`

Expected: PASS.

- [ ] **Step 5: Commit the Claude provider refactor**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/ClaudeApiClient.kt app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt app/src/test/java/com/example/myapplication/data/remote/ClaudeApiClientTest.kt app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt
git commit -m "Normalize Claude provider errors"
```

---

### Task 3: OpenAI Responses API Provider

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/OpenAiApiClient.kt`
- Create: `app/src/test/java/com/example/myapplication/data/remote/OpenAiApiClientTest.kt`

**Interfaces:**
- Consumes: `PracticeAiProvider`, `AiProvider.OPENAI`, and normalized exceptions from Task 1.
- Produces: `OpenAiApiClient(httpClient, baseUrl) : PracticeAiProvider` using fixed model `gpt-5.6-terra`.

- [ ] **Step 1: Write MockWebServer tests for request shape and output extraction**

```kotlin
class OpenAiApiClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OpenAiApiClient

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        client = OpenAiApiClient(OkHttpClient(), server.url("/v1/responses").toString())
    }

    @After fun tearDown() = server.close()

    @Test
    fun `sends bearer key fixed model and prompt`() = runTest {
        server.enqueue(MockResponse(body = SUCCESS_BODY))
        client.generate("openai-secret", "OPIc prompt")
        val request = server.takeRequest()
        assertEquals("Bearer openai-secret", request.headers["Authorization"])
        val body = request.body?.utf8().orEmpty()
        assertTrue(body.contains("gpt-5.6-terra"))
        assertTrue(body.contains("OPIc prompt"))
    }

    @Test
    fun `joins output text across message content and ignores other items`() = runTest {
        server.enqueue(MockResponse(body = SUCCESS_BODY))
        assertEquals("first\nsecond", client.generate("key", "prompt"))
    }

    @Test
    fun `missing output text is invalid provider response`() = runTest {
        server.enqueue(MockResponse(body = """{"output":[{"type":"reasoning"}]}"""))
        assertTrue(
            runCatching { client.generate("key", "prompt") }.exceptionOrNull()
                is InvalidProviderResponse
        )
    }

    companion object {
        private val SUCCESS_BODY = """
            {"output":[
              {"type":"reasoning"},
              {"type":"message","content":[
                {"type":"output_text","text":"first"},
                {"type":"refusal","refusal":"ignored"},
                {"type":"output_text","text":"second"}
              ]}
            ]}
        """.trimIndent()
    }
}
```

Also add 401, 429, 500-with-secret-body, malformed-JSON, and `IOException` tests matching the error classes from Task 1.

- [ ] **Step 2: Run the OpenAI tests and verify missing-class failure**

Run: `./gradlew testDebugUnitTest --tests '*OpenAiApiClientTest'`

Expected: compilation failure for unresolved `OpenAiApiClient`.

- [ ] **Step 3: Implement request serialization and flexible output scanning**

```kotlin
@Serializable
private data class OpenAiRequestBody(val model: String, val input: String)

@Serializable
private data class OpenAiResponseBody(val output: List<OpenAiOutputItem> = emptyList())

@Serializable
private data class OpenAiOutputItem(
    val type: String,
    val content: List<OpenAiContentItem> = emptyList()
)

@Serializable
private data class OpenAiContentItem(val type: String, val text: String = "")

class OpenAiApiClient(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://api.openai.com/v1/responses"
) : PracticeAiProvider {
    override val provider = AiProvider.OPENAI
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generate(apiKey: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val payload = json.encodeToString(OpenAiRequestBody("gpt-5.6-terra", prompt))
            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(payload.toRequestBody("application/json".toMediaType()))
                .build()
            val response = try {
                httpClient.newCall(request).execute()
            } catch (e: IOException) {
                throw NetworkFailure(provider, e)
            }
            response.use {
                val bodyText = it.body?.string().orEmpty()
                when (it.code) {
                    401 -> throw AuthenticationFailed(provider)
                    429 -> throw RateLimited(provider)
                }
                if (!it.isSuccessful) throw ProviderFailure(provider, it.code)
                val parsed = try {
                    json.decodeFromString<OpenAiResponseBody>(bodyText)
                } catch (e: SerializationException) {
                    throw InvalidProviderResponse(provider, e)
                }
                parsed.output.asSequence()
                    .filter { item -> item.type == "message" }
                    .flatMap { item -> item.content.asSequence() }
                    .filter { content -> content.type == "output_text" }
                    .map { content -> content.text }
                    .filter(String::isNotBlank)
                    .joinToString("\n")
                    .takeIf(String::isNotBlank)
                    ?: throw InvalidProviderResponse(provider)
            }
        }
}
```

- [ ] **Step 4: Run all provider client tests**

Run: `./gradlew testDebugUnitTest --tests '*ApiClientTest'`

Expected: PASS for Claude and OpenAI request, extraction, and sanitized error cases.

- [ ] **Step 5: Commit the OpenAI provider**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/OpenAiApiClient.kt app/src/test/java/com/example/myapplication/data/remote/OpenAiApiClientTest.kt
git commit -m "Add OpenAI Responses API provider"
```

---

### Task 4: Provider-Scoped Encrypted Settings

**Files:**
- Delete: `app/src/main/java/com/example/myapplication/data/settings/ApiKeyStore.kt`
- Delete: `app/src/main/java/com/example/myapplication/data/settings/EncryptedApiKeyStore.kt`
- Create: `app/src/main/java/com/example/myapplication/data/settings/AiSettingsStore.kt`
- Create: `app/src/main/java/com/example/myapplication/data/settings/EncryptedAiSettingsStore.kt`
- Replace: `app/src/androidTest/java/com/example/myapplication/data/settings/EncryptedApiKeyStoreTest.kt`
- With: `app/src/androidTest/java/com/example/myapplication/data/settings/EncryptedAiSettingsStoreTest.kt`

**Interfaces:**
- Consumes: `AiProvider` from Task 1 and the existing encrypted preference file `opic_practice_secure_prefs`.
- Produces: `AiSettingsStore.getSelectedProvider()`, `setSelectedProvider(provider)`, `getApiKey(provider)`, `setApiKey(provider, key)`, and `clearApiKey(provider)`.

- [ ] **Step 1: Write encrypted-store instrumentation tests**

```kotlin
@RunWith(AndroidJUnit4::class)
class EncryptedAiSettingsStoreTest {
    private lateinit var store: EncryptedAiSettingsStore

    @Before fun setUp() {
        store = EncryptedAiSettingsStore(ApplicationProvider.getApplicationContext())
        AiProvider.entries.forEach(store::clearApiKey)
        store.setSelectedProvider(AiProvider.CLAUDE)
    }

    @After fun tearDown() {
        AiProvider.entries.forEach(store::clearApiKey)
        store.setSelectedProvider(AiProvider.CLAUDE)
    }

    @Test fun defaultsToClaude() = assertEquals(AiProvider.CLAUDE, store.getSelectedProvider())

    @Test fun storesKeysIndependently() {
        store.setApiKey(AiProvider.CLAUDE, "claude-key")
        store.setApiKey(AiProvider.OPENAI, "openai-key")
        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
        assertEquals("openai-key", store.getApiKey(AiProvider.OPENAI))
    }

    @Test fun clearingOpenAiDoesNotClearClaude() {
        store.setApiKey(AiProvider.CLAUDE, "claude-key")
        store.setApiKey(AiProvider.OPENAI, "openai-key")
        store.clearApiKey(AiProvider.OPENAI)
        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
        assertNull(store.getApiKey(AiProvider.OPENAI))
    }
}
```

Add a compatibility test that writes `anthropic_api_key` directly into the same encrypted preferences and verifies `getApiKey(CLAUDE)` returns it. Provide an `internal` constructor accepting `SharedPreferences` so this test does not duplicate encryption initialization details.

- [ ] **Step 2: Run the instrumentation class and confirm missing-type failure**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.settings.EncryptedAiSettingsStoreTest`

Expected: compilation failure for unresolved `EncryptedAiSettingsStore` and `AiSettingsStore`.

- [ ] **Step 3: Define the settings contract**

```kotlin
interface AiSettingsStore {
    fun getSelectedProvider(): AiProvider
    fun setSelectedProvider(provider: AiProvider)
    fun getApiKey(provider: AiProvider): String?
    fun setApiKey(provider: AiProvider, apiKey: String)
    fun clearApiKey(provider: AiProvider)
}
```

- [ ] **Step 4: Implement encrypted provider-scoped storage with legacy fallback**

Use these exact preference keys:

```kotlin
private const val PREFS_FILE_NAME = "opic_practice_secure_prefs"
private const val KEY_SELECTED_PROVIDER = "selected_ai_provider"
private const val KEY_LEGACY_CLAUDE_API_KEY = "anthropic_api_key"
private const val KEY_OPENAI_API_KEY = "openai_api_key"

class EncryptedAiSettingsStore internal constructor(
    private val prefs: SharedPreferences
) : AiSettingsStore {
    constructor(context: Context) : this(createEncryptedPrefs(context))

    override fun getSelectedProvider(): AiProvider =
        prefs.getString(KEY_SELECTED_PROVIDER, null)
            ?.let { stored -> AiProvider.entries.firstOrNull { it.name == stored } }
            ?: AiProvider.CLAUDE

    override fun setSelectedProvider(provider: AiProvider) {
        prefs.edit().putString(KEY_SELECTED_PROVIDER, provider.name).apply()
    }

    override fun getApiKey(provider: AiProvider): String? =
        prefs.getString(keyFor(provider), null)

    override fun setApiKey(provider: AiProvider, apiKey: String) {
        prefs.edit().putString(keyFor(provider), apiKey).apply()
    }

    override fun clearApiKey(provider: AiProvider) {
        prefs.edit().remove(keyFor(provider)).apply()
    }

    private fun keyFor(provider: AiProvider) = when (provider) {
        AiProvider.CLAUDE -> KEY_LEGACY_CLAUDE_API_KEY
        AiProvider.OPENAI -> KEY_OPENAI_API_KEY
    }
}
```

Move the existing `MasterKey` and `EncryptedSharedPreferences.create` code into `createEncryptedPrefs(context)` and keep the same preference filename and encryption schemes.

- [ ] **Step 5: Run the encrypted-store instrumentation tests**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.settings.EncryptedAiSettingsStoreTest`

Expected: PASS, including default-Claude and legacy-key compatibility.

- [ ] **Step 6: Commit the settings storage change**

```bash
git add app/src/main/java/com/example/myapplication/data/settings app/src/androidTest/java/com/example/myapplication/data/settings
git commit -m "Store AI provider keys independently"
```

---

### Task 5: Selected-Provider Repository Routing and Retry

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt`

**Interfaces:**
- Consumes: `AiSettingsStore`, a `Map<AiProvider, PracticeAiProvider>`, `buildSetPrompt`, and `parsePracticeSet`.
- Produces: `PracticeRepository.generateSet(category, alreadyAskedQuestions, questionCount)` with no API-key parameter and `DefaultPracticeRepository` routing implementation.

- [ ] **Step 1: Replace repository tests with provider-routing tests**

Define focused fakes:

```kotlin
private class FakeAiSettingsStore(
    private var selected: AiProvider,
    private val keys: MutableMap<AiProvider, String> = mutableMapOf()
) : AiSettingsStore {
    override fun getSelectedProvider() = selected
    override fun setSelectedProvider(provider: AiProvider) { selected = provider }
    override fun getApiKey(provider: AiProvider) = keys[provider]
    override fun setApiKey(provider: AiProvider, apiKey: String) { keys[provider] = apiKey }
    override fun clearApiKey(provider: AiProvider) { keys.remove(provider) }
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
```

Add these routing tests, using `validResponseJson` from the existing suite and a `repository(settings, claude, openAi)` helper that constructs `DefaultPracticeRepository(setOf(claude, openAi), settings)`:

```kotlin
@Test fun `calls only selected OpenAI provider with its key`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.OPENAI,
        mutableMapOf(AiProvider.OPENAI to "openai-key"))
    val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
    val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))
    val result = repository(settings, claude, openAi).generateSet(
        PracticeCategory.HOUSING, emptyList(), 1)
    assertTrue(result.isSuccess)
    assertEquals(0, claude.callCount)
    assertEquals(1, openAi.callCount)
    assertEquals("openai-key", openAi.lastKey)
}

@Test fun `missing selected provider key fails without network call`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.OPENAI)
    val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
    val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))
    val error = repository(settings, claude, openAi).generateSet(
        PracticeCategory.HOUSING, emptyList(), 1).exceptionOrNull()
    assertTrue(error is MissingApiKey)
    assertEquals(AiProvider.OPENAI, (error as MissingApiKey).provider)
    assertEquals(0, claude.callCount)
    assertEquals(0, openAi.callCount)
}

@Test fun `retries malformed JSON once on same provider`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.OPENAI,
        mutableMapOf(AiProvider.OPENAI to "key"))
    val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
    val openAi = FakeProvider(AiProvider.OPENAI,
        listOf(Result.success("not json"), Result.success(validResponseJson)))
    assertTrue(repository(settings, claude, openAi).generateSet(
        PracticeCategory.HOUSING, emptyList(), 1).isSuccess)
    assertEquals(0, claude.callCount)
    assertEquals(2, openAi.callCount)
}

@Test fun `second malformed JSON becomes InvalidPracticeSet`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.OPENAI,
        mutableMapOf(AiProvider.OPENAI to "key"))
    val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
    val openAi = FakeProvider(AiProvider.OPENAI,
        listOf(Result.success("bad"), Result.success("still bad")))
    val error = repository(settings, claude, openAi).generateSet(
        PracticeCategory.HOUSING, emptyList(), 1).exceptionOrNull()
    assertTrue(error is InvalidPracticeSet)
    assertEquals(2, openAi.callCount)
}

@Test fun `provider failure is not retried`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.OPENAI,
        mutableMapOf(AiProvider.OPENAI to "key"))
    val claude = FakeProvider(AiProvider.CLAUDE, listOf(Result.success(validResponseJson)))
    val openAi = FakeProvider(AiProvider.OPENAI,
        listOf(Result.failure(RateLimited(AiProvider.OPENAI))))
    val error = repository(settings, claude, openAi).generateSet(
        PracticeCategory.HOUSING, emptyList(), 1).exceptionOrNull()
    assertTrue(error is RateLimited)
    assertEquals(1, openAi.callCount)
}

@Test fun `cancellation is rethrown`() = runTest {
    val settings = FakeAiSettingsStore(AiProvider.CLAUDE,
        mutableMapOf(AiProvider.CLAUDE to "key"))
    val claude = FakeProvider(AiProvider.CLAUDE,
        listOf(Result.failure(CancellationException("cancel"))))
    val openAi = FakeProvider(AiProvider.OPENAI, listOf(Result.success(validResponseJson)))
    assertFailsWith<CancellationException> {
        repository(settings, claude, openAi).generateSet(
            PracticeCategory.HOUSING, emptyList(), 1)
    }
}
```

Retain prompt-category and parsed-success assertions from the old test suite.

- [ ] **Step 2: Run the repository tests and confirm signature/behavior failures**

Run: `./gradlew testDebugUnitTest --tests '*PracticeRepositoryTest'`

Expected: FAIL because the old repository requires an API key and only accepts a Claude sender.

- [ ] **Step 3: Implement provider-neutral repository routing**

```kotlin
interface PracticeRepository {
    suspend fun generateSet(
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int = 5
    ): Result<List<PracticeQuestion>>
}

class DefaultPracticeRepository(
    providers: Set<PracticeAiProvider>,
    private val settingsStore: AiSettingsStore
) : PracticeRepository {
    private val providersById = providers.associateBy(PracticeAiProvider::provider)

    override suspend fun generateSet(
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = try {
        val selected = settingsStore.getSelectedProvider()
        val key = settingsStore.getApiKey(selected)?.takeIf(String::isNotBlank)
            ?: throw MissingApiKey(selected)
        val provider = checkNotNull(providersById[selected]) {
            "Provider not configured: ${selected.name}"
        }
        val prompt = buildSetPrompt(category, alreadyAskedQuestions, questionCount)
        val first = provider.generate(key, prompt)
        val questions = try {
            parsePracticeSet(first, category)
        } catch (firstError: PracticeSetParseException) {
            try {
                parsePracticeSet(provider.generate(key, prompt), category)
            } catch (secondError: PracticeSetParseException) {
                throw InvalidPracticeSet(secondError)
            }
        }
        Result.success(questions)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

The `checkNotNull` message contains only an enum name, never credentials or remote data.

- [ ] **Step 4: Run repository and provider tests**

Run: `./gradlew testDebugUnitTest --tests '*PracticeRepositoryTest' --tests '*ApiClientTest'`

Expected: PASS; routing tests show no cross-provider calls and exact retry counts.

- [ ] **Step 5: Commit repository routing**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt
git commit -m "Route practice generation by selected provider"
```

---

### Task 6: Provider-Aware Settings UI

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/ui/settings/SettingsUiState.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/settings/SettingsViewModel.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/settings/SettingsViewModelTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/settings/SettingsScreenTest.kt`

**Interfaces:**
- Consumes: `AiSettingsStore` and `AiProvider`.
- Produces: provider selection, provider-specific registered state, save/delete operations, and no plaintext hydration.

- [ ] **Step 1: Write ViewModel tests for provider-safe state transitions**

Use the `FakeAiSettingsStore` contract from Task 5 and assert:

```kotlin
@Test fun `initial state reports key registration without exposing stored key`() {
    val store = FakeAiSettingsStore(AiProvider.CLAUDE, mutableMapOf(AiProvider.CLAUDE to "secret"))
    val vm = SettingsViewModel(store)
    assertEquals("", vm.uiState.value.apiKeyInput)
    assertTrue(vm.uiState.value.hasStoredKey)
}

@Test fun `changing provider clears plaintext input and refreshes registration`() {
    val store = FakeAiSettingsStore(AiProvider.CLAUDE, mutableMapOf(AiProvider.OPENAI to "openai-key"))
    val vm = SettingsViewModel(store)
    vm.onApiKeyChanged("unsaved secret")
    vm.selectProvider(AiProvider.OPENAI)
    assertEquals(AiProvider.OPENAI, vm.uiState.value.selectedProvider)
    assertEquals("", vm.uiState.value.apiKeyInput)
    assertTrue(vm.uiState.value.hasStoredKey)
}

@Test fun `save trims key only for selected provider and clears input`() {
    val store = FakeAiSettingsStore(AiProvider.OPENAI)
    val vm = SettingsViewModel(store)
    vm.onApiKeyChanged("  openai-key  ")
    vm.saveApiKey()
    assertEquals("openai-key", store.getApiKey(AiProvider.OPENAI))
    assertNull(store.getApiKey(AiProvider.CLAUDE))
    assertEquals("", vm.uiState.value.apiKeyInput)
    assertEquals(AiProvider.OPENAI, vm.uiState.value.savedProvider)
}

@Test fun `clear removes only selected provider key`() {
    val store = FakeAiSettingsStore(AiProvider.OPENAI, mutableMapOf(
        AiProvider.CLAUDE to "claude-key", AiProvider.OPENAI to "openai-key"))
    val vm = SettingsViewModel(store)
    vm.clearApiKey()
    assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
    assertNull(store.getApiKey(AiProvider.OPENAI))
    assertFalse(vm.uiState.value.hasStoredKey)
}
```

- [ ] **Step 2: Run ViewModel tests and verify they fail against the old API**

Run: `./gradlew testDebugUnitTest --tests '*SettingsViewModelTest'`

Expected: compilation failures for provider-aware fields and methods.

- [ ] **Step 3: Implement provider-aware SettingsUiState and ViewModel**

```kotlin
data class SettingsUiState(
    val selectedProvider: AiProvider = AiProvider.CLAUDE,
    val apiKeyInput: String = "",
    val hasStoredKey: Boolean = false,
    val savedProvider: AiProvider? = null
)

class SettingsViewModel(private val settingsStore: AiSettingsStore) : ViewModel() {
    private val initialProvider = settingsStore.getSelectedProvider()
    private val _uiState = MutableStateFlow(stateFor(initialProvider))
    val uiState = _uiState.asStateFlow()

    fun selectProvider(provider: AiProvider) {
        settingsStore.setSelectedProvider(provider)
        _uiState.value = stateFor(provider)
    }

    fun onApiKeyChanged(value: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = value, savedProvider = null)
    }

    fun saveApiKey() {
        val state = _uiState.value
        val key = state.apiKeyInput.trim().takeIf(String::isNotBlank) ?: return
        settingsStore.setApiKey(state.selectedProvider, key)
        _uiState.value = state.copy(apiKeyInput = "", hasStoredKey = true,
            savedProvider = state.selectedProvider)
    }

    fun clearApiKey() {
        val provider = _uiState.value.selectedProvider
        settingsStore.clearApiKey(provider)
        _uiState.value = stateFor(provider)
    }

    private fun stateFor(provider: AiProvider) = SettingsUiState(
        selectedProvider = provider,
        hasStoredKey = !settingsStore.getApiKey(provider).isNullOrBlank()
    )
}
```

- [ ] **Step 4: Write Compose tests for selection, save state, and deletion isolation**

Add tests using stable tags `providerClaude`, `providerOpenAi`, `apiKeyInput`, and `clearApiKey`:

```kotlin
@Test fun changingProvider_clearsInputAndShowsRegisteredState() {
    val store = FakeAiSettingsStore(AiProvider.CLAUDE, mutableMapOf(AiProvider.OPENAI to "secret"))
    val vm = SettingsViewModel(store)
    composeTestRule.setContent { SettingsScreen(vm) }
    composeTestRule.onNodeWithTag("apiKeyInput").performTextInput("plaintext")
    composeTestRule.onNodeWithTag("providerOpenAi").performClick()
    composeTestRule.onNodeWithTag("apiKeyInput").assertTextEquals("")
    composeTestRule.onNodeWithText("OpenAI API 키가 등록되어 있습니다.").assertExists()
}
```

Add a save test expecting `OpenAI API 키를 저장했습니다.` and a delete test expecting `OpenAI API 키가 등록되어 있지 않습니다.` while the fake store still contains the Claude key.

- [ ] **Step 5: Implement the provider selector and key status UI**

In `SettingsScreen`, use two Material 3 `FilterChip`s bound to `selectProvider`; give them the stable tags above. Render:

```kotlin
Text("${uiState.selectedProvider.displayName} API 키")
Text(
    if (uiState.hasStoredKey) {
        "${uiState.selectedProvider.displayName} API 키가 등록되어 있습니다."
    } else {
        "${uiState.selectedProvider.displayName} API 키가 등록되어 있지 않습니다."
    }
)
OutlinedTextField(
    value = uiState.apiKeyInput,
    onValueChange = viewModel::onApiKeyChanged,
    visualTransformation = PasswordVisualTransformation(),
    modifier = Modifier.fillMaxWidth().testTag("apiKeyInput")
)
if (uiState.hasStoredKey) {
    Button(onClick = viewModel::clearApiKey, modifier = Modifier.testTag("clearApiKey")) {
        Text("API 키 삭제")
    }
}
uiState.savedProvider?.let { Text("${it.displayName} API 키를 저장했습니다.") }
```

Never place the stored key into `apiKeyInput`.

- [ ] **Step 6: Run settings unit and Compose tests**

Run: `./gradlew testDebugUnitTest --tests '*SettingsViewModelTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.settings.SettingsScreenTest`

Expected: PASS.

- [ ] **Step 7: Commit the settings UI**

```bash
git add app/src/main/java/com/example/myapplication/ui/settings app/src/test/java/com/example/myapplication/ui/settings app/src/androidTest/java/com/example/myapplication/ui/settings
git commit -m "Add AI provider settings UI"
```

---

### Task 7: Practice Error UX, Navigation, and Dependency Wiring

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetUiState.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetViewModel.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeScreen.kt`
- Modify: `app/src/main/java/com/example/myapplication/di/AppContainer.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt`

**Interfaces:**
- Consumes: provider-neutral `PracticeRepository`, `AiSettingsStore`, `ClaudeApiClient`, and `OpenAiApiClient`.
- Produces: safe actionable practice errors, settings navigation for missing/authentication errors, and fully wired runtime selection.

- [ ] **Step 1: Rewrite ViewModel tests for the repository-owned key lookup**

Remove `ApiKeyStore` from ViewModel test construction and change the fake repository signature to:

```kotlin
override suspend fun generateSet(
    category: PracticeCategory,
    alreadyAskedQuestions: List<String>,
    questionCount: Int
): Result<List<PracticeQuestion>> = result
```

Add exact error-state assertions:

```kotlin
@Test fun `missing key error offers settings action`() = runTest {
    val vm = buildViewModel(Result.failure(MissingApiKey(AiProvider.OPENAI)))
    advanceUntilIdle()
    assertEquals(
        PracticeSetUiState.Error("OpenAI API 키가 없습니다.", showSettingsAction = true),
        vm.uiState.value
    )
}

@Test fun `rate limit error offers retry but not settings`() = runTest {
    val vm = buildViewModel(Result.failure(RateLimited(AiProvider.OPENAI)))
    advanceUntilIdle()
    assertEquals(false, (vm.uiState.value as PracticeSetUiState.Error).showSettingsAction)
}
```

Also assert `AuthenticationFailed` shows settings, while network/provider/invalid-response/invalid-set errors do not.

- [ ] **Step 2: Run ViewModel tests and confirm old dependencies/state fail**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetViewModelTest'`

Expected: compilation failures around `ApiKeyStore`, the repository signature, and `showSettingsAction`.

- [ ] **Step 3: Make PracticeSetViewModel map typed failures to safe UI state**

Change the error state and remove settings storage from the ViewModel constructor:

```kotlin
data class Error(
    val message: String,
    val showSettingsAction: Boolean = false
) : PracticeSetUiState
```

Call the repository without a key and map errors:

```kotlin
practiceRepository.generateSet(
    category = category,
    alreadyAskedQuestions = emptyList()
).onSuccess { questions ->
    _uiState.value = PracticeSetUiState.Ready(
        questions = questions,
        currentIndex = 0,
        isRecording = false,
        hasRecording = false,
        isCurrentFavorite = favoriteRepository.isFavorite(questions.first()),
        isSetComplete = false
    )
}
 .onFailure { error ->
    _uiState.value = PracticeSetUiState.Error(
        message = error.message ?: "문제를 생성하지 못했습니다.",
        showSettingsAction = error is MissingApiKey || error is AuthenticationFailed
    )
}
```

- [ ] **Step 4: Add and implement the settings navigation action in Compose**

Write a `PracticeScreenTest` that supplies `onOpenSettings = { opened = true }`, injects a missing-key failure, taps `설정으로 이동`, and asserts `opened`. Then extend the screen signature and error branch:

```kotlin
fun PracticeScreen(
    viewModel: PracticeSetViewModel,
    onSetComplete: (Int) -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    // existing recording parameters
)

is PracticeSetUiState.Error -> {
    Text(state.message)
    if (state.showSettingsAction) {
        Button(onClick = onOpenSettings) { Text("설정으로 이동") }
    }
    Button(onClick = viewModel::loadSet) { Text("다시 시도") }
}
```

- [ ] **Step 5: Wire providers, settings, repository, and navigation in AppContainer**

```kotlin
val aiSettingsStore: AiSettingsStore = EncryptedAiSettingsStore(appContext)

val practiceRepository: PracticeRepository = DefaultPracticeRepository(
    providers = setOf(ClaudeApiClient(), OpenAiApiClient()),
    settingsStore = aiSettingsStore
)
```

In `AppNavGraph`, pass `aiSettingsStore` to `SettingsViewModel`, remove it from `PracticeSetViewModel`, and add:

```kotlin
onOpenSettings = {
    navController.navigate(Routes.Settings.route) { launchSingleTop = true }
}
```

- [ ] **Step 6: Run ViewModel, screen, and navigation compilation tests**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetViewModelTest' --tests '*SettingsViewModelTest' --tests '*PracticeRepositoryTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest`

Expected: PASS.

- [ ] **Step 7: Commit runtime integration**

```bash
git add app/src/main/java/com/example/myapplication/di/AppContainer.kt app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt app/src/main/java/com/example/myapplication/ui/set app/src/test/java/com/example/myapplication/ui/set app/src/androidTest/java/com/example/myapplication/ui/set
git commit -m "Integrate selectable AI providers"
```

---

### Task 8: Full Regression, Security Audit, and Build Verification

**Files:**
- Modify only files implicated by concrete failures from the commands below.

**Interfaces:**
- Consumes: the complete Claude/OpenAI implementation from Tasks 1–7.
- Produces: verified debug app with passing JVM, instrumentation, build, lint, and secret-exposure checks.

- [ ] **Step 1: Run the complete JVM suite**

Run: `./gradlew testDebugUnitTest`

Expected: all unit tests PASS.

- [ ] **Step 2: Run the complete Android instrumentation suite**

Run: `./gradlew connectedDebugAndroidTest`

Expected: all instrumentation and Compose tests PASS on the connected emulator/device.

- [ ] **Step 3: Build the debug APK and run lint**

Run: `./gradlew assembleDebug lintDebug`

Expected: `BUILD SUCCESSFUL`, lint error count 0. Existing dependency/deprecation warnings may remain; introduce no new error.

- [ ] **Step 4: Audit source and test artifacts for accidental secrets or raw-body exceptions**

Run:

```bash
rg -n 'HTTP.*bodyText|responseBody|Claude API returned.*\$bodyText|openai-secret|claude-key|sk-ant-test' app/src/main
```

Expected: no matches in production sources. Test-only dummy keys remain confined to `app/src/test` and `app/src/androidTest`.

- [ ] **Step 5: Review the final diff for scope and migration safety**

Run: `git diff --check HEAD~7..HEAD && git status --short`

Expected: no whitespace errors; only planned source/tests/docs plus the pre-existing untracked `.gstack/` tooling directory appear. Confirm there is no Room schema change and `anthropic_api_key` remains the Claude key.
