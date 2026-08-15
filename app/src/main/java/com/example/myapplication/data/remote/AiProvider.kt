package com.example.myapplication.data.remote

enum class AiProvider(val displayName: String) {
    CLAUDE("Claude"),
    OPENAI("OpenAI")
}

interface PracticeAiProvider {
    val provider: AiProvider
    suspend fun generate(apiKey: String, prompt: String): String
}

sealed class PracticeGenerationException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

class MissingApiKey(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} API 키가 없습니다."
)

class AuthenticationFailed(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} API 키를 확인해주세요."
)

class RateLimited(val provider: AiProvider) : PracticeGenerationException(
    "${provider.displayName} 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요."
)

class NetworkFailure(val provider: AiProvider, cause: Throwable) : PracticeGenerationException(
    "네트워크 연결을 확인해주세요.", cause
)

class ProviderFailure(val provider: AiProvider, val statusCode: Int) : PracticeGenerationException(
    "${provider.displayName} 요청에 실패했습니다. (HTTP $statusCode)"
)

class InvalidProviderResponse(val provider: AiProvider, cause: Throwable? = null) :
    PracticeGenerationException("${provider.displayName} 응답을 읽지 못했습니다.", cause)

class InvalidPracticeSet(cause: Throwable) : PracticeGenerationException(
    "생성된 문제 형식이 올바르지 않습니다.", cause
)

class InsufficientUniqueQuestions : PracticeGenerationException(
    "서로 다른 문제를 충분히 만들지 못했습니다. 다시 시도해주세요."
)
