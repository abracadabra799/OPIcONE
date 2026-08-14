package com.example.myapplication.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tappingACategory_invokesCallbackWithThatCategory() {
        var selected: PracticeCategory? = null
        composeTestRule.setContent {
            HomeScreen(
                onCategorySelected = { selected = it },
                onOpenFavorites = {},
                onOpenSettings = {}
            )
        }

        composeTestRule.onNodeWithText(PracticeCategory.HOBBY.koreanLabel).performClick()

        assertEquals(PracticeCategory.HOBBY, selected)
    }

    @Test
    fun tappingFavoritesButton_invokesCallback() {
        var favoritesOpened = false
        composeTestRule.setContent {
            HomeScreen(
                onCategorySelected = {},
                onOpenFavorites = { favoritesOpened = true },
                onOpenSettings = {}
            )
        }

        composeTestRule.onNodeWithText("즐겨찾기").performClick()

        assertTrue(favoritesOpened)
    }
}
