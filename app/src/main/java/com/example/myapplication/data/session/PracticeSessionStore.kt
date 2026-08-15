package com.example.myapplication.data.session

import com.example.myapplication.data.model.PracticeQuestion
import java.util.Locale

data class CompletedPracticeItem(
    val question: PracticeQuestion,
    val isFavorite: Boolean
)

data class CompletedPracticeSet(val items: List<CompletedPracticeItem>)

class PracticeSessionStore {
    private val normalizedAsked = linkedSetOf<String>()
    private var completed: CompletedPracticeSet? = null

    fun askedQuestions(): List<String> = normalizedAsked.toList()

    fun containsQuestion(question: String): Boolean =
        normalizeQuestion(question) in normalizedAsked

    fun addAskedQuestions(questions: List<String>) {
        normalizedAsked += questions.map(::normalizeQuestion)
    }

    fun saveCompletedSet(value: CompletedPracticeSet) {
        completed = value.copy(items = value.items.toList())
    }

    fun lastCompletedSet(): CompletedPracticeSet? = completed
}

internal fun normalizeQuestion(value: String): String =
    value.trim().replace(Regex("\\s+"), " ").lowercase(Locale.ROOT)
