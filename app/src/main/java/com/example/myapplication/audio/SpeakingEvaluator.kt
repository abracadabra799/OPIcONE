package com.example.myapplication.audio

data class EvaluatedWord(
    val text: String,
    val isMatched: Boolean
)

data class SpeakingEvaluationResult(
    val spokenText: String,
    val accuracyScore: Int,
    val evaluatedWords: List<EvaluatedWord>,
    val feedbackMessage: String
)

object SpeakingEvaluator {

    fun evaluate(spokenText: String, targetSentence: String): SpeakingEvaluationResult {
        val trimmedSpoken = spokenText.trim()
        val targetTokens = targetSentence.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

        if (trimmedSpoken.isBlank() || targetTokens.isEmpty()) {
            return SpeakingEvaluationResult(
                spokenText = trimmedSpoken,
                accuracyScore = 0,
                evaluatedWords = targetTokens.map { EvaluatedWord(it, false) },
                feedbackMessage = "발화가 감지되지 않았습니다. 다시 녹음해보세요."
            )
        }

        val spokenNormalizedWords = trimmedSpoken.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .toSet()

        var matchedCount = 0
        val evaluatedWords = targetTokens.map { token ->
            val cleanToken = token.lowercase().replace(Regex("[^a-z0-9]"), "")
            val isMatched = cleanToken.isNotBlank() && cleanToken in spokenNormalizedWords
            if (isMatched) matchedCount++
            EvaluatedWord(text = token, isMatched = isMatched)
        }

        val score = if (targetTokens.isNotEmpty()) {
            ((matchedCount.toDouble() / targetTokens.size.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        val feedbackMessage = when {
            score >= 85 -> "완벽에 가까운 AL 레벨 발화입니다! 👏"
            score >= 70 -> "핵심 표현을 훌륭하게 구사하셨습니다! 👍"
            score >= 50 -> "좋은 시도입니다. 주요 담화 표지를 보강해보세요! 💪"
            else -> "한국어 힌트의 어순에 맞춰 다시 한번 연습해보세요! 🎯"
        }

        return SpeakingEvaluationResult(
            spokenText = trimmedSpoken,
            accuracyScore = score,
            evaluatedWords = evaluatedWords,
            feedbackMessage = feedbackMessage
        )
    }
}
