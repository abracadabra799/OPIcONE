package com.example.myapplication.ui.set

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SetCompleteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tappingBackToHome_invokesCallback() {
        var backPressed = false
        composeTestRule.setContent {
            SetCompleteScreen(questionCount = 5, onBackToHome = { backPressed = true })
        }

        composeTestRule.onNodeWithText("오늘 5문장을 연습했습니다. 수고하셨습니다!").assertExists()
        composeTestRule.onNodeWithText("홈으로").performClick()

        assertTrue(backPressed)
    }
}
