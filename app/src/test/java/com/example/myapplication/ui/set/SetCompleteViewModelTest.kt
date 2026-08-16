package com.example.myapplication.ui.set

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.session.CompletedPracticeItem
import com.example.myapplication.data.session.CompletedPracticeSet
import com.example.myapplication.data.session.PracticeSessionStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetCompleteViewModelTest {

    @Test
    fun `summary exposes items and favorite count`() {
        val expectedItems = listOf(
            CompletedPracticeItem(QUESTION_1, true),
            CompletedPracticeItem(QUESTION_2, false)
        )
        val store = PracticeSessionStore().apply {
            saveCompletedSet(CompletedPracticeSet(expectedItems))
        }

        val state = SetCompleteViewModel(store).uiState.value as SetCompleteUiState.Summary

        assertEquals(expectedItems, state.items)
        assertEquals(1, state.favoriteCount)
    }

    @Test
    fun `missing snapshot exposes empty state`() {
        val state = SetCompleteViewModel(PracticeSessionStore()).uiState.value

        assertTrue(state is SetCompleteUiState.Empty)
    }

    private companion object {
        val QUESTION_1 = PracticeQuestion(
            opicQuestion = "Tell me about item 1.",
            koreanHint = "힌트1",
            englishSentence = "Sentence one.",
            category = PracticeCategory.HOBBY
        )
        val QUESTION_2 = PracticeQuestion(
            opicQuestion = "Tell me about item 2.",
            koreanHint = "힌트2",
            englishSentence = "Sentence two.",
            category = PracticeCategory.HOBBY
        )
    }
}
