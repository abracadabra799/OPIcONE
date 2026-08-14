package com.example.myapplication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchingTheApp_showsTheHomeScreenTitle() {
        composeTestRule.onNodeWithText("오픽 스피킹 연습").assertExists()
    }
}
