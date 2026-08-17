package com.example.myapplication.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiProviderTest {
    @Test
    fun `provider labels and apiKey requirement settings are correct`() {
        assertEquals("내장 AL 마스터 뱅크 (Zero-API-Key)", AiProvider.LOCAL_BANK.displayName)
        assertFalse(AiProvider.LOCAL_BANK.requiresApiKey)

        assertEquals("온디바이스 LLM (Gemma/Llama)", AiProvider.ON_DEVICE_LLM.displayName)
        assertFalse(AiProvider.ON_DEVICE_LLM.requiresApiKey)

        assertEquals("Claude", AiProvider.CLAUDE.displayName)
        assertTrue(AiProvider.CLAUDE.requiresApiKey)

        assertEquals("OpenAI", AiProvider.OPENAI.displayName)
        assertTrue(AiProvider.OPENAI.requiresApiKey)
    }

    @Test
    fun `provider failure message cannot contain a remote body`() {
        val error = ProviderFailure(AiProvider.OPENAI, 500)
        assertEquals("OpenAI 요청에 실패했습니다. (HTTP 500)", error.message)
        assertFalse(error.message.orEmpty().contains("responseBody"))
    }
}
