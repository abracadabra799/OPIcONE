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

    override suspend fun generate(apiKey: String?, prompt: String): String = withContext(Dispatchers.IO) {
        val validKey = apiKey?.takeIf(String::isNotBlank) ?: throw MissingApiKey(provider)
        val payload = json.encodeToString(OpenAiRequestBody("gpt-5.6-terra", prompt))
        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $validKey")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                val bodyText = response.body?.string().orEmpty()
                when (response.code) {
                    401 -> throw AuthenticationFailed(provider)
                    429 -> throw RateLimited(provider)
                }
                if (!response.isSuccessful) throw ProviderFailure(provider, response.code)

                val parsed = try {
                    json.decodeFromString<OpenAiResponseBody>(bodyText)
                } catch (e: SerializationException) {
                    throw InvalidProviderResponse(provider)
                }
                parsed.output.asSequence()
                    .filter { it.type == "message" }
                    .flatMap { it.content.asSequence() }
                    .filter { it.type == "output_text" }
                    .map { it.text }
                    .filter(String::isNotBlank)
                    .joinToString("\n")
                    .takeIf(String::isNotBlank)
                    ?: throw InvalidProviderResponse(provider)
            }
        } catch (e: IOException) {
            throw NetworkFailure(provider, e)
        }
    }
}
