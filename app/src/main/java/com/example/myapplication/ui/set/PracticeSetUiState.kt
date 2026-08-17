package com.example.myapplication.ui.set

import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.data.model.PracticeQuestion

sealed interface PracticeSetUiState {
    data object Loading : PracticeSetUiState

    data class Error(
        val message: String,
        val showSettingsAction: Boolean = false
    ) : PracticeSetUiState

    data class Ready(
        val questions: List<PracticeQuestion>,
        val currentIndex: Int,
        val isRecording: Boolean,
        val hasRecording: Boolean,
        val favoriteQuestionKeys: Set<String>,
        val speechAvailability: SpeechAvailability,
        val isSetComplete: Boolean,
        val evaluationResult: com.example.myapplication.audio.SpeakingEvaluationResult? = null,
        val isAnswerRevealed: Boolean = false
    ) : PracticeSetUiState {
        val isCurrentFavorite: Boolean
            get() = questions[currentIndex].englishSentence in favoriteQuestionKeys
    }
}
