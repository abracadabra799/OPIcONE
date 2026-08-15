package com.example.myapplication.data.remote

import java.io.IOException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.ForwardingSource
import okio.buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OpenAiApiClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OpenAiApiClient

    @Before
    fun setUp() {
        server = MockWebServer().also { it.start() }
        client = OpenAiApiClient(OkHttpClient(), server.url("/v1/responses").toString())
    }

    @After
    fun tearDown() = server.close()

    @Test
    fun `posts exact responses request shape`() = runTest {
        server.enqueue(MockResponse(body = SUCCESS_BODY))

        client.generate("openai-secret", "OPIc prompt")

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/v1/responses", request.url.encodedPath)
        assertEquals("application/json; charset=utf-8", request.headers["Content-Type"])
        assertEquals("Bearer openai-secret", request.headers["Authorization"])
        val body = Json.parseToJsonElement(request.body?.utf8().orEmpty()).jsonObject
        assertEquals(setOf("model", "input"), body.keys)
        assertEquals("gpt-5.6-terra", body.getValue("model").jsonPrimitive.content)
        assertEquals("OPIc prompt", body.getValue("input").jsonPrimitive.content)
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
            runCatching { client.generate("key", "prompt") }.exceptionOrNull() is
                InvalidProviderResponse
        )
    }

    @Test
    fun `maps 401 without exposing response body`() = runTest {
        server.enqueue(MockResponse(code = 401, body = "openai-secret remote details"))

        val error = runCatching { client.generate("bad-key", "prompt") }.exceptionOrNull()

        assertTrue(error is AuthenticationFailed)
        assertFalse(error?.message.orEmpty().contains("openai-secret remote details"))
    }

    @Test
    fun `maps 429 to rate limited`() = runTest {
        server.enqueue(MockResponse(code = 429, body = "do not expose"))

        assertTrue(runCatching { client.generate("key", "prompt") }.exceptionOrNull() is RateLimited)
    }

    @Test
    fun `maps other non-success responses without exposing response body`() = runTest {
        server.enqueue(MockResponse(code = 500, body = "openai-secret internal failure"))

        val error = runCatching { client.generate("key", "prompt") }.exceptionOrNull()

        assertTrue(error is ProviderFailure)
        assertEquals(500, (error as ProviderFailure).statusCode)
        assertFalse(error.message.orEmpty().contains("openai-secret internal failure"))
    }

    @Test
    fun `maps malformed success body without exposing its content`() = runTest {
        val secretBody = "not-json openai-secret-malformed-body"
        server.enqueue(MockResponse(body = secretBody))

        val error = runCatching { client.generate("key", "prompt") }.exceptionOrNull()

        assertTrue(error is InvalidProviderResponse)
        assertFalse(error?.message.orEmpty().contains(secretBody))
        assertTrue(error?.cause == null)
    }

    @Test
    fun `maps execution failure to network failure`() = runTest {
        val failingClient = OpenAiApiClient(
            httpClient = OkHttpClient.Builder().addInterceptor(ThrowingInterceptor).build(),
            baseUrl = server.url("/").toString()
        )

        assertTrue(
            runCatching { failingClient.generate("key", "prompt") }.exceptionOrNull() is NetworkFailure
        )
    }

    @Test
    fun `maps response body read failure to network failure`() = runTest {
        val failingClient = OkHttpClient.Builder()
            .addInterceptor { chain -> failingResponse(chain.request()) }
            .build()
        val failingResponseClient = OpenAiApiClient(failingClient, server.url("/").toString())

        assertTrue(
            runCatching { failingResponseClient.generate("key", "prompt") }.exceptionOrNull() is
                NetworkFailure
        )
    }

    private fun failingResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(object : ResponseBody() {
            override fun contentLength() = -1L
            override fun contentType() = null
            override fun source() = object : ForwardingSource(Buffer()) {
                override fun read(sink: Buffer, byteCount: Long): Long {
                    throw IOException("response stream closed")
                }
            }.buffer()
        })
        .build()

    private object ThrowingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response =
            throw IOException("connection refused")
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
