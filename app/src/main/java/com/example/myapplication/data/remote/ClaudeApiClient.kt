package com.example.myapplication.data.remote

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
private data class ClaudeRequestBody(
    val model: String = "claude-sonnet-5",
    val max_tokens: Int = 2048,
    val messages: List<ClaudeMessage>
)

@Serializable
private data class ClaudeMessage(val role: String, val content: String)

@Serializable
private data class ClaudeResponseBody(val content: List<ClaudeContentBlock> = emptyList())

@Serializable
private data class ClaudeContentBlock(val type: String, val text: String = "")

class ClaudeApiClient(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://api.anthropic.com/v1/messages"
) : PracticeAiProvider {

    override val provider = AiProvider.CLAUDE
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generate(apiKey: String?, prompt: String): String = withContext(Dispatchers.IO) {
        val validKey = apiKey?.takeIf(String::isNotBlank) ?: throw MissingApiKey(provider)
        val body = json.encodeToString(
            ClaudeRequestBody(messages = listOf(ClaudeMessage("user", prompt)))
        )
        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("x-api-key", validKey)
            .addHeader("anthropic-version", "2023-06-01")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        try {
            httpClient.newCall(request).execute().use {
                val bodyText = it.body?.string().orEmpty()
                when (it.code) {
                    401 -> throw AuthenticationFailed(provider)
                    429 -> throw RateLimited(provider)
                }
                if (!it.isSuccessful) throw ProviderFailure(provider, it.code)
                val parsed = try {
                    json.decodeFromString<ClaudeResponseBody>(bodyText)
                } catch (_: SerializationException) {
                    throw InvalidProviderResponse(provider)
                }
                parsed.content.firstOrNull { block -> block.type == "text" }?.text
                    ?.takeIf(String::isNotBlank)
                    ?: throw InvalidProviderResponse(provider)
            }
        } catch (e: IOException) {
            throw NetworkFailure(provider, e)
        }
    }
}
