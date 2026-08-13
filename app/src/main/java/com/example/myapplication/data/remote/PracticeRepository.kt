package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.CancellationException

interface PracticeRepository {
    suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int = 5
    ): Result<List<PracticeQuestion>>
}

class ClaudePracticeRepository(
    private val sender: ClaudePromptSender
) : PracticeRepository {

    override suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> {
        return try {
            val prompt = buildSetPrompt(category, alreadyAskedQuestions, questionCount)
            val rawResponse = sender.sendPrompt(apiKey, prompt)
            Result.success(parsePracticeSet(rawResponse, category))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
