package com.example.myapplication.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnDeviceLlmAiProvider(
    private val engine: OnDeviceLlmEngine,
    private val fallbackProvider: PracticeAiProvider = LocalBankAiProvider()
) : PracticeAiProvider {

    override val provider: AiProvider = AiProvider.ON_DEVICE_LLM

    override suspend fun generate(apiKey: String?, prompt: String): String = withContext(Dispatchers.Default) {
        if (engine.isModelReady()) {
            try {
                val output = engine.generate(prompt)
                val json = extractJsonBlock(output)
                if (json.isNotBlank() && json.startsWith("[") && json.endsWith("]")) {
                    return@withContext json
                }
            } catch (_: Exception) {
                // Fallback to curated local bank if local model fails or throws
            }
        }
        fallbackProvider.generate(apiKey, prompt)
    }

    private fun extractJsonBlock(text: String): String {
        val start = text.indexOf('[')
        val end = text.lastIndexOf(']')
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1)
        }
        return text.trim()
    }
}
