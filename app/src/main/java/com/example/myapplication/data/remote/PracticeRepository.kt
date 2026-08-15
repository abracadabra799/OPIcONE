package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.settings.AiSettingsStore
import kotlinx.coroutines.CancellationException

interface PracticeRepository {
    suspend fun generateSet(
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int = 5
    ): Result<List<PracticeQuestion>>
}

class DefaultPracticeRepository(
    providers: Set<PracticeAiProvider>,
    private val settingsStore: AiSettingsStore
) : PracticeRepository {
    private val providersById = providers.associateBy(PracticeAiProvider::provider)

    override suspend fun generateSet(
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = try {
        val selected = settingsStore.getSelectedProvider()
        val key = settingsStore.getApiKey(selected)?.takeIf(String::isNotBlank)
            ?: throw MissingApiKey(selected)
        val provider = checkNotNull(providersById[selected]) {
            "Provider not configured: ${selected.name}"
        }
        val prompt = buildSetPrompt(category, alreadyAskedQuestions, questionCount)
        val first = provider.generate(key, prompt)
        val questions = try {
            parsePracticeSet(first, category)
        } catch (_: PracticeSetParseException) {
            try {
                parsePracticeSet(provider.generate(key, prompt), category)
            } catch (secondError: PracticeSetParseException) {
                throw InvalidPracticeSet(secondError)
            }
        }
        Result.success(questions)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
