package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
private data class PracticeQuestionDto(
    val opic_question: String,
    val korean_hint: String,
    val english_sentence: String
)

class PracticeSetParseException(message: String) : Exception(message)

private val json = Json { ignoreUnknownKeys = true }

fun parsePracticeSet(rawJson: String, category: PracticeCategory): List<PracticeQuestion> {
    val dtos = try {
        json.decodeFromString<List<PracticeQuestionDto>>(rawJson.trim())
    } catch (_: Exception) {
        throw PracticeSetParseException("Could not parse practice set JSON")
    }

    if (dtos.isEmpty()) {
        throw PracticeSetParseException("Practice set JSON was empty")
    }

    return dtos.map { dto ->
        val opicQuestion = dto.opic_question.trim()
        val koreanHint = dto.korean_hint.trim()
        val englishSentence = dto.english_sentence.trim()
        if (opicQuestion.isBlank() || koreanHint.isBlank() || englishSentence.isBlank()) {
            throw PracticeSetParseException("Practice set item had a blank field")
        }
        PracticeQuestion(
            opicQuestion = opicQuestion,
            koreanHint = koreanHint,
            englishSentence = englishSentence,
            category = category
        )
    }
}
