package com.example.myapplication.ui.set

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.session.CompletedPracticeItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SetCompleteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun summary_showsQuestionsSentencesAndFavoriteCount() {
        composeTestRule.setContent {
            SetCompleteScreen(
                state = SetCompleteUiState.Summary(
                    items = listOf(
                        CompletedPracticeItem(QUESTION_1, true),
                        CompletedPracticeItem(QUESTION_2, false)
                    ),
                    favoriteCount = 1
                ),
                onBackToHome = {}
            )
        }

        composeTestRule.onNodeWithText("오늘 2문장을 연습했습니다.").assertExists()
        composeTestRule.onNodeWithText("즐겨찾기 1개").assertExists()
        composeTestRule.onNodeWithText(QUESTION_1.opicQuestion).assertExists()
        composeTestRule.onNodeWithText(QUESTION_1.koreanHint).assertExists()
        composeTestRule.onNodeWithText(QUESTION_1.englishSentence).assertExists()
        composeTestRule.onNodeWithText(QUESTION_2.opicQuestion).assertExists()
        composeTestRule.onNodeWithText(QUESTION_2.koreanHint).assertExists()
        composeTestRule.onNodeWithText(QUESTION_2.englishSentence).assertExists()
        composeTestRule.onAllNodesWithText("★ 즐겨찾기").assertCountEquals(1)
    }

    @Test
    fun emptyState_showsMessageAndReturnsHome() {
        var backPressed = false
        composeTestRule.setContent {
            SetCompleteScreen(
                state = SetCompleteUiState.Empty,
                onBackToHome = { backPressed = true }
            )
        }

        composeTestRule.onNodeWithText("완료한 연습 세트가 없습니다.").assertExists()
        composeTestRule.onNodeWithText("홈으로").performClick()

        assertTrue(backPressed)
    }

    private companion object {
        val QUESTION_1 = PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "나는 산다 / 서울에",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        )
        val QUESTION_2 = PracticeQuestion(
            opicQuestion = "What do you like about your neighborhood?",
            koreanHint = "우리 동네는 / 조용하다",
            englishSentence = "My neighborhood is quiet.",
            category = PracticeCategory.HOUSING
        )
    }
}
