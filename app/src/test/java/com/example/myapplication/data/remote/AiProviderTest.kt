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
