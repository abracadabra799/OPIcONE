package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.session.PracticeSessionStore
import com.example.myapplication.data.session.normalizeQuestion
import com.example.myapplication.data.settings.AiSettingsStore
import kotlinx.coroutines.CancellationException

interface PracticeRepository {
    suspend fun generateSet(
        category: PracticeCategory,
        questionCount: Int = 5
    ): Result<List<PracticeQuestion>>
}

class DefaultPracticeRepository(
    providers: Set<PracticeAiProvider>,
    private val settingsStore: AiSettingsStore,
    private val sessionStore: PracticeSessionStore
) : PracticeRepository {
    private val providersById = providers.associateBy(PracticeAiProvider::provider)

    override suspend fun generateSet(
        category: PracticeCategory,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = try {
        val selected = settingsStore.getSelectedProvider()
        val key = settingsStore.getApiKey(selected)?.takeIf(String::isNotBlank)
            ?: throw MissingApiKey(selected)
        val provider = checkNotNull(providersById[selected]) {
            "Provider not configured: ${selected.name}"
        }

        val accepted = mutableListOf<PracticeQuestion>()
        val acceptedNormalized = linkedSetOf<String>()

        repeat(MAX_GENERATION_CALLS) { attempt ->
            val promptHistory = sessionStore.askedQuestions() +
                accepted.map(PracticeQuestion::opicQuestion)
            val prompt = buildSetPrompt(category, promptHistory, questionCount)
            val response = provider.generate(key, prompt)
            val parsed = try {
                parsePracticeSet(response, category)
            } catch (error: PracticeSetParseException) {
                if (attempt == MAX_GENERATION_CALLS - 1) {
                    throw InvalidPracticeSet(error)
                }
                return@repeat
            }

            for (question in parsed) {
                val normalized = normalizeQuestion(question.opicQuestion)
                if (!sessionStore.containsQuestion(question.opicQuestion) &&
                    acceptedNormalized.add(normalized)
                ) {
                    accepted += question
                    if (accepted.size == questionCount) {
                        sessionStore.addAskedQuestions(
                            accepted.map(PracticeQuestion::opicQuestion)
                        )
                        return Result.success(accepted.toList())
                    }
                }
            }
        }

        throw InsufficientUniqueQuestions()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    private companion object {
        const val MAX_GENERATION_CALLS = 2
    }
}
