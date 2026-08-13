package com.example.myapplication.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ClaudeApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

interface ClaudePromptSender {
    suspend fun sendPrompt(apiKey: String, prompt: String): String
}

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
) : ClaudePromptSender {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendPrompt(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        val requestBodyJson = json.encodeToString(
            ClaudeRequestBody(messages = listOf(ClaudeMessage(role = "user", content = prompt)))
        )

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val response = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ClaudeApiException("Network request failed", e)
        }

        response.use {
            val bodyText = it.body?.string().orEmpty()
            if (!it.isSuccessful) {
                throw ClaudeApiException("Claude API returned HTTP ${it.code}: $bodyText")
            }
            val parsed = try {
                json.decodeFromString<ClaudeResponseBody>(bodyText)
            } catch (e: Exception) {
                throw ClaudeApiException("Could not parse Claude API response", e)
            }
            parsed.content.firstOrNull { block -> block.type == "text" }?.text
                ?: throw ClaudeApiException("Claude API response had no text content")
        }
    }
}
