package com.example.myapplication.ui.favorites

import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.data.local.FavoriteSentence

sealed interface FavoritePracticeUiState {
    data object Loading : FavoritePracticeUiState

    data object NotFound : FavoritePracticeUiState

    data class Ready(
        val favorite: FavoriteSentence,
        val isRecording: Boolean = false,
        val hasRecording: Boolean = false,
        val isComplete: Boolean = false,
        val speechAvailability: SpeechAvailability,
        val evaluationResult: com.example.myapplication.audio.SpeakingEvaluationResult? = null,
        val isAnswerRevealed: Boolean = false
    ) : FavoritePracticeUiState
}
