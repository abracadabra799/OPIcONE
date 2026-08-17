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
        val key = if (selected.requiresApiKey) {
            settingsStore.getApiKey(selected)?.takeIf(String::isNotBlank)
                ?: throw MissingApiKey(selected)
        } else {
            settingsStore.getApiKey(selected)
        }
        val provider = checkNotNull(providersById[selected]) {
            "Provider not configured: ${selected.name}"
        }

        val accepted = mutableListOf<PracticeQuestion>()
        val acceptedNormalized = linkedSetOf<String>()
        val fallbackCandidates = mutableListOf<PracticeQuestion>()

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
                if (acceptedNormalized.add(normalized)) {
                    if (!sessionStore.containsQuestion(question.opicQuestion)) {
                        accepted += question
                    } else {
                        fallbackCandidates += question
                    }

                    if (accepted.size == questionCount) {
                        sessionStore.addAskedQuestions(
                            accepted.map(PracticeQuestion::opicQuestion)
                        )
                        return Result.success(accepted.toList())
                    }
                }
            }
        }

        // When unseen questions in session are exhausted, fill from distinct candidates with fresh script variations
        for (fallback in fallbackCandidates) {
            if (accepted.size == questionCount) break
            accepted += fallback
        }

        if (accepted.size == questionCount) {
            sessionStore.addAskedQuestions(
                accepted.map(PracticeQuestion::opicQuestion)
            )
            Result.success(accepted.toList())
        } else {
            throw InsufficientUniqueQuestions()
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    private companion object {
        const val MAX_GENERATION_CALLS = 2
    }
}
