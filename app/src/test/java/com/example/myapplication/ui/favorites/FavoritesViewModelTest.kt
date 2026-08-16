package com.example.myapplication.ui.favorites

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FakeSpeechPlayer(
    initialAvailability: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initialAvailability)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    val spoken = mutableListOf<String>()
    var released = false

    override fun speak(text: String) {
        spoken += text
    }

    override fun release() {
        released = true
    }
}

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `favorites reflects what is stored in the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "힌트",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        ))
        val viewModel = FavoritesViewModel(repository, FakeSpeechPlayer())

        advanceUntilIdle()

        assertEquals(1, viewModel.favorites.value.size)
        assertEquals("I live in Seoul.", viewModel.favorites.value[0].englishSentence)
    }

    @Test
    fun `removeFavorite deletes the item from the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion(
            opicQuestion = "Tell me about your home.",
            koreanHint = "힌트",
            englishSentence = "I live in Seoul.",
            category = PracticeCategory.HOUSING
        ))
        val viewModel = FavoritesViewModel(repository, FakeSpeechPlayer())
        advanceUntilIdle()
        val stored = viewModel.favorites.value.first()

        viewModel.removeFavorite(stored)
        advanceUntilIdle()

        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `playFavorite speaks selected English sentence when available`() = runTest {
        val player = FakeSpeechPlayer(SpeechAvailability.Available)
        val viewModel = FavoritesViewModel(FavoriteRepository(FakeFavoriteDao()), player)

        viewModel.playFavorite(favorite())

        assertEquals(listOf("Answer."), player.spoken)
    }

    @Test
    fun `playFavorite does not speak when unavailable`() = runTest {
        val player = FakeSpeechPlayer(SpeechAvailability.Unavailable)
        val viewModel = FavoritesViewModel(FavoriteRepository(FakeFavoriteDao()), player)

        viewModel.playFavorite(favorite())

        assertTrue(player.spoken.isEmpty())
        assertEquals(SpeechAvailability.Unavailable, viewModel.speechAvailability.value)
    }

    @Test
    fun `clearing ViewModel releases speech player`() {
        val player = FakeSpeechPlayer()
        val viewModel = FavoritesViewModel(FavoriteRepository(FakeFavoriteDao()), player)

        viewModel.clearForTest()

        assertTrue(player.released)
    }

    private fun favorite() = FavoriteSentence(
        id = 1,
        category = "HOUSING",
        opicQuestion = "Question",
        koreanHint = "힌트",
        englishSentence = "Answer.",
        createdAt = 1
    )
}
