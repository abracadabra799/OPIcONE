package com.example.myapplication.ui.favorites

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `favorites reflects what is stored in the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion("힌트", "I live in Seoul.", PracticeCategory.HOUSING))
        val viewModel = FavoritesViewModel(repository)

        advanceUntilIdle()

        assertEquals(1, viewModel.favorites.value.size)
        assertEquals("I live in Seoul.", viewModel.favorites.value[0].englishSentence)
    }

    @Test
    fun `removeFavorite deletes the item from the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion("힌트", "I live in Seoul.", PracticeCategory.HOUSING))
        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()
        val stored = viewModel.favorites.value.first()

        viewModel.removeFavorite(stored)
        advanceUntilIdle()

        assertTrue(viewModel.favorites.value.isEmpty())
    }
}
