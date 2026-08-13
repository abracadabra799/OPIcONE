package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
private data class PracticeQuestionDto(
    val korean_hint: String,
    val english_sentence: String
)

class PracticeSetParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

private val json = Json { ignoreUnknownKeys = true }

fun parsePracticeSet(rawJson: String, category: PracticeCategory): List<PracticeQuestion> {
    val dtos = try {
        json.decodeFromString<List<PracticeQuestionDto>>(rawJson.trim())
    } catch (e: Exception) {
        throw PracticeSetParseException("Could not parse practice set JSON", e)
    }

    if (dtos.isEmpty()) {
        throw PracticeSetParseException("Practice set JSON was empty")
    }

    return dtos.map { dto ->
        if (dto.korean_hint.isBlank() || dto.english_sentence.isBlank()) {
            throw PracticeSetParseException("Practice set item had a blank field")
        }
        PracticeQuestion(
            koreanHint = dto.korean_hint,
            englishSentence = dto.english_sentence,
            category = category
        )
    }
}
