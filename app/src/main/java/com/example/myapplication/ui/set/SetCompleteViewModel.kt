package com.example.myapplication.ui.set

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.session.PracticeSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SetCompleteViewModel(
    sessionStore: PracticeSessionStore
) : ViewModel() {
    private val initialState = sessionStore.lastCompletedSet()?.let { completedSet ->
        SetCompleteUiState.Summary(
            items = completedSet.items,
            favoriteCount = completedSet.items.count { it.isFavorite }
        )
    } ?: SetCompleteUiState.Empty

    val uiState: StateFlow<SetCompleteUiState> = MutableStateFlow(initialState)
}
