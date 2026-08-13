package com.example.myapplication.data.remote

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
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
        val body = """{"content":[{"type":"text","text":"[{\"korean_hint\":\"h\",\"english_sentence\":\"e\"}]"}]}"""
        server.enqueue(MockResponse(body = body))

        val result = client.sendPrompt(apiKey = "test-key", prompt = "prompt")

        assertEquals("[{\"korean_hint\":\"h\",\"english_sentence\":\"e\"}]", result)
    }

    @Test
    fun `sends the api key header and prompt in the request body`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[{"type":"text","text":"ok"}]}"""))

        client.sendPrompt(apiKey = "secret-key", prompt = "hello prompt")

        val recorded = server.takeRequest()
        assertEquals("secret-key", recorded.headers["x-api-key"])
        assertTrue(recorded.body?.utf8().orEmpty().contains("hello prompt"))
    }

    @Test
    fun `throws ClaudeApiException on non-2xx response`() = runTest {
        server.enqueue(MockResponse(code = 401, body = """{"error":"unauthorized"}"""))

        var thrown: ClaudeApiException? = null
        try {
            client.sendPrompt(apiKey = "bad-key", prompt = "prompt")
        } catch (e: ClaudeApiException) {
            thrown = e
        }
        assertTrue(thrown != null)
    }

    @Test
    fun `throws ClaudeApiException when response has no text block`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[]}"""))

        var thrown: ClaudeApiException? = null
        try {
            client.sendPrompt(apiKey = "test-key", prompt = "prompt")
        } catch (e: ClaudeApiException) {
            thrown = e
        }
        assertTrue(thrown != null)
    }
}
