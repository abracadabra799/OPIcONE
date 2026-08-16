package com.example.myapplication.ui.set

import com.example.myapplication.data.session.CompletedPracticeItem

sealed interface SetCompleteUiState {
    data object Empty : SetCompleteUiState

    data class Summary(
        val items: List<CompletedPracticeItem>,
        val favoriteCount: Int
    ) : SetCompleteUiState
}
