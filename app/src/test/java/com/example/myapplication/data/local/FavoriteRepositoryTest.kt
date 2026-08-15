package com.example.myapplication.data.local

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteRepositoryTest {

    private val question = PracticeQuestion(
        opicQuestion = "Tell me about your home.",
        koreanHint = "힌트",
        englishSentence = "I live in Seoul.",
        category = PracticeCategory.HOUSING
    )

    @Test
    fun addFavorite_copiesOpicQuestion() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())

        repository.addFavorite(question)

        val stored = repository.observeFavorites().first().single()
        assertEquals("Tell me about your home.", stored.opicQuestion)
    }

    @Test
    fun getFavorite_returnsStoredFavoriteById() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(question)
        val id = repository.observeFavorites().first().single().id

        val stored = repository.getFavorite(id)

        assertEquals("Tell me about your home.", stored?.opicQuestion)
    }
}
