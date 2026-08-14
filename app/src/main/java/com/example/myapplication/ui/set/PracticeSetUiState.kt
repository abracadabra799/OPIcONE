package com.example.myapplication.ui.set

import com.example.myapplication.data.model.PracticeQuestion

sealed interface PracticeSetUiState {
    data object Loading : PracticeSetUiState

    data class Error(val message: String) : PracticeSetUiState

    data class Ready(
        val questions: List<PracticeQuestion>,
        val currentIndex: Int,
        val isRecording: Boolean,
        val hasRecording: Boolean,
        val isCurrentFavorite: Boolean,
        val isSetComplete: Boolean
    ) : PracticeSetUiState
}
