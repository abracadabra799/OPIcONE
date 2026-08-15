package com.example.myapplication.data.remote

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Protocol
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

class ClaudeApiClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: ClaudeApiClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = ClaudeApiClient(
            httpClient = OkHttpClient(),
            baseUrl = server.url("/v1/messages").toString()
        )
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `returns text content from a successful response`() = runTest {
        val body = """{"content":[{"type":"text","text":"[{\"opic_question\":\"Tell me about your home.\",\"korean_hint\":\"나는 산다 / 서울에\",\"english_sentence\":\"I live in Seoul.\"}]"}]}"""
        server.enqueue(MockResponse(body = body))

        val result = client.generate(apiKey = "test-key", prompt = "prompt")

        assertEquals(
            "[{\"opic_question\":\"Tell me about your home.\",\"korean_hint\":\"나는 산다 / 서울에\",\"english_sentence\":\"I live in Seoul.\"}]",
            result
        )
    }

    @Test
    fun `sends the api key header and prompt in the request body`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[{"type":"text","text":"ok"}]}"""))

        client.generate(apiKey = "secret-key", prompt = "hello prompt")

        val recorded = server.takeRequest()
        assertEquals("secret-key", recorded.headers["x-api-key"])
        assertTrue(recorded.body?.utf8().orEmpty().contains("hello prompt"))
    }

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
    fun `maps malformed success body without retaining response content in cause chain`() = runTest {
        val responseSentinel = "claude-secret-response-sentinel"
        server.enqueue(MockResponse(body = responseSentinel))

        val error = runCatching { client.generate("key", "prompt") }.exceptionOrNull()

        assertTrue(error is InvalidProviderResponse)
        val causeChainText = generateSequence(error) { it.cause }
            .joinToString("\n") { it.message.orEmpty() }
        assertFalse(causeChainText.contains(responseSentinel))
        assertTrue(error?.cause == null)
    }

    @Test
    fun `maps response body read failure to network failure`() = runTest {
        val failingClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(object : ResponseBody() {
                        override fun contentLength() = -1L
                        override fun contentType() = null
                        override fun source() = object : ForwardingSource(Buffer()) {
                            override fun read(sink: Buffer, byteCount: Long): Long {
                                throw java.io.IOException("response stream closed")
                            }
                        }.buffer()
                    })
                    .build()
            }
            .build()
        val failingResponseClient = ClaudeApiClient(failingClient, server.url("/").toString())

        assertTrue(
            runCatching { failingResponseClient.generate("key", "prompt") }.exceptionOrNull() is
                NetworkFailure
        )
    }

    @Test
    fun `maps success response without text content to invalid provider response`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[]}"""))

        assertTrue(
            runCatching { client.generate("test-key", "prompt") }.exceptionOrNull() is
                InvalidProviderResponse
        )
    }
}
