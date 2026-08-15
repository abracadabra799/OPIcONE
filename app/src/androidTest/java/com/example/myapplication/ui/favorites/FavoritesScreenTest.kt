package com.example.myapplication.ui.favorites

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun deletingAFavorite_removesItFromTheList() = runBlocking {
        val repository = FavoriteRepository(database.favoriteDao())
        repository.addFavorite(PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "힌트",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        ))
        val viewModel = FavoritesViewModel(repository)

        composeTestRule.setContent {
            FavoritesScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("I live in Seoul.").assertExists()
        composeTestRule.onNodeWithText("삭제").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule.onAllNodesWithText("I live in Seoul.").fetchSemanticsNodes().isEmpty()
        }
    }
}
