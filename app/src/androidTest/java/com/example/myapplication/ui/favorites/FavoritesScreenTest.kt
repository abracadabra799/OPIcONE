package com.example.myapplication.ui.favorites

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class FakeSpeechPlayer(
    initialAvailability: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initialAvailability)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    val spoken = mutableListOf<String>()

    override fun speak(text: String) {
        spoken += text
    }

    override fun release() = Unit
}

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
        val viewModel = FavoritesViewModel(repository, FakeSpeechPlayer())

        composeTestRule.setContent {
            FavoritesScreen(viewModel = viewModel, onPracticeFavorite = {})
        }

        composeTestRule.onNodeWithText("I live in Seoul.").assertExists()
        composeTestRule.onNodeWithText("삭제").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule.onAllNodesWithText("I live in Seoul.").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun favoriteCard_showsQuestionAndInvokesPlaybackAndPracticeActions() = runBlocking {
        val repository = FavoriteRepository(database.favoriteDao())
        repository.addFavorite(PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "힌트",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        ))
        val favoriteId = repository.observeFavorites().first().single().id
        val player = FakeSpeechPlayer()
        val viewModel = FavoritesViewModel(repository, player)
        var practiceId: Long? = null

        composeTestRule.setContent {
            FavoritesScreen(
                viewModel = viewModel,
                onPracticeFavorite = { practiceId = it }
            )
        }

        composeTestRule.onNodeWithText("Tell me about your home.").assertExists()
        composeTestRule.onNodeWithText("바로 듣기").performClick()
        composeTestRule.onNodeWithText("전체 연습").performClick()

        assertEquals(listOf("I live in Seoul."), player.spoken)
        assertEquals(favoriteId, practiceId)
    }

    @Test
    fun legacyFavorite_showsExactQuestionFallback() = runBlocking {
        database.favoriteDao().insert(
            FavoriteSentence(
                category = PracticeCategory.HOUSING.name,
                opicQuestion = null,
                koreanHint = "힌트",
                englishSentence = "Legacy answer.",
                createdAt = 1
            )
        )
        val viewModel = FavoritesViewModel(
            FavoriteRepository(database.favoriteDao()),
            FakeSpeechPlayer()
        )

        composeTestRule.setContent {
            FavoritesScreen(viewModel = viewModel, onPracticeFavorite = {})
        }

        composeTestRule.onNodeWithText(
            "저장된 질문 없음 — 힌트를 보고 답해보세요."
        ).assertExists()
        Unit
    }

    @Test
    fun unavailableSpeech_disablesInstantPlaybackAndShowsGuidance() = runBlocking {
        val repository = FavoriteRepository(database.favoriteDao())
        repository.addFavorite(PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "힌트",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        ))
        val player = FakeSpeechPlayer(SpeechAvailability.Unavailable)
        val viewModel = FavoritesViewModel(repository, player)

        composeTestRule.setContent {
            FavoritesScreen(viewModel = viewModel, onPracticeFavorite = {})
        }

        composeTestRule.onNodeWithText("바로 듣기").assertIsNotEnabled()
        composeTestRule.onNodeWithText(
            "이 기기에서는 영어 음성 재생을 사용할 수 없습니다."
        ).assertExists()
        assertTrue(player.spoken.isEmpty())
    }
}
