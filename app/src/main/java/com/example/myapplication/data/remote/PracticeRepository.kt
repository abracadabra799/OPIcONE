package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion

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
    ): Result<List<PracticeQuestion>> = runCatching {
        val prompt = buildSetPrompt(category, alreadyAskedQuestions, questionCount)
        val rawResponse = sender.sendPrompt(apiKey, prompt)
        parsePracticeSet(rawResponse, category)
    }
}
