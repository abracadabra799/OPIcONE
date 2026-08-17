package com.example.myapplication.data.remote

import com.example.myapplication.data.local.LocalOpicQuestionBank
import com.example.myapplication.data.model.PracticeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class BankQuestionDto(
    val opic_question: String,
    val korean_hint: String,
    val english_sentence: String
)

class LocalBankAiProvider : PracticeAiProvider {

    override val provider: AiProvider = AiProvider.LOCAL_BANK
    private val json = Json { prettyPrint = false }

    override suspend fun generate(apiKey: String?, prompt: String): String = withContext(Dispatchers.Default) {
        val category = resolveCategory(prompt)
        val exclusions = resolveExclusions(prompt)

        val questions = LocalOpicQuestionBank.getQuestions(
            category = category,
            count = 5,
            excludeQuestions = exclusions
        )

        val dtos = questions.map {
            BankQuestionDto(
                opic_question = it.opicQuestion,
                korean_hint = it.koreanHint,
                english_sentence = it.englishSentence
            )
        }

        json.encodeToString(dtos)
    }

    private fun resolveCategory(prompt: String): PracticeCategory {
        return PracticeCategory.entries.firstOrNull {
            prompt.contains(it.promptTopic, ignoreCase = true) || prompt.contains(it.koreanLabel)
        } ?: PracticeCategory.HOUSING
    }

    private fun resolveExclusions(prompt: String): Set<String> {
        val markers = listOf(
            "Do not repeat any of these questions already used in this session:",
            "Do NOT reuse any of these questions:"
        )
        val marker = markers.firstOrNull { prompt.contains(it, ignoreCase = true) } ?: return emptySet()
        val markerIndex = prompt.indexOf(marker)
        if (markerIndex == -1) return emptySet()

        val afterMarker = prompt.substring(markerIndex + marker.length)
        return afterMarker.lines()
            .map { it.trim().removePrefix("-").trim() }
            .filter { it.isNotBlank() && !it.startsWith("Respond with", ignoreCase = true) && !it.startsWith("Output", ignoreCase = true) }
            .toSet()
    }
}
